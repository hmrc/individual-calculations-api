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

package v2.models.response.getIncomeTaxAndNics

import config.AppConfig
import play.api.libs.functional.syntax._
import play.api.libs.json._
import v2.hateoas.{HateoasLinks, HateoasLinksFactory}
import v2.models.hateoas.{HateoasData, Link}
import v2.models.response.getIncomeTaxAndNics.detail.CalculationDetail
import v2.models.response.getIncomeTaxAndNics.summary.CalculationSummary

case class IncomeTaxAndNicsResponse(summary: CalculationSummary, detail: CalculationDetail, id: String)

object IncomeTaxAndNicsResponse extends HateoasLinks {

  implicit val writes: OWrites[IncomeTaxAndNicsResponse] = Json.writes[IncomeTaxAndNicsResponse]
  implicit val reads: Reads[IncomeTaxAndNicsResponse] =
    (
      (JsPath \ "incomeTaxAndNicsCalculated" \ "summary").read[CalculationSummary] and
        (JsPath \ "incomeTaxAndNicsCalculated" \ "detail").read[CalculationDetail] and
        (JsPath \ "metadata" \ "id").read[String]
      )(IncomeTaxAndNicsResponse.apply _)

  implicit object LinksFactory extends HateoasLinksFactory[IncomeTaxAndNicsResponse, IncomeTaxAndNicsHateoasData] {
    override def links(appConfig: AppConfig, data: IncomeTaxAndNicsHateoasData): Seq[Link] = {
      import data.{calculationId, nino}
      Seq(
        getMetadata(appConfig, nino, calculationId, isSelf = false),
        getIncomeTax(appConfig, nino, calculationId, isSelf = true)
      )
    }
  }

}

case class IncomeTaxAndNicsHateoasData(nino: String, calculationId: String) extends HateoasData