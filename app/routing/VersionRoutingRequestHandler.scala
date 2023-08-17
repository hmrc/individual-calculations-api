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

package routing

import api.models.errors.{InvalidAcceptHeaderError, NotFoundError, UnsupportedVersionError}
import config.AppConfig
import play.api.http.{DefaultHttpRequestHandler, HttpConfiguration, HttpErrorHandler, HttpFilters}
import play.api.libs.json.Json
import play.api.mvc.{DefaultActionBuilder, Handler, RequestHeader, Results}
import play.api.routing.Router
import play.core.DefaultWebCommands

import javax.inject.{Inject, Singleton}

@Singleton
class VersionRoutingRequestHandler @Inject() (versionRoutingMap: VersionRoutingMap,
                                              errorHandler: HttpErrorHandler,
                                              httpConfiguration: HttpConfiguration,
                                              config: AppConfig,
                                              filters: HttpFilters,
                                              action: DefaultActionBuilder)
    extends DefaultHttpRequestHandler(
      webCommands = new DefaultWebCommands,
      optDevContext = None,
      router = versionRoutingMap.defaultRouter,
      errorHandler = errorHandler,
      configuration = httpConfiguration,
      filters = filters.filters
    ) {

  private val unsupportedVersionAction = action(Results.NotFound(Json.toJson(UnsupportedVersionError)))

  private val resourceNotFoundAction = action(Results.NotFound(Json.toJson(NotFoundError)))

  private val invalidAcceptHeaderError = action(Results.NotAcceptable(Json.toJson(InvalidAcceptHeaderError)))

  override def routeRequest(request: RequestHeader): Option[Handler] = {

    def documentHandler: Option[Handler] = routeWith(versionRoutingMap.defaultRouter, request)

    def apiHandler: Option[Handler] = Some(
      Versions.getFromRequest(request) match {
        case Left(InvalidHeader)   => invalidAcceptHeaderError
        case Left(VersionNotFound) => unsupportedVersionAction

        case Right(version) => findRoute(request, version) getOrElse resourceNotFoundAction
      }
    )

    documentHandler orElse apiHandler
  }

  /** If a route isn't found for this version, fall back to previous available.
    */
  private def findRoute(request: RequestHeader, version: Version): Option[Handler] = {
    val found =
      if (config.endpointsEnabled(version)) {
        versionRoutingMap
          .versionRouter(version)
          .flatMap(router => routeWith(router, request))
      } else {
        Some(unsupportedVersionAction)
      }

    found
      .orElse(version.maybePrevious.flatMap(previousVersion => findRoute(request, previousVersion)))
  }

  private def routeWith(router: Router, request: RequestHeader): Option[Handler] =
    router
      .handlerFor(request)
      .orElse {
        if (request.path.endsWith("/")) {
          val pathWithoutSlash        = request.path.dropRight(1)
          val requestWithModifiedPath = request.withTarget(request.target.withPath(pathWithoutSlash))
          router.handlerFor(requestWithModifiedPath)
        } else {
          None
        }
      }

}
