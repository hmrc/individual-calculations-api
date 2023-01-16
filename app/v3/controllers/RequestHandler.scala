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

import cats.data.EitherT
import cats.implicits._
import play.api.http.Status
import play.api.libs.json.{JsValue, Json, Writes}
import play.api.mvc.Result
import play.api.mvc.Results.InternalServerError
import utils.Logging
import v3.controllers.requestParsers.RequestParser
import v3.hateoas.{HateoasFactory, HateoasLinksFactory}
import v3.models.errors.{ErrorWrapper, InternalError}
import v3.models.hateoas.{HateoasData, HateoasWrapper}
import v3.models.outcomes.ResponseWrapper
import v3.models.request.RawData

import scala.concurrent.{ExecutionContext, Future}

trait RequestHandler[InputRaw <: RawData] {

  def handleRequest(rawData: InputRaw)(implicit ctx: RequestContext, request: UserRequest[_], ec: ExecutionContext): Future[Result]

}

object RequestHandler {

  def withParser[InputRaw <: RawData, Input](parser: RequestParser[InputRaw, Input]): ParserOnlyBuilder[InputRaw, Input] =
    new ParserOnlyBuilder[InputRaw, Input](parser)

  // Intermediate class so that the compiler can separately capture the InputRaw and Input types here, and the Output type later
  class ParserOnlyBuilder[InputRaw <: RawData, Input] private[RequestHandler] (parser: RequestParser[InputRaw, Input]) {

    def withService[Output](
        serviceFunction: Input => Future[Either[ErrorWrapper, ResponseWrapper[Output]]]): RequestHandlerBuilder[InputRaw, Input, Output] =
      RequestHandlerBuilder(parser, serviceFunction)

  }

