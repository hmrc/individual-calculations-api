/*
 * Copyright 2019 HM Revenue & Customs
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
import play.api.libs.json.JsValue
import play.api.mvc.{Action, AnyContent, ControllerComponents, Request}
import v1.connectors.httpparsers.StandardHttpParser
import v1.connectors.httpparsers.StandardHttpParser.SuccessCode
import v1.controllers.requestParsers.GetCalculationMessagesParser
import v1.handler.{AuditHandler, RequestDefn, RequestHandler}
import v1.hateoas.HateoasFactory
import v1.models.audit.{AuditError, AuditResponse, GetCalculationAuditDetail}
import v1.models.errors._
import v1.models.hateoas.HateoasWrapper
import v1.models.outcomes.ResponseWrapper
import v1.models.request.{GetCalculationMessagesRawData, GetCalculationMessagesRequest, MessageType}
import v1.models.response.getCalculationMessages.{CalculationMessages, CalculationMessagesHateoasData}
import v1.services.{AuditService, EnrolmentsAuthService, MtdIdLookupService, StandardService}
import v1.support.MessagesFilter

import scala.concurrent.ExecutionContext

class GetCalculationMessagesController @Inject()(
    authService: EnrolmentsAuthService,
    lookupService: MtdIdLookupService,
    parser: GetCalculationMessagesParser,
    service: StandardService,
    hateoasFactory: HateoasFactory,
    auditService: AuditService,
    cc: ControllerComponents
)(implicit ec: ExecutionContext)
    extends StandardController[GetCalculationMessagesRawData,
                               GetCalculationMessagesRequest,
                               CalculationMessages,
                               HateoasWrapper[CalculationMessages],
                               AnyContent](authService, lookupService, parser, service, auditService, cc)
    with MessagesFilter {
  controller =>

  implicit val endpointLogContext: EndpointLogContext =
    EndpointLogContext(controllerName = "GetCalculationMessagesController", endpointName = "getMessages")
  override val successCode: StandardHttpParser.SuccessCode = SuccessCode(OK)

  override def requestHandlerFor(playRequest: Request[AnyContent],
                                 req: GetCalculationMessagesRequest): RequestHandler[CalculationMessages, HateoasWrapper[CalculationMessages]] =
    RequestHandler[CalculationMessages](RequestDefn.Get(req.backendCalculationUri))
      .withPassThroughErrors(
        NinoFormatError,
        CalculationIdFormatError,
        TypeFormatError,
        NotFoundError
      )
      .mapSuccess(filterMessages(req.queryData))
      .mapSuccessSimple(rawResponse => hateoasFactory.wrap(rawResponse, CalculationMessagesHateoasData(req.nino.nino, req.calculationId)))

  def getMessages(nino: String, calculationId: String): Action[AnyContent] =
    authorisedAction(nino).async { implicit request =>
      val rawData = GetCalculationMessagesRawData(nino, calculationId, request.queryString.getOrElse("type", Seq()))

      val auditHandling = AuditHandler(
        "retrieveSelfAssessmentTaxCalculationMessages",
        "retrieve-self-assessment-tax-calculation-messages",
        eventFactory = (correlationId: String, auditResponse: AuditResponse) =>
          GetCalculationAuditDetail(request.userDetails,
            nino, calculationId,
            correlationId,
            auditResponse)
      )

      doHandleRequest(rawData, Some(auditHandling))
    }

  def filterMessages(queries: Seq[MessageType])(
      messagesResponse: ResponseWrapper[CalculationMessages]): Either[ErrorWrapper, ResponseWrapper[CalculationMessages]] = {
    val filteredResponse = messagesResponse.map(messages => filter(messages, queries))
    if (filteredResponse.responseData.hasMessages) {
      Right(filteredResponse)
    } else {
      Left(ErrorWrapper(Some(filteredResponse.correlationId), MtdErrors(NOT_FOUND, NoMessagesExistError)))
    }
  }
}
