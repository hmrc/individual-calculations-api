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
import v3.models.response.retrieveCalculation.RetrieveCalculationResponse
//import play.api.http.Status
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import utils.{IdGenerator, Logging}
import v3.controllers.requestParsers.RetrieveCalculationParser
import v3.hateoas.HateoasFactory
import v3.models.request.{RetrieveCalculationRawData}
import v3.models.response.retrieveCalculation.{RetrieveCalculationHateoasData}
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

  private def getBasicExtension(response: RetrieveCalculationResponse): RetrieveCalculationResponse = {
    FeatureSwitches()(appConfig).isCL249Enabled match {
      case false => {
        response.calculation match {
          case None => response
          case Some(calc) => {
            val updatedReliefs = calc.reliefs.map(reliefs => reliefs.copy(basicRateExtension = None))
            val updatedCalculation = updatedReliefs match {
              case Some(reliefs) if (reliefs.isEmpty) => calc.copy(reliefs = None)
              case _                                  => calc.copy(reliefs = updatedReliefs)
            }

            updatedCalculation match {
              case calc if (calc.isEmpty) => response.copy(calculation = None)
              case calc                   => response.copy(calculation = Some(calc))

            }
          }
        }
      }

      case _ => response
    }
  }

  private def getTotalAllowanceAndDeductions(response: RetrieveCalculationResponse): RetrieveCalculationResponse = {
    FeatureSwitches()(appConfig).isCL249Enabled match {
      case false
          if (!FeatureSwitches(appConfig.featureSwitches).isR8bSpecificApiEnabled &&
            response.calculation.exists(calc => calc.endOfYearEstimate.exists(eoy => eoy.totalAllowancesAndDeductions.isDefined))) => {
        response.copy(calculation = response.calculation.map(calc =>
          calc.copy(endOfYearEstimate = calc.endOfYearEstimate.map(x => x.copy(totalAllowancesAndDeductions = None)))))
      }
      case _ => response
    }
  }

  def retrieveCalculation(nino: String, taxYear: String, calculationId: String): Action[AnyContent] =
    authorisedAction(nino).async { implicit request =>
      implicit val ctx: RequestContext = RequestContext.from(idGenerator, endpointLogContext)

      val rawData =
        RetrieveCalculationRawData(nino = nino, taxYear = taxYear, calculationId = calculationId, FeatureSwitches()(appConfig).isCL249Enabled)

      val requestHandler =
        RequestHandler
          .withParser(parser)
          .withService(service.retrieveCalculation)
          .withHateoasResultFrom(hateoasFactory) { (request, response) =>
            {
              val res = getBasicExtension(getTotalAllowanceAndDeductions(response))

              RetrieveCalculationHateoasData(
                nino = nino,
                taxYear = request.taxYear,
                calculationId = calculationId,
                response = res
              )
            }
          }

      requestHandler.handleRequest(rawData)
    }

}