  case class RequestHandlerBuilder[InputRaw <: RawData, Input, Output] private[RequestHandler] (
      parser: RequestParser[InputRaw, Input],
      service: Input => Future[Either[ErrorWrapper, ResponseWrapper[Output]]],
      errorHandling: ErrorHandling = ErrorHandling.Default,
      resultCreator: ResultCreator[InputRaw, Input, Output] = ResultCreator.noContent[InputRaw, Input, Output](),
      auditHandler: Option[AuditHandler] = None
  ) extends RequestHandler[InputRaw] {

    def handleRequest(rawData: InputRaw)(implicit ctx: RequestContext, request: UserRequest[_], ec: ExecutionContext): Future[Result] =
      Delegate.handleRequest(rawData)

    def withResultCreator(resultCreator: ResultCreator[InputRaw, Input, Output]): RequestHandlerBuilder[InputRaw, Input, Output] =
      copy(resultCreator = resultCreator)

    def withErrorHandling(errorHandling: ErrorHandling): RequestHandlerBuilder[InputRaw, Input, Output] =
      copy(errorHandling = errorHandling)

    def withAuditing(auditHandler: AuditHandler): RequestHandlerBuilder[InputRaw, Input, Output] =
      copy(auditHandler = Some(auditHandler))

    /** Shorthand for
      * {{{
      * withResultCreator(ResultCreator.plainJson(successStatus))
      * }}}
      */
    def withPlainJsonResult(successStatus: Int = Status.OK)(implicit ws: Writes[Output]): RequestHandlerBuilder[InputRaw, Input, Output] =
      withResultCreator(ResultCreator.plainJson(successStatus))

    /** Shorthand for
      * {{{
      * withResultCreator(ResultCreator.noContent)
      * }}}
      */
    def withNoContentResult(successStatus: Int = Status.NO_CONTENT): RequestHandlerBuilder[InputRaw, Input, Output] =
      withResultCreator(ResultCreator.noContent(successStatus))

    /** Shorthand for
      * {{{
      * withResultCreator(ResultCreator.hateoasWrapping(hateoasFactory, successStatus)(data))
      * }}}
      */
    def withHateoasResultFrom[HData <: HateoasData](
        hateoasFactory: HateoasFactory)(data: (Input, Output) => HData, successStatus: Int = Status.OK)(implicit
        linksFactory: HateoasLinksFactory[Output, HData],
        writes: Writes[HateoasWrapper[Output]]): RequestHandlerBuilder[InputRaw, Input, Output] =
      withResultCreator(ResultCreator.hateoasWrapping(hateoasFactory, successStatus)(data))

    /** Shorthand for
      * {{{
      * withResultCreator(ResultCreator.hateoasWrapping(hateoasFactory, successStatus)((_,_) => data))
      * }}}
      */
    def withHateoasResult[HData <: HateoasData](hateoasFactory: HateoasFactory)(data: HData, successStatus: Int = Status.OK)(implicit
        linksFactory: HateoasLinksFactory[Output, HData],
        writes: Writes[HateoasWrapper[Output]]): RequestHandlerBuilder[InputRaw, Input, Output] =
      withResultCreator(ResultCreator.hateoasWrapping(hateoasFactory, successStatus)((_, _) => data))

    // Scoped as a private delegate so as to keep the logic completely separate from the configuration
    private object Delegate extends RequestHandler[InputRaw] with Logging with RequestContextImplicits {

      implicit class Response(result: Result) {

        def withApiHeaders(correlationId: String, responseHeaders: (String, String)*): Result = {

          val newHeaders: Seq[(String, String)] = responseHeaders ++ Seq(
            "X-CorrelationId"        -> correlationId,
            "X-Content-Type-Options" -> "nosniff"
          )

          result.copy(header = result.header.copy(headers = result.header.headers ++ newHeaders))
        }

      }

      def handleRequest(rawData: InputRaw)(implicit ctx: RequestContext, request: UserRequest[_], ec: ExecutionContext): Future[Result] = {

        def auditIfRequired(httpStatus: Int, response: Either[ErrorWrapper, Option[JsValue]]): Unit =
          auditHandler.foreach { creator =>
            creator.performAudit(request.userDetails, httpStatus, response)
          }

        logger.info(
          message = s"[${ctx.endpointLogContext.controllerName}][${ctx.endpointLogContext.endpointName}] " +
            s"with correlationId : ${ctx.correlationId}")

        val result =
          for {
            parsedRequest   <- EitherT.fromEither[Future](parser.parseRequest(rawData))
            serviceResponse <- EitherT(service(parsedRequest))
          } yield {
            logger.info(
              s"[${ctx.endpointLogContext.controllerName}][${ctx.endpointLogContext.endpointName}] - " +
                s"Success response received with CorrelationId: ${serviceResponse.correlationId}")

            val resultWrapper = resultCreator
              .createResult(rawData, parsedRequest, serviceResponse.responseData)

            val result = resultWrapper.asResult.withApiHeaders(serviceResponse.correlationId)

            auditIfRequired(result.header.status, Right(resultWrapper.body))

            result
          }

        result.leftMap { errorWrapper =>
          val result = errorResult(errorWrapper)

          auditIfRequired(result.header.status, Left(errorWrapper))

          result
        }.merge
      }

      private def errorResult(errorWrapper: ErrorWrapper)(implicit endpointLogContext: EndpointLogContext): Result = {
        val resCorrelationId = errorWrapper.correlationId

        logger.warn(
          s"[${endpointLogContext.controllerName}][${endpointLogContext.endpointName}] - " +
            s"Error response received with CorrelationId: $resCorrelationId")

        val errorResult = errorHandling.errorHandler.applyOrElse(errorWrapper, unhandledError)

        errorResult.withApiHeaders(resCorrelationId)
      }

      private def unhandledError(errorWrapper: ErrorWrapper)(implicit endpointLogContext: EndpointLogContext): Result = {
        logger.error(
          s"[${endpointLogContext.controllerName}][${endpointLogContext.endpointName}] - " +
            s"Unhandled error: $errorWrapper")
        InternalServerError(Json.toJson(InternalError))
      }

    }

  }

}
