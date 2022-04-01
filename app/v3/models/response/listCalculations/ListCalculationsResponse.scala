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

import cats.Functor
import config.AppConfig
import play.api.libs.json._
import v3.hateoas.{HateoasLinks, HateoasListLinksFactory}
import v3.models.hateoas.{HateoasData, Link}
import v3.models.response.common.{CalculationRequestor, CalculationType}

case class CalculationListItem(id: String,
                               calculationTimestamp: String,
                               `type`: CalculationType,
                               requestedBy: Option[CalculationRequestor])

object CalculationListItem {
  implicit val format: OFormat[CalculationListItem] = Json.format[CalculationListItem]
}

case class ListCalculationsResponse[I](calculations: Seq[I])

object ListCalculationsResponse extends HateoasLinks {

  implicit def writes[I: Writes]: OWrites[ListCalculationsResponse[I]] = Json.writes[ListCalculationsResponse[I]]
  implicit def reads[I: Reads]: Reads[ListCalculationsResponse[I]] = Json.reads

  implicit object LinksFactory extends HateoasListLinksFactory[ListCalculationsResponse, CalculationListItem, ListCalculationsHateoasData] {
    override def links(appConfig: AppConfig, data: ListCalculationsHateoasData): Seq[Link] = {
      import data.nino
      Seq(list(appConfig, nino), trigger(appConfig, nino))
    }

    override def itemLinks(appConfig: AppConfig, data: ListCalculationsHateoasData, item: CalculationListItem): Seq[Link] = {
      import data.nino
      import item.id
      Seq(getMetadata(appConfig, nino, id, isSelf = true))
    }
  }

  implicit object ResponseFunctor extends Functor[ListCalculationsResponse] {
    override def map[A, B](fa: ListCalculationsResponse[A])(f: A => B): ListCalculationsResponse[B] = {
      ListCalculationsResponse(fa.calculations.map(f))
    }
  }

}

case class ListCalculationsHateoasData(nino: String) extends HateoasData