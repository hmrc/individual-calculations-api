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

package v3.models.response.triggerCalculation

import config.AppConfig
import play.api.libs.json.{Json, OFormat}
import v3.hateoas.{HateoasLinks, HateoasLinksFactory}
import v3.models.hateoas.{HateoasData, Link}

case class TriggerCalculationResponse(id: String)

object TriggerCalculationResponse extends HateoasLinks {

  implicit val format: OFormat[TriggerCalculationResponse] = Json.format[TriggerCalculationResponse]

  implicit object LinksFactory extends HateoasLinksFactory[TriggerCalculationResponse, TriggerCalculationHateoasData] {
    override def links(appConfig: AppConfig, data: TriggerCalculationHateoasData): Seq[Link] = {
      import data.{id, nino}
      Seq(getMetadata(appConfig, nino, id, isSelf = true))
    }
  }

}

case class TriggerCalculationHateoasData(nino: String, id: String) extends HateoasData