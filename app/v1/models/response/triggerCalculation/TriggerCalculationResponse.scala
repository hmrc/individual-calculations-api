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

package v1.models.response.triggerCalculation

import config.AppConfig
import play.api.libs.json.{Json, OFormat}
import v1.hateoas.{HateoasLinks, HateoasLinksFactory}
import v1.models.hateoas.{HateoasData, Link}

case class TriggerCalculationResponse(id: String)

object TriggerCalculationResponse extends HateoasLinks {
  implicit val formats: OFormat[TriggerCalculationResponse] = Json.format[TriggerCalculationResponse]

  implicit object TriggerLinksFactory extends HateoasLinksFactory[TriggerCalculationResponse, TriggerCalculationHateaosData] {
    override def links(appConfig: AppConfig, data: TriggerCalculationHateaosData): Seq[Link] = {
      Seq(getMetadata(appConfig, data.nino, data.id))
    }
  }
}

case class TriggerCalculationHateaosData(nino: String, id: String) extends HateoasData
