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

package v1.controllers.selfAssessment

import cats.data.EitherT
import cats.implicits._
import javax.inject.{ Inject, Singleton }
import play.api.http.MimeTypes
import play.api.libs.json.{ Json, Reads }
import play.api.mvc.{ Action, AnyContent, ControllerComponents }
import utils.Logging
import v1.controllers.requestParsers.ListCalculationsParser
import v1.controllers.{ AuthorisedController, BaseController, EndpointLogContext }
import v1.handling.{ RequestDefn, RequestHandling }
import v1.models.domain.selfAssessment.ListCalculationsResponse
import v1.models.errors._
import v1.models.outcomes.ResponseWrapper
import v1.models.requestData.selfAssessment.{ ListCalculationsRawData, ListCalculationsRequest }
import v1.services._
import v1.support.BackendResponseMappingSupport

import scala.concurrent.{ ExecutionContext, Future }

@Singleton
class ListCalculationsController @Inject()(
    val authService: EnrolmentsAuthService,
    val lookupService: MtdIdLookupService,
    listCalculationsParser: ListCalculationsParser,
    listCalculationsService: StandardService,
    cc: ControllerComponents
)(implicit ec: ExecutionContext)
    extends AuthorisedController(cc)
    with BaseController
    with BackendResponseMappingSupport
    with Logging {

  implicit val endpointLogContext: EndpointLogContext =
    EndpointLogContext(
      controllerName = "ListCalculationsController",
      endpointName = "listCalculations"
    )

  private val requestHandlingFactory: RequestHandling.Factory[ListCalculationsRequest, ListCalculationsResponse] = { (playRequest, req) =>
    new RequestHandling[ListCalculationsResponse] {
      def requestDefn: RequestDefn = {
        val params = Seq("taxYear" -> req.taxYear)
          .collect {
            case (n, Some(v)) => n -> v
          }

        RequestDefn.Get(playRequest.path, params)
      }

      def passThroughErrors: List[MtdError] =
        List(
          NinoFormatError,
          TaxYearFormatError,
          RuleTaxYearNotSupportedError,
          RuleTaxYearRangeExceededError,
          NotFoundError,
          DownstreamError
        )

      def customErrorMapping: PartialFunction[String, (Int, MtdError)] = PartialFunction.empty

      val reads: Reads[ListCalculationsResponse] = implicitly
    }
  }

  def listCalculations(nino: String, taxYear: Option[String]): Action[AnyContent] =
    authorisedAction(nino).async { implicit request =>
      val rawData = ListCalculationsRawData(nino, taxYear)
      val result =
        for {
          parsedRequest   <- EitherT.fromEither[Future](listCalculationsParser.parseRequest(rawData))
          backendResponse <- EitherT(listCalculationsService.doService(requestHandlingFactory(request, parsedRequest)))
          response        <- EitherT.fromEither[Future](notFoundErrorWhenEmpty(backendResponse))
        } yield {
          logger.info(
            s"[${endpointLogContext.controllerName}][${endpointLogContext.endpointName}] - " +
              s"Success response received with CorrelationId: ${response.correlationId}"
          )

          Ok(Json.toJson(response.responseData))
            .withApiHeaders(response.correlationId)
            .as(MimeTypes.JSON)
        }

      result.leftMap { errorWrapper =>
        val correlationId = getCorrelationId(errorWrapper)
        val errorBody     = errorWrapper.errors

        Status(errorWrapper.errors.statusCode)(Json.toJson(errorBody)).withApiHeaders(correlationId)
      }.merge
    }

  private def notFoundErrorWhenEmpty(responseWrapper: ResponseWrapper[ListCalculationsResponse]) =
    responseWrapper.toErrorWhen {
      case response if response.calculations.isEmpty => MtdErrors(NOT_FOUND, NotFoundError)
    }
}
