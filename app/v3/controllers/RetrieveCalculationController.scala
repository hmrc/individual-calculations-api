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

import cats.data.EitherT
import cats.implicits._
import play.api.libs.json.Json
import play.api.mvc._
import utils.{IdGenerator, Logging}
import v3.controllers.requestParsers.RetrieveCalculationParser
import v3.hateoas.HateoasFactory
import v3.models.errors._
import v3.models.request.RetrieveCalculationRawData
import v3.models.response.retrieveCalculation.RetrieveCalculationHateoasData
import v3.services.{EnrolmentsAuthService, MtdIdLookupService, RetrieveCalculationService}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class RetrieveCalculationController @Inject() (val authService: EnrolmentsAuthService,
                                               val lookupService: MtdIdLookupService,
                                               retrieveCalculationParser: RetrieveCalculationParser,
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

  def retrieveCalculation(nino: String, taxYear: String, calculationId: String): Action[AnyContent] =
    authorisedAction(nino).async { implicit request =>
      implicit val correlationId: String = idGenerator.getCorrelationId
      logger.info(
        s"[${endpointLogContext.controllerName}][${endpointLogContext.endpointName}] " +
          s"with CorrelationId: $correlationId")

      val rawData = RetrieveCalculationRawData(nino = nino, taxYear = taxYear, calculationId = calculationId)

      val result =
        for {
          parsedRequest <- EitherT.fromEither[Future](retrieveCalculationParser.parseRequest(rawData))
          response      <- EitherT(service.retrieveCalculation(parsedRequest))
          hateoasResponse <- EitherT.fromEither[Future](
            hateoasFactory
              .wrap(
                response.responseData,
                RetrieveCalculationHateoasData(nino = nino, taxYear = taxYear, calculationId = calculationId, response = response.responseData)
              )
              .asRight[ErrorWrapper]
          )
        } yield {
          logger.info(
            s"[${endpointLogContext.controllerName}][${endpointLogContext.endpointName}] - " +
              s"Success response received with correlationId: ${response.correlationId}"
          )

          Ok(Json.toJson(hateoasResponse))
            .withApiHeaders(response.correlationId)
        }
      result.leftMap { errorWrapper =>
        val resCorrelationId = errorWrapper.correlationId
        val result           = errorResult(errorWrapper).withApiHeaders(resCorrelationId)
        logger.info(
          s"[${endpointLogContext.controllerName}][${endpointLogContext.endpointName}] - " +
            s"Error response received with CorrelationId: $resCorrelationId")

        result
      }.merge
    }

  private def errorResult(errorWrapper: ErrorWrapper) =
    errorWrapper.error match {
      case _
          if errorWrapper.containsAnyOf(
            BadRequestError,
            NinoFormatError,
            TaxYearFormatError,
            RuleTaxYearRangeInvalidError,
            RuleTaxYearNotSupportedError,
            CalculationIdFormatError,
            RuleIncorrectGovTestScenarioError
          ) =>
        BadRequest(Json.toJson(errorWrapper))
      case NotFoundError   => NotFound(Json.toJson(errorWrapper))
      case DownstreamError => InternalServerError(Json.toJson(errorWrapper))
      case _               => unhandledError(errorWrapper)
    }

}
