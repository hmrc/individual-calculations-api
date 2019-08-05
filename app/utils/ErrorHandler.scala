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

package utils

import javax.inject._
import play.api._
import play.api.http.DefaultHttpErrorHandler
import play.api.libs.json.Json
import play.api.mvc.Results._
import play.api.mvc._
import play.api.routing.Router
import v1.models.errors.MtdError

import scala.concurrent._

@Singleton
class ErrorHandler @Inject()(
                              env: Environment,
                              config: Configuration,
                              sourceMapper: OptionalSourceMapper,
                              router: Provider[Router]
                            )(implicit ec: ExecutionContext) extends DefaultHttpErrorHandler(env, config, sourceMapper, router) {

  override protected def onBadRequest(request: RequestHeader, error: String): Future[Result] = {
    Logger.warn(s"[ErrorHandler][onBadRequest] - Received undetected error: '$error'")
    val result = BadRequest(Json.toJson(MtdError("INVALID_REQUEST", JsonErrorSanitiser.sanitise(error))))

    Future.successful(result)
  }
}

