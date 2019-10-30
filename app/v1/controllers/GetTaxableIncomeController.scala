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
import v1.models.audit.{AuditError, AuditResponse, GetTaxableIncomeAuditDetail}
import v1.models.errors._
import v1.models.hateoas.HateoasWrapper
import v1.models.request.{GetCalculationRawData, GetCalculationRequest}
import v1.models.response.CalculationWrapperOrError
import v1.models.response.getTaxableIncome.{TaxableIncomeHateoasData, TaxableIncomeResponse}
import v1.services.{AuditService, EnrolmentsAuthService, MtdIdLookupService, StandardService}

import scala.concurrent.ExecutionContext

class GetTaxableIncomeController @Inject()(
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
    CalculationWrapperOrError[TaxableIncomeResponse],
    HateoasWrapper[TaxableIncomeResponse],
    AnyContent](authService, lookupService, parser, service, auditService, cc) {
  controller =>

  implicit val endpointLogContext: EndpointLogContext =
    EndpointLogContext(controllerName = "GetTaxableIncomeController", endpointName = "getTaxableIncome")

  override def requestHandlingFor(playRequest: Request[AnyContent],
                                  req: GetCalculationRequest): RequestHandling[CalculationWrapperOrError[TaxableIncomeResponse], HateoasWrapper[TaxableIncomeResponse]] =
    RequestHandling[CalculationWrapperOrError[TaxableIncomeResponse]](
      RequestDefn.Get(req.backendCalculationUri))
      .withPassThroughErrors(
        NinoFormatError,
        CalculationIdFormatError,
        NotFoundError
      )
      .mapSuccess { responseWrapper =>
        responseWrapper.mapToEither[TaxableIncomeResponse] {
          case CalculationWrapperOrError.ErrorsInCalculation => Left(MtdErrors(FORBIDDEN, RuleCalculationErrorMessagesExist))
          case CalculationWrapperOrError.CalculationWrapper(calc) => Right(calc)
        }
      }
      .mapSuccessSimple(rawResponse =>
      hateoasFactory.wrap(rawResponse, TaxableIncomeHateoasData(req.nino.nino, req.calculationId)))

  override val successCode: StandardHttpParser.SuccessCode = SuccessCode(OK)

  def getTaxableIncome(nino: String, calculationId: String): Action[AnyContent] =
    authorisedAction(nino).async { implicit request =>
      val rawData = GetCalculationRawData(nino, calculationId)

      val auditHandling = AuditHandling(
        "retrieveSelfAssessmentTaxCalculationEndOfYearEstimate",
        "retrieve-self-assessment-tax-calculation-end-of-year-estimate",
        successEventFactory = (correlationId: String, status: Int, response: Option[JsValue]) =>
          GetTaxableIncomeAuditDetail(request.userDetails.userType,
            request.userDetails.agentReferenceNumber,
            nino, calculationId,
            correlationId,
            AuditResponse(status, Right(response))),
        failureEventFactory = (correlationId: String, status: Int, errors: Seq[AuditError]) =>
          GetTaxableIncomeAuditDetail(request.userDetails.userType,
            request.userDetails.agentReferenceNumber,
            nino, calculationId,
            correlationId,
            AuditResponse(status, Left(errors)))
      )

      doHandleRequest(rawData, Some(auditHandling))


    }
}
