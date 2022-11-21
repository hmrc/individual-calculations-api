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
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import play.mvc.Http.MimeTypes
import utils.{IdGenerator, Logging}
import v3.controllers.requestParsers.ListCalculationsParser
import v3.hateoas.HateoasFactory
import v3.models.errors.{ErrorWrapper, _}
import v3.models.request.ListCalculationsRawData
import v3.models.response.listCalculations.ListCalculationsHateoasData
import v3.services.{EnrolmentsAuthService, ListCalculationsService, MtdIdLookupService}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ListCalculationsController @Inject() (val authService: EnrolmentsAuthService,
                                            val lookupService: MtdIdLookupService,
                                            parser: ListCalculationsParser,
                                            service: ListCalculationsService,
                                            hateoasFactory: HateoasFactory,
                                            cc: ControllerComponents,
                                            val idGenerator: IdGenerator)(implicit val ec: ExecutionContext)
    extends AuthorisedController(cc)
    with BaseController
    with Logging {

  implicit val endpointLogContext: EndpointLogContext = EndpointLogContext(
    controllerName = "ListCalculationsController",
    endpointName = "list"
  )

  def list(nino: String, taxYear: Option[String]): Action[AnyContent] =
    authorisedAction(nino).async { implicit request =>
      implicit val correlationId: String = idGenerator.getCorrelationId
      logger.info(
        message = s"[${endpointLogContext.controllerName}][${endpointLogContext.endpointName}] " +
          s"with correlationId : $correlationId"
      )

      val rawData = ListCalculationsRawData(nino, taxYear)
      val result =
        for {
          parsedRequest   <- EitherT.fromEither[Future](parser.parseRequest(rawData))
          serviceResponse <- EitherT(service.list(parsedRequest))
        } yield {
          val vendorResponse = hateoasFactory.wrapList(serviceResponse.responseData, ListCalculationsHateoasData(nino, taxYear))
          logger.info(
            s"[${endpointLogContext.controllerName}][${endpointLogContext.endpointName}] - " +
              s"Success response received with CorrelationId: ${serviceResponse.correlationId}")

          Ok(Json.toJson(vendorResponse))
            .withApiHeaders(serviceResponse.correlationId)
            .as(MimeTypes.JSON)
        }

      result.leftMap { errorWrapper =>
        val resCorrelationId = errorWrapper.correlationId
        val result           = errorResult(errorWrapper).withApiHeaders(resCorrelationId)
        logger.warn(
          s"[${endpointLogContext.controllerName}][${endpointLogContext.endpointName}] - " +
            s"Error response received with CorrelationId: $resCorrelationId")

        result
      }.merge
    }

  private def errorResult(errorWrapper: ErrorWrapper) = errorWrapper.error match {
    case _
        if errorWrapper.containsAnyOf(
          BadRequestError,
          NinoFormatError,
          TaxYearFormatError,
          RuleTaxYearRangeInvalidError,
          RuleTaxYearNotSupportedError,
          RuleIncorrectGovTestScenarioError
        ) =>
      BadRequest(Json.toJson(errorWrapper))
    case NotFoundError => NotFound(Json.toJson(errorWrapper))
    case InternalError => InternalServerError(Json.toJson(errorWrapper))
    case _             => unhandledError(errorWrapper)
  }

}
