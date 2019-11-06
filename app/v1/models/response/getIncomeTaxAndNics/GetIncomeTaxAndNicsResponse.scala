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

package v1.models.response.getIncomeTaxAndNics

import config.AppConfig
import play.api.libs.json._
import v1.hateoas.{HateoasLinks, HateoasLinksFactory}
import v1.models.hateoas.{HateoasData, Link}
import v1.models.response.getIncomeTaxAndNics.detail.CalculationDetail
import v1.models.response.getIncomeTaxAndNics.summary.CalculationSummary

case class GetIncomeTaxAndNicsResponse(summary: CalculationSummary, detail: CalculationDetail)

object GetIncomeTaxAndNicsResponse extends HateoasLinks {

  implicit val writes: OWrites[GetIncomeTaxAndNicsResponse] = Json.writes[GetIncomeTaxAndNicsResponse]
  implicit def reads: Reads[GetIncomeTaxAndNicsResponse] =
    ( JsPath \ "incomeTaxAndNicsCalculated").read[GetIncomeTaxAndNicsResponse](Json.reads[GetIncomeTaxAndNicsResponse])

  implicit object LinksFactory extends HateoasLinksFactory[GetIncomeTaxAndNicsResponse, TaxAndNicsHateoasData] {
    override def links(appConfig: AppConfig, data: TaxAndNicsHateoasData): Seq[Link] = {
      Seq(getMetadata(appConfig, data.nino, data.calculationId, isSelf = false), getIncomeTax(appConfig, data.nino, data.calculationId, isSelf = true))
    }
  }

}

case class TaxAndNicsHateoasData(nino: String, calculationId: String) extends HateoasData