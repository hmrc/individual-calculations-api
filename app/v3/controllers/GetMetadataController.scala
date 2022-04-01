/*
 * Copyright 2022 HM Revenue & Customs
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

package v3.controllers

import javax.inject.Inject
import play.api.mvc.{Action, AnyContent, ControllerComponents, Request}
import utils.IdGenerator
import v3.connectors.httpparsers.StandardHttpParser
import v3.connectors.httpparsers.StandardHttpParser.SuccessCode
import v3.controllers.requestParsers.GetCalculationParser
import v3.handler.{AuditHandler, RequestDefn, RequestHandler}
import v3.hateoas.HateoasFactory
import v3.models.audit.GenericAuditDetail
import v3.models.errors.{CalculationIdFormatError, NinoFormatError, NotFoundError}
import v3.models.hateoas.HateoasWrapper
import v3.models.request.{GetCalculationRawData, GetCalculationRequest}
import v3.models.response.getMetadata.{MetadataExistence, MetadataHateoasData, MetadataResponse}
import v3.services.{AuditService, EnrolmentsAuthService, MtdIdLookupService, StandardService}

import scala.concurrent.ExecutionContext

class GetMetadataController @Inject()(authService: EnrolmentsAuthService,
                                      lookupService: MtdIdLookupService,
                                      parser: GetCalculationParser,
                                      service: StandardService,
                                      hateoasFactory: HateoasFactory,
                                      auditService: AuditService,
                                      cc: ControllerComponents,
                                      idGenerator: IdGenerator,
                                     )(implicit ec: ExecutionContext)
  extends StandardController[GetCalculationRawData,
    GetCalculationRequest,
    MetadataResponse,
    HateoasWrapper[MetadataResponse],
    AnyContent](authService, lookupService, parser, service, auditService, cc, idGenerator) {
  controller =>

  implicit val endpointLogContext: EndpointLogContext =
    EndpointLogContext(
      controllerName = "GetCalculationMetadataController",
      endpointName = "getMetadata"
    )

  override val successCode: StandardHttpParser.SuccessCode = SuccessCode(OK)

  override def requestHandlerFor(playRequest: Request[AnyContent],
                                 req: GetCalculationRequest): RequestHandler[MetadataResponse, HateoasWrapper[MetadataResponse]] =
    RequestHandler[MetadataResponse](RequestDefn.Get(req.backendCalculationUri))
      .withPassThroughErrors(
        NinoFormatError,
        CalculationIdFormatError,
        NotFoundError
      )
      .mapSuccessSimple(rawResponse =>
        hateoasFactory.wrap(rawResponse.copy(metadataExistence = None), MetadataHateoasData(req.nino.nino, rawResponse.id,
          rawResponse.calculationErrorCount, rawResponse.metadataExistence.getOrElse(MetadataExistence()))))

  def getMetadata(nino: String, calculationId: String): Action[AnyContent] =
    authorisedAction(nino).async { implicit request =>
      val rawData = GetCalculationRawData(nino, calculationId)

      val auditHandler: AuditHandler[GenericAuditDetail] = AuditHandler.withoutBody(
        "retrieveSelfAssessmentTaxCalculationMetadata",
        "retrieve-self-assessment-tax-calculation-metadata",
        Map("nino" -> nino, "calculationId" -> calculationId), request
      )

      doHandleRequest(rawData, Some(auditHandler))
    }
}