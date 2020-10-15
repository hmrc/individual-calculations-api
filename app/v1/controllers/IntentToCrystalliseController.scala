/*
 * Copyright 2020 HM Revenue & Customs
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

import cats.data.EitherT
import cats.implicits._
import config.AppConfig
import javax.inject.Inject
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import play.mvc.Http.MimeTypes
import utils.Logging
import v1.controllers.requestParsers.IntentToCrystalliseRequestParser
import v1.hateoas.HateoasFactory
import v1.models.errors._
import v1.models.request.intentToCrystallise.IntentToCrystalliseRawData
import v1.models.response.intentToCrystallise.IntentToCrystalliseHateaosData
import v1.services.{EnrolmentsAuthService, IntentToCrystalliseService, MtdIdLookupService}

import scala.concurrent.{ExecutionContext, Future}

class IntentToCrystalliseController @Inject()(val authService: EnrolmentsAuthService,
                                              val lookupService: MtdIdLookupService,
                                              appConfig: AppConfig,
                                              requestParser: IntentToCrystalliseRequestParser,
                                              service: IntentToCrystalliseService,
                                              hateoasFactory: HateoasFactory,
                                              cc: ControllerComponents)(implicit ec: ExecutionContext)
  extends AuthorisedController(cc) with BaseController with Logging {

  implicit val endpointLogContext: EndpointLogContext =
    EndpointLogContext(
      controllerName = "IntentToCrystalliseController",
      endpointName = "intentToCrystallise"
    )

  def submitIntent(nino: String, taxYear: String): Action[AnyContent] =
    authorisedAction(nino).async { implicit request =>

      val rawData: IntentToCrystalliseRawData = IntentToCrystalliseRawData(
        nino = nino,
        taxYear = taxYear
      )

      val result =
        for {
          parsedRequest <- EitherT.fromEither[Future](requestParser.parseRequest(rawData))
          serviceResponse <- EitherT(service.submitIntent(parsedRequest))
          hateoasResponse <- EitherT.fromEither[Future](
            hateoasFactory.wrap(
              serviceResponse.responseData,
              IntentToCrystalliseHateaosData(nino, taxYear, serviceResponse.responseData.calculationId)
            ).asRight[ErrorWrapper])
        } yield {
          logger.info(
            s"[${endpointLogContext.controllerName}][${endpointLogContext.endpointName}] - " +
              s"Success response received with CorrelationId: ${serviceResponse.correlationId}")

          Ok(Json.toJson(hateoasResponse))
            .withApiHeaders(serviceResponse.correlationId)
            .as(MimeTypes.JSON)
        }

      result.leftMap { errorWrapper =>
        val correlationId = getCorrelationId(errorWrapper)
        val result = errorResult(errorWrapper).withApiHeaders(correlationId)

        result
      }.merge
    }

  private def errorResult(errorWrapper: ErrorWrapper) = {
    (errorWrapper.error: @unchecked) match {
      case BadRequestError | NinoFormatError | TaxYearFormatError |
           RuleTaxYearRangeInvalidError | RuleTaxYearNotSupportedError
      => BadRequest(Json.toJson(errorWrapper))
      case RuleNoSubmissionsExistError | RuleFinalDeclarationReceivedError => Forbidden(Json.toJson(errorWrapper))
      case NotFoundError => NotFound(Json.toJson(errorWrapper))
      case DownstreamError => InternalServerError(Json.toJson(errorWrapper))
    }
  }
}