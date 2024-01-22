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

package api.controllers

import api.controllers.requestParsers.RequestParser
import api.models.errors.{ErrorWrapper, InternalError}
import api.models.hateoas.{HateoasData, HateoasWrapper}
import api.models.outcomes.ResponseWrapper
import api.models.request.RawData
import cats.data.EitherT
import cats.data.Validated.{Invalid, Valid}
import cats.implicits._
import config.AppConfig
import config.Deprecation.{Deprecated, NotDeprecated}
import play.api.http.Status
import play.api.libs.json.{JsValue, Json, Writes}
import play.api.mvc.Result
import play.api.mvc.Results.InternalServerError
import routing.Version
import utils.DateUtils.formatImf
import utils.Logging
import v3.hateoas.{HateoasFactory, HateoasLinksFactory}

import scala.concurrent.{ExecutionContext, Future}

trait RequestHandler[InputRaw <: RawData] {

  def handleRequest(
      rawData: InputRaw)(implicit ctx: RequestContext, request: UserRequest[_], ec: ExecutionContext, appConfig: AppConfig): Future[Result]

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
      auditHandler: Option[AuditHandler] = None,
      modelHandler: Option[Output => Output] = None
  ) extends RequestHandler[InputRaw] {

    def handleRequest(
        rawData: InputRaw)(implicit ctx: RequestContext, request: UserRequest[_], ec: ExecutionContext, appConfig: AppConfig): Future[Result] =
      Delegate.handleRequest(rawData)

    def withErrorHandling(errorHandling: ErrorHandling): RequestHandlerBuilder[InputRaw, Input, Output] =
      copy(errorHandling = errorHandling)

    def withAuditing(auditHandler: AuditHandler): RequestHandlerBuilder[InputRaw, Input, Output] =
      copy(auditHandler = Some(auditHandler))

    def withModelHandling(modelHandler: Output => Output): RequestHandlerBuilder[InputRaw, Input, Output] =
      copy(modelHandler = Option(modelHandler))

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

    def withResultCreator(resultCreator: ResultCreator[InputRaw, Input, Output]): RequestHandlerBuilder[InputRaw, Input, Output] =
      copy(resultCreator = resultCreator)

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

      implicit class Response(result: Result)(implicit appConfig: AppConfig, apiVersion: Version) {

        private val deprecationFor = appConfig.deprecationFor(apiVersion)

        def withApiHeaders(correlationId: String, responseHeaders: (String, String)*): Result = {

          val maybeDeprecationHeaders: List[(String, String)] = deprecationFor match {
            case Valid(Deprecated(deprecatedOn, sunsetDate)) =>
              List(
                "Deprecation" -> formatImf(deprecatedOn),
                "Sunset"      -> sunsetDate.map(sunsetDate => formatImf(sunsetDate)).getOrElse(""),
                "Link"        -> appConfig.apiDocumentationUrl
              )
            case Valid(NotDeprecated) => Nil
            case Invalid(error)       => throw new Exception(error)
          }

          val headers =
            responseHeaders ++
              List(
                "X-CorrelationId"        -> correlationId,
                "X-Content-Type-Options" -> "nosniff"
              ) ++
              maybeDeprecationHeaders

          result.copy(header = result.header.copy(headers = result.header.headers ++ headers))
        }

      }

      def handleRequest(
          rawData: InputRaw)(implicit ctx: RequestContext, request: UserRequest[_], ec: ExecutionContext, appConfig: AppConfig): Future[Result] = {

        logger.info(
          message = s"[${ctx.endpointLogContext.controllerName}][${ctx.endpointLogContext.endpointName}] " +
            s"with correlationId : ${ctx.correlationId}")

        val result =
          for {
            parsedRequest   <- EitherT.fromEither[Future](parser.parseRequest(rawData))
            serviceResponse <- EitherT(service(parsedRequest))
          } yield doWithContext(ctx.withCorrelationId(serviceResponse.correlationId)) { implicit ctx: RequestContext =>
            modelHandler match {
              case Some(responseHandler) =>
                handleSuccess(rawData, parsedRequest, serviceResponse.copy(responseData = responseHandler(serviceResponse.responseData)))
              case None =>
                handleSuccess(rawData, parsedRequest, serviceResponse)
            }
          }

        result.leftMap { errorWrapper =>
          doWithContext(ctx.withCorrelationId(errorWrapper.correlationId)) { implicit ctx: RequestContext =>
            handleFailure(errorWrapper)
          }
        }.merge
      }

      private def doWithContext[A](ctx: RequestContext)(f: RequestContext => A): A = f(ctx)

      private def handleSuccess(rawData: InputRaw, parsedRequest: Input, serviceResponse: ResponseWrapper[Output])(implicit
          ctx: RequestContext,
          request: UserRequest[_],
          ec: ExecutionContext,
          appConfig: AppConfig): Result = {

        implicit val apiVersion: Version = Version(request)

        logger.info(
          s"[${ctx.endpointLogContext.controllerName}][${ctx.endpointLogContext.endpointName}] - " +
            s"Success response received with CorrelationId: ${ctx.correlationId}")

        val resultWrapper = resultCreator
          .createResult(rawData, parsedRequest, serviceResponse.responseData)

        val result = resultWrapper.asResult.withApiHeaders(ctx.correlationId)

        auditIfRequired(result.header.status, Right(resultWrapper.body))

        result
      }

      def auditIfRequired(httpStatus: Int, response: Either[ErrorWrapper, Option[JsValue]])(implicit
          ctx: RequestContext,
          request: UserRequest[_],
          ec: ExecutionContext): Unit =
        auditHandler.foreach { creator =>
          creator.performAudit(request.userDetails, httpStatus, response)
        }

      private def handleFailure(
          errorWrapper: ErrorWrapper)(implicit ctx: RequestContext, request: UserRequest[_], ec: ExecutionContext, appConfig: AppConfig) = {

        implicit val apiVersion: Version = Version(request)

        logger.warn(
          s"[${ctx.endpointLogContext.controllerName}][${ctx.endpointLogContext.endpointName}] - " +
            s"Error response received with CorrelationId: ${ctx.correlationId}")

        val errorResult = errorHandling.errorHandler.applyOrElse(errorWrapper, unhandledError)

        val result = errorResult.withApiHeaders(ctx.correlationId)

        auditIfRequired(result.header.status, Left(errorWrapper))

        result
      }

      private def unhandledError(errorWrapper: ErrorWrapper)(implicit endpointLogContext: EndpointLogContext): Result = {
        logger.error(
          s"[${endpointLogContext.controllerName}][${endpointLogContext.endpointName}] - " +
            s"Unhandled error: $errorWrapper")
        InternalServerError(Json.toJson(InternalError))
      }

      def handleRequestWithModelUpdate(
          rawData: InputRaw)(implicit ctx: RequestContext, request: UserRequest[_], ec: ExecutionContext, appConfig: AppConfig): Future[Result] = {

        logger.info(
          message = s"[${ctx.endpointLogContext.controllerName}][${ctx.endpointLogContext.endpointName}] " +
            s"with correlationId : ${ctx.correlationId}")

        val result =
          for {
            parsedRequest   <- EitherT.fromEither[Future](parser.parseRequest(rawData))
            serviceResponse <- EitherT(service(parsedRequest))
          } yield doWithContext(ctx.withCorrelationId(serviceResponse.correlationId)) { implicit ctx: RequestContext =>
            handleSuccess(rawData, parsedRequest, serviceResponse)
          }

        result.leftMap { errorWrapper =>
          doWithContext(ctx.withCorrelationId(errorWrapper.correlationId)) { implicit ctx: RequestContext =>
            handleFailure(errorWrapper)
          }
        }.merge
      }

    }

  }

}
