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

package v1.models.response.getMessages

import config.AppConfig
import play.api.libs.json._
import v1.hateoas.{HateoasLinks, HateoasLinksFactory}
import v1.models.hateoas.{HateoasData, Link}

object MessagesResponse extends HateoasLinks {

  implicit object LinksFactory extends HateoasLinksFactory[JsValue, MessagesHateoasData] {
    override def links(appConfig: AppConfig, data: MessagesHateoasData): Seq[Link] = {
      import data.{id, nino}
      Seq(
        getMetadata(appConfig, nino, id, isSelf = false),
        getMessages(appConfig, nino, id, isSelf = true)
      )
    }
  }

  def hasMessages(json: JsValue): Boolean = {
    val info = json \ "data" \ "messages" \ "info"
    val warnings = json \ "data" \ "messages" \ "warnings"
    val errors = json \ "data" \ "messages" \ "errors"

    !(info.isEmpty && warnings.isEmpty && errors.isEmpty)
  }

}

case class MessagesHateoasData(nino: String, id: String) extends HateoasData
