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

package v2.models.response.getAllowancesDeductionsAndReliefs

import config.AppConfig
import play.api.libs.functional.syntax._
import play.api.libs.json._
import v2.hateoas.{HateoasLinks, HateoasLinksFactory}
import v2.models.hateoas.{HateoasData, Link}
import v2.models.response.getAllowancesDeductionsAndReliefs.detail.CalculationDetail
import v2.models.response.getAllowancesDeductionsAndReliefs.summary.CalculationSummary

case class AllowancesDeductionsAndReliefsResponse(summary: CalculationSummary, detail: CalculationDetail, id: String)

object AllowancesDeductionsAndReliefsResponse extends HateoasLinks {

  implicit val writes: OWrites[AllowancesDeductionsAndReliefsResponse] = new OWrites[AllowancesDeductionsAndReliefsResponse] {

    def writes(response: AllowancesDeductionsAndReliefsResponse): JsObject =
      Json.obj(
        "summary" -> response.summary,
        "detail"  -> response.detail
      )

  }

  implicit val reads: Reads[AllowancesDeductionsAndReliefsResponse] =
    ((JsPath \ "allowancesDeductionsAndReliefs" \ "summary").read[CalculationSummary] and
      (JsPath \ "allowancesDeductionsAndReliefs" \ "detail").read[CalculationDetail] and
      (JsPath \ "metadata" \ "id").read[String])(AllowancesDeductionsAndReliefsResponse.apply _)

  implicit object LinksFactory extends HateoasLinksFactory[AllowancesDeductionsAndReliefsResponse, AllowancesDeductionsAndReliefsHateoasData] {

    override def links(appConfig: AppConfig, data: AllowancesDeductionsAndReliefsHateoasData): Seq[Link] = {
      Seq(
        getMetadata(appConfig, data.nino, data.calculationId, isSelf = false),
        getAllowances(appConfig, data.nino, data.calculationId, isSelf = true)
      )
    }

  }

}

case class AllowancesDeductionsAndReliefsHateoasData(nino: String, calculationId: String) extends HateoasData
