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

package v3.controllers

import config.{AppConfig, FeatureSwitches}
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import utils.{IdGenerator, Logging}
import v3.controllers.requestParsers.RetrieveCalculationParser
import v3.hateoas.HateoasFactory
import v3.models.request.RetrieveCalculationRawData
import v3.models.response.retrieveCalculation.{RetrieveCalculationHateoasData, RetrieveCalculationResponse}
import v3.services.{EnrolmentsAuthService, MtdIdLookupService, RetrieveCalculationService}

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class RetrieveCalculationController @Inject() (val authService: EnrolmentsAuthService,
                                               val lookupService: MtdIdLookupService,
                                               appConfig: AppConfig,
                                               parser: RetrieveCalculationParser,
                                               service: RetrieveCalculationService,
                                               hateoasFactory: HateoasFactory,
                                               cc: ControllerComponents,
                                               val idGenerator: IdGenerator)(implicit val ec: ExecutionContext)
    extends AuthorisedController(cc)
    with BaseController
    with Logging {

  implicit val endpointLogContext: EndpointLogContext =
    EndpointLogContext(
      controllerName = "RetrieveCalculationController",
      endpointName = "retrieveCalculation"
    )

  private val featureSwitches = FeatureSwitches()(appConfig)

  def retrieveCalculation(nino: String, taxYear: String, calculationId: String): Action[AnyContent] =
    authorisedAction(nino).async { implicit request =>
      implicit val ctx: RequestContext = RequestContext.from(idGenerator, endpointLogContext)

      val rawData =
        RetrieveCalculationRawData(
          nino = nino,
          taxYear = taxYear,
          calculationId = calculationId,
          featureSwitches.isR8bSpecificApiEnabled
        )

      val requestHandler =
        RequestHandler
          .withParser(parser)
          .withService(service.retrieveCalculation)
          .withModelHandling { response: RetrieveCalculationResponse =>
            if (featureSwitches.isR8bSpecificApiEnabled)
              response
            else
              (response.withoutBasicExtension).withoutTotalAllowanceAndDeductions
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

      requestHandler.handleRequest(rawData)

    }

}
