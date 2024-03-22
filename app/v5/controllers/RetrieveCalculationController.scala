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

package v5.controllers

import api.controllers._
import api.hateoas.HateoasFactory
import api.models.domain.TaxYear
import api.services.{AuditService,EnrolmentsAuthService, MtdIdLookupService}
import config.{AppConfig, FeatureSwitches}
import play.api.libs.json.JsValue
import play.api.mvc.{Action, ControllerComponents}
import utils.{IdGenerator, Logging}
import routing.{Version, Version5}
import v5.controllers.validators.RetrieveCalculationValidatorFactory
import v5.models.response.retrieveCalculation.{RetrieveCalculationHateoasData, RetrieveCalculationResponse}
import v5.services.RetrieveCalculationService

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class RetrieveCalculationController @Inject() (val authService: EnrolmentsAuthService,
                                               val lookupService: MtdIdLookupService,
                                               validatorFactory: RetrieveCalculationValidatorFactory,
                                               service: RetrieveCalculationService,
                                               auditService: AuditService,
                                               hateoasFactory: HateoasFactory,
                                               cc: ControllerComponents,
                                               val idGenerator: IdGenerator)(implicit val ec: ExecutionContext, appConfig: AppConfig)
    extends AuthorisedController(cc)
    with Logging {

  implicit val endpointLogContext: EndpointLogContext =
    EndpointLogContext(
      controllerName = "RetrieveCalculationController",
      endpointName = "retrieveCalculation"
    )

  private val featureSwitches = FeatureSwitches()(appConfig)

  import featureSwitches.{isBasicRateDivergenceEnabled, isCl290Enabled, isR8bSpecificApiEnabled, isRetrieveSAAdditionalFieldsEnabled}

  def retrieveCalculation(nino: String, taxYear: String, calculationId: String): Action[JsValue] =
    authorisedAction(nino).async(parse.json) { implicit request =>
      implicit val ctx: RequestContext = RequestContext.from(idGenerator, endpointLogContext)

      val validator = validatorFactory.validator(
        nino = nino,
        taxYear = taxYear,
        calculationId = calculationId
      )

      val requestHandler =
        RequestHandler
          .withValidator(validator)
          .withService(service.retrieveCalculation)
          .withAuditing(AuditHandler(
            auditService,
            auditType = "RetrieveATaxCalculation",
            transactionName = "retrieve-a-tax-calculation",
            apiVersion = Version.from(request, orElse = Version5),
            params = Map("nino" -> nino, "calculationId" -> calculationId, "taxYear" -> taxYear),
            Some(request.body)
          ))
          .withModelHandling { response: RetrieveCalculationResponse =>
            val responseMaybeWithoutR8b              = updateModelR8b(response)
            val responseMaybeWithoutAdditionalFields = updateModelAdditionalFields(responseMaybeWithoutR8b)
            val responseMaybeWithoutCl290            = updateModelCl290(responseMaybeWithoutAdditionalFields)
            updateModelBasicRateDivergence(taxYear, responseMaybeWithoutCl290)
          }
          .withHateoasResultFrom(hateoasFactory) { (request, response) =>
            {
              RetrieveCalculationHateoasData(
                nino = nino,
                taxYear = request.taxYear,
                calculationId = calculationId,
                response = response
              )
            }
          }

      requestHandler.handleRequest()

    }

  private def updateModelR8b(response: RetrieveCalculationResponse): RetrieveCalculationResponse =
    if (isR8bSpecificApiEnabled) response else response.withoutR8bSpecificUpdates

  private def updateModelAdditionalFields(response: RetrieveCalculationResponse): RetrieveCalculationResponse =
    if (isRetrieveSAAdditionalFieldsEnabled) response else response.withoutAdditionalFieldsUpdates

  private def updateModelCl290(response: RetrieveCalculationResponse): RetrieveCalculationResponse =
    if (isCl290Enabled) response else response.withoutTaxTakenOffTradingIncome

  private def updateModelBasicRateDivergence(taxYear: String, response: RetrieveCalculationResponse): RetrieveCalculationResponse = {
    if (isBasicRateDivergenceEnabled && TaxYear.fromMtd(taxYear).is2025) response else response.withoutBasicRateDivergenceUpdates
  }

}
