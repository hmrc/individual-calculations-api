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
import v1.controllers.requestParsers.GetCalculationParser
import v1.handling.{AuditHandling, RequestDefn, RequestHandling}
import v1.hateoas.HateoasFactory
import v1.models.audit.{AuditError, AuditResponse, GetCalculationAuditDetail}
import v1.models.errors._
import v1.models.hateoas.HateoasWrapper
import v1.models.request.{GetCalculationRawData, GetCalculationRequest}
import v1.models.response.EoyEstimateWrapperOrError
import v1.models.response.getEndOfYearEstimate.{EoyEstimateResponse, EoyEstimateResponseHateoasData}
import v1.services.{AuditService, EnrolmentsAuthService, MtdIdLookupService, StandardService}

import scala.concurrent.ExecutionContext

class GetEoyEstimateController @Inject()(
    authService: EnrolmentsAuthService,
    lookupService: MtdIdLookupService,
    parser: GetCalculationParser,
    service: StandardService,
    hateoasFactory: HateoasFactory,
    auditService: AuditService,
    cc: ControllerComponents
)(implicit ec: ExecutionContext)
    extends StandardController[GetCalculationRawData,
                               GetCalculationRequest,
                               EoyEstimateWrapperOrError,
                               HateoasWrapper[EoyEstimateResponse],
                               AnyContent](authService, lookupService, parser, service, auditService, cc) {
  controller =>

  override implicit val endpointLogContext: EndpointLogContext =
    EndpointLogContext(controllerName = "GetEoyEstimateController", endpointName = "getEoyEstimate")

  override def requestHandlingFor(playRequest: Request[AnyContent],
                                  req: GetCalculationRequest): RequestHandling[EoyEstimateWrapperOrError, HateoasWrapper[EoyEstimateResponse]] = {
    RequestHandling[EoyEstimateWrapperOrError](RequestDefn.Get(req.backendCalculationUri))
      .withPassThroughErrors(
        NinoFormatError,
        CalculationIdFormatError,
        NotFoundError
      )
      .mapSuccess { responseWrapper =>
        responseWrapper.mapToEither {
          case EoyEstimateWrapperOrError.EoyErrorMessages         => Left(MtdErrors(FORBIDDEN, RuleCalculationErrorMessagesExist))
          case EoyEstimateWrapperOrError.EoyCrystallisedError     => Left(MtdErrors(NOT_FOUND, EndOfYearEstimateNotPresentError))
          case EoyEstimateWrapperOrError.EoyEstimateWrapper(calc) => Right(calc)
        }
      }
      .mapSuccessSimple(rawResponse => hateoasFactory.wrap(rawResponse, EoyEstimateResponseHateoasData(req.nino.nino, req.calculationId)))
  }

  override val successCode: StandardHttpParser.SuccessCode = SuccessCode(OK)

  def getEoyEstimate(nino: String, calculationId: String): Action[AnyContent] =
    authorisedAction(nino).async { implicit request =>
      val rawData = GetCalculationRawData(nino, calculationId)

      val auditHandling = AuditHandling(
        "retrieveSelfAssessmentTaxCalculationEndOfYearEstimate",
        "retrieve-self-assessment-tax-calculation-end-of-year-estimate",
        successEventFactory = (correlationId: String, status: Int, response: Option[JsValue]) =>
          GetCalculationAuditDetail(request.userDetails,
            nino, calculationId,
            correlationId,
            AuditResponse(status, Right(response))),
        failureEventFactory = (correlationId: String, status: Int, errors: Seq[AuditError]) =>
          GetCalculationAuditDetail(request.userDetails,
            nino, calculationId,
            correlationId,
            AuditResponse(status, Left(errors)))
      )

      doHandleRequest(rawData, Some(auditHandling))
    }
}
