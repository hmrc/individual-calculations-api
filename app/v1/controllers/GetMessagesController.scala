/*
 * Copyright 2020 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package v1.controllers

import javax.inject.Inject
import play.api.mvc.{Action, AnyContent, ControllerComponents, Request}
import v1.connectors.httpparsers.StandardHttpParser
import v1.connectors.httpparsers.StandardHttpParser.SuccessCode
import v1.controllers.requestParsers.GetMessagesParser
import v1.handler.{AuditHandler, RequestDefn, RequestHandler}
import v1.hateoas.HateoasFactory
import v1.models.audit.GenericAuditDetail
import v1.models.domain.MessageType
import v1.models.errors._
import v1.models.hateoas.HateoasWrapper
import v1.models.outcomes.ResponseWrapper
import v1.models.request.{GetMessagesRawData, GetMessagesRequest}
import v1.models.response.getMessages.{MessagesHateoasData, MessagesResponse}
import v1.services.{AuditService, EnrolmentsAuthService, MtdIdLookupService, StandardService}
import v1.support.MessagesFilter

import scala.concurrent.ExecutionContext

class GetMessagesController @Inject()(authService: EnrolmentsAuthService,
                                      lookupService: MtdIdLookupService,
                                      parser: GetMessagesParser,
                                      service: StandardService,
                                      hateoasFactory: HateoasFactory,
                                      auditService: AuditService,
                                      cc: ControllerComponents
                                     )(implicit ec: ExecutionContext)
  extends StandardController[GetMessagesRawData,
    GetMessagesRequest,
    MessagesResponse,
    HateoasWrapper[MessagesResponse],
    AnyContent](authService, lookupService, parser, service, auditService, cc)
    with MessagesFilter {
  controller =>

  implicit val endpointLogContext: EndpointLogContext =
    EndpointLogContext(
      controllerName = "GetCalculationMessagesController",
      endpointName = "getMessages"
    )

  override val successCode: StandardHttpParser.SuccessCode = SuccessCode(OK)

  override def requestHandlerFor(playRequest: Request[AnyContent],
                                 req: GetMessagesRequest): RequestHandler[MessagesResponse, HateoasWrapper[MessagesResponse]] =
    RequestHandler[MessagesResponse](RequestDefn.Get(req.backendCalculationUri))
      .withPassThroughErrors(
        NinoFormatError,
        CalculationIdFormatError,
        TypeFormatError,
        NotFoundError
      )
      .mapSuccess(filterMessages(req.queryData))
      .mapSuccessSimple(rawResponse => hateoasFactory.wrap(rawResponse, MessagesHateoasData(req.nino.nino, req.calculationId)))

  def filterMessages(queries: Seq[MessageType])(
    messagesResponse: ResponseWrapper[MessagesResponse]): Either[ErrorWrapper, ResponseWrapper[MessagesResponse]] = {
    val filteredResponse = messagesResponse.map(messages => filter(messages, queries))
    if (filteredResponse.responseData.hasMessages) {
      Right(filteredResponse)
    } else {
      Left(ErrorWrapper(Some(filteredResponse.correlationId), MtdErrors(NOT_FOUND, NoMessagesExistError)))
    }
  }

  def getMessages(nino: String, calculationId: String): Action[AnyContent] =
    authorisedAction(nino).async { implicit request =>
      val rawData = GetMessagesRawData(nino, calculationId, request.queryString.getOrElse("type", Seq()))

      val auditHandler: AuditHandler[GenericAuditDetail] = AuditHandler.withoutBody(
        "retrieveSelfAssessmentTaxCalculationMessages",
        "retrieve-self-assessment-tax-calculation-messages",
        Map("nino" -> nino, "calculationId" -> calculationId), request
      )

      doHandleRequest(rawData, Some(auditHandler))
    }
}
