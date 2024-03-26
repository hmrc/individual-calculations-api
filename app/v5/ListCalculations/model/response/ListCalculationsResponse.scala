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

package v5.ListCalculations.model.response

import api.hateoas.{HateoasData, HateoasLinks, HateoasListLinksFactory, Link}
import api.models.domain.TaxYear
import cats.Functor
import config.AppConfig
import play.api.libs.json._
import v5.ListCalculations.def1.model.response.Def1_Calculation
import v5.ListCalculations.model.response.Def1_ListCalculationsResponse.Def1_ListLinksFactory

sealed trait ListCalculationsResponse[I]

object ListCalculationsResponse extends HateoasLinks {

  implicit def writes[I: Writes]: OWrites[ListCalculationsResponse[I]] = { case def1: Def1_ListCalculationsResponse[I] =>
    Json.toJsObject(def1)
  }

  implicit object ListLinksFactory extends HateoasListLinksFactory[ListCalculationsResponse, Def1_Calculation, ListCalculationsHateoasData] {

    override def itemLinks(appConfig: AppConfig, data: ListCalculationsHateoasData, item: Def1_Calculation): Seq[Link] =
      Def1_ListLinksFactory.itemLinks(appConfig, data, item)

    override def links(appConfig: AppConfig, data: ListCalculationsHateoasData): Seq[Link] =
      Def1_ListLinksFactory.links(appConfig, data)

  }

}

case class Def1_ListCalculationsResponse[I](calculations: Seq[I]) extends ListCalculationsResponse[I]

object Def1_ListCalculationsResponse extends HateoasLinks {
  type ListCalculations = Def1_ListCalculationsResponse[Def1_Calculation]

  implicit def writes[I: Writes]: OWrites[Def1_ListCalculationsResponse[I]] = Json.writes[Def1_ListCalculationsResponse[I]]
  implicit def reads[I: Reads]: Reads[Def1_ListCalculationsResponse[I]]     = JsPath.read[Seq[I]].map(Def1_ListCalculationsResponse(_))

  implicit object Def1_ListLinksFactory
      extends HateoasListLinksFactory[Def1_ListCalculationsResponse, Def1_Calculation, ListCalculationsHateoasData] {

    override def itemLinks(appConfig: AppConfig, data: ListCalculationsHateoasData, item: Def1_Calculation): Seq[Link] =
      Seq(item.taxYear.map(retrieve(appConfig, data.nino, _, item.calculationId))).flatten

    override def links(appConfig: AppConfig, data: ListCalculationsHateoasData): Seq[Link] = {
      import data._
      Seq(
        trigger(appConfig, nino, taxYear),
        list(appConfig, nino, taxYear, isSelf = true)
      )
    }

  }

  implicit object ResponseFunctor extends Functor[Def1_ListCalculationsResponse] {

    override def map[A, B](fa: Def1_ListCalculationsResponse[A])(f: A => B): Def1_ListCalculationsResponse[B] =
      Def1_ListCalculationsResponse(fa.calculations.map(f))

  }

}

case class ListCalculationsHateoasData(nino: String, taxYear: TaxYear) extends HateoasData
