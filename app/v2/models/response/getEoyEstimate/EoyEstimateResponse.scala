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

package v2.models.response.getEoyEstimate

import config.AppConfig
import play.api.libs.functional.syntax._
import play.api.libs.json._
import v2.hateoas.{HateoasLinks, HateoasLinksFactory}
import v2.models.hateoas.{HateoasData, Link}
import v2.models.response.getEoyEstimate.detail.EoyEstimateDetail
import v2.models.response.getEoyEstimate.summary.EoyEstimateSummary

case class EoyEstimateResponse(summary: EoyEstimateSummary, detail: EoyEstimateDetail, id: String)

object EoyEstimateResponse extends HateoasLinks {

  implicit val writes: OWrites[EoyEstimateResponse] = new OWrites[EoyEstimateResponse] {
    def writes(response: EoyEstimateResponse): JsObject =
      Json.obj(
        "summary" -> response.summary,
        "detail" -> response.detail
      )
  }
  implicit val reads: Reads[EoyEstimateResponse] = (
    (JsPath \ "endOfYearEstimate" \ "summary").read[EoyEstimateSummary] and
      (JsPath \ "endOfYearEstimate" \ "detail").read[EoyEstimateDetail] and
      (JsPath \ "metadata" \ "id").read[String])(EoyEstimateResponse.apply _)

  implicit object LinksFactory extends HateoasLinksFactory[EoyEstimateResponse, EoyEstimateHateoasData] {
    override def links(appConfig: AppConfig, data: EoyEstimateHateoasData): Seq[Link] = {
      import data.{calculationId, nino}
      Seq(
        getMetadata(appConfig, nino, calculationId, isSelf = false),
        getEoyEstimate(appConfig, nino, calculationId, isSelf = true)
      )
    }
  }

}

case class EoyEstimateHateoasData(nino: String, calculationId: String) extends HateoasData