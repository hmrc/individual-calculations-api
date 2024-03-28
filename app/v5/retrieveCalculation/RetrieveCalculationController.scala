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

package v5.retrieveCalculation

import api.controllers._
import api.hateoas.HateoasFactory
import api.services.{EnrolmentsAuthService, MtdIdLookupService}
import config.{AppConfig, FeatureSwitches}
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import utils.{IdGenerator, Logging}
import v5.retrieveCalculation.models.response.RetrieveCalculationResponse

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class RetrieveCalculationController @Inject() (val authService: EnrolmentsAuthService,
                                               val lookupService: MtdIdLookupService,
                                               validatorFactory: RetrieveCalculationValidatorFactory,
                                               service: RetrieveCalculationService,
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
          .withModelHandling { response: RetrieveCalculationResponse =>
            response.adjustFields(FeatureSwitches()(appConfig), taxYear)
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

}
