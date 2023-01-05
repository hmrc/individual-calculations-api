/*
 * Copyright 2023 HM Revenue & Customs
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

package v2.controllers

import play.api.libs.json.JsValue
import play.api.mvc._
import utils.IdGenerator
import v2.connectors.httpparsers.StandardHttpParser.SuccessCode
import v2.controllers.requestParsers.TriggerCalculationParser
import v2.handler.{AuditHandler, RequestDefn, RequestHandler}
import v2.hateoas.HateoasFactory
import v2.models.audit.GenericAuditDetail
import v2.models.errors._
import v2.models.hateoas.HateoasWrapper
import v2.models.request.{TriggerCalculationRawData, TriggerCalculationRequest}
import v2.models.response.triggerCalculation.{TriggerCalculationHateoasData, TriggerCalculationResponse}
import v2.services.{AuditService, EnrolmentsAuthService, MtdIdLookupService, StandardService}

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class TriggerCalculationController @Inject() (authService: EnrolmentsAuthService,
                                              lookupService: MtdIdLookupService,
                                              triggerCalculationParser: TriggerCalculationParser,
                                              service: StandardService,
                                              hateoasFactory: HateoasFactory,
                                              auditService: AuditService,
                                              cc: ControllerComponents,
                                              idGenerator: IdGenerator)(implicit val ec: ExecutionContext)
    extends StandardController[
      TriggerCalculationRawData,
      TriggerCalculationRequest,
      TriggerCalculationResponse,
      HateoasWrapper[TriggerCalculationResponse],
      JsValue](authService, lookupService, triggerCalculationParser, service, auditService, cc, idGenerator) {
  controller =>

  implicit val endpointLogContext: EndpointLogContext =
    EndpointLogContext(
      controllerName = "TriggerCalculationController",
      endpointName = "triggerCalculation"
    )

  override val successCode: SuccessCode = SuccessCode(ACCEPTED)

  override def requestHandlerFor(
      playRequest: Request[JsValue],
      req: TriggerCalculationRequest): RequestHandler[TriggerCalculationResponse, HateoasWrapper[TriggerCalculationResponse]] = {
    RequestHandler[TriggerCalculationResponse](RequestDefn.Post(playRequest.path, playRequest.body))
      .withPassThroughErrors(
        NinoFormatError,
        TaxYearFormatError,
        RuleTaxYearNotSupportedError,
        RuleTaxYearRangeInvalidError,
        DownstreamError,
        RuleNoIncomeSubmissionsExistError,
        RuleIncorrectOrEmptyBodyError,
        RuleIncorrectGovTestScenarioError
      )
      .withRequestSuccessCode(ACCEPTED)
      .mapSuccessSimple(rawResponse => hateoasFactory.wrap(rawResponse, TriggerCalculationHateoasData(req.nino.nino, rawResponse.id)))

  }

  def triggerCalculation(nino: String): Action[JsValue] = authorisedAction(nino).async(parse.json) { implicit request =>
    val rawData = TriggerCalculationRawData(nino, AnyContentAsJson(request.body))

    val auditHandler: AuditHandler[GenericAuditDetail] = AuditHandler.withBody(
      "triggerASelfAssessmentTaxCalculation",
      "trigger-a-self-assessment-tax-calculation",
      Map("nino" -> nino),
      request
    )

    doHandleRequest(rawData, Some(auditHandler))
  }

}
