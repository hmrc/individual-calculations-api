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

package v3.models.response.listCalculations

import config.AppConfig
import play.api.libs.json.{JsPath, Json, OWrites, Reads}
import v3.hateoas.{HateoasLinks, HateoasListLinksFactory}
import v3.models.hateoas
import v3.models.hateoas.HateoasData

case class ListCalculationsResponse[I](calculations: Seq[I])

object ListCalculationsResponse extends HateoasLinks {
  type ListCalculations = ListCalculationsResponse[Calculation]

  implicit val writes: OWrites[ListCalculations] = Json.writes[ListCalculations]
  implicit val reads: Reads[ListCalculations] = JsPath.read[Seq[Calculation]].map(ListCalculationsResponse(_))

  implicit object ListLinksFactory extends HateoasListLinksFactory[ListCalculationsResponse, Calculation, ListCalculationsHateoasData] {
    override def itemLinks(appConfig: AppConfig,
                           data: ListCalculationsHateoasData,
                           item: Calculation): Seq[hateoas.Link] = Seq(
      retrieve(appConfig, data.nino, data.taxYear, item.calculationId)
    )

    override def links(appConfig: AppConfig, data: ListCalculationsHateoasData): Seq[hateoas.Link] = {
      import data._
      Seq(
        trigger(appConfig, nino, taxYear),
        list(appConfig, nino)
      )
    }
  }

}

case class ListCalculationsHateoasData(nino: String, taxYear: String) extends HateoasData
