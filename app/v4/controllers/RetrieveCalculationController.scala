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

package v4.controllers

import shared.utils.{IdGenerator, Logging}
import shared.controllers._
import shared.services.{AuditService, EnrolmentsAuthService, MtdIdLookupService}
import shared.config.AppConfig
import config.FeatureSwitches
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import shared.routing.Version
import v4.controllers.validators.RetrieveCalculationValidatorFactory
import v4.models.response.retrieveCalculation.RetrieveCalculationResponse
import v4.services.RetrieveCalculationService

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class RetrieveCalculationController @Inject() (val authService: EnrolmentsAuthService,
                                               val lookupService: MtdIdLookupService,
                                               validatorFactory: RetrieveCalculationValidatorFactory,
                                               service: RetrieveCalculationService,
                                               auditService: AuditService,
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

  import featureSwitches.{isCl290Enabled, isR8bSpecificApiEnabled, isRetrieveSAAdditionalFieldsEnabled}

  def retrieveCalculation(nino: String, taxYear: String, calculationId: String): Action[AnyContent] =
    authorisedAction(nino).async { implicit request =>
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
            apiVersion = Version(request),
            params = Map("nino" -> nino, "calculationId" -> calculationId, "taxYear" -> taxYear),
            includeResponse = true
          ))
          .withModelHandling { response: RetrieveCalculationResponse =>
            val responseMaybeWithoutR8b              = updateModelR8b(response)
            val responseMaybeWithoutAdditionalFields = updateModelAdditionalFields(responseMaybeWithoutR8b)
            updateModelCl290(responseMaybeWithoutAdditionalFields)
          }
          .withPlainJsonResult()

      requestHandler.handleRequest()

    }

  private def updateModelR8b(response: RetrieveCalculationResponse): RetrieveCalculationResponse =
    if (isR8bSpecificApiEnabled) response else response.withoutR8bSpecificUpdates

  private def updateModelAdditionalFields(response: RetrieveCalculationResponse): RetrieveCalculationResponse =
    if (isRetrieveSAAdditionalFieldsEnabled) response else response.withoutAdditionalFieldsUpdates

  private def updateModelCl290(response: RetrieveCalculationResponse): RetrieveCalculationResponse =
    if (isCl290Enabled) response else response.withoutTaxTakenOffTradingIncome

}
