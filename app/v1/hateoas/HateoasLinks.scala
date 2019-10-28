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

package v1.hateoas

import config.AppConfig
import v1.models.hateoas.Link
import v1.models.hateoas.Method._
import v1.models.hateoas.RelType._

trait HateoasLinks {
  private def baseUri(appConfig: AppConfig, nino: String): String =
    s"/${appConfig.apiGatewayContext}/$nino/self-assessment"

  private def metadataUri(appConfig: AppConfig, nino: String, calcId: String): String =
    baseUri(appConfig, nino) + s"/$calcId"

  // API resource links
  def trigger(appConfig: AppConfig, nino: String): Link =
    Link(href = baseUri(appConfig, nino), method = POST, rel = TRIGGER)

  def list(appConfig: AppConfig, nino: String): Link =
    Link(href = baseUri(appConfig, nino), method = GET, rel = SELF)

  def getMetadata(appConfig: AppConfig, nino: String, calcId: String): Link =
    Link(href = metadataUri(appConfig, nino, calcId), method = GET, rel = SELF)
}
