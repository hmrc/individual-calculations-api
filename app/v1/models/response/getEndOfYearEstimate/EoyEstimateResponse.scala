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

package v1.models.response.getEndOfYearEstimate

import config.AppConfig
import play.api.libs.json._
import v1.hateoas.{ HateoasLinks, HateoasLinksFactory }
import v1.models.hateoas.{ HateoasData, Link }
import v1.models.hateoas.Method.GET

case class EoyEstimateResponse(summary: EoyEstimateSummary, detail: EoyEstimateDetail)

object EoyEstimateResponse extends HateoasLinks {
  implicit val writes: OWrites[EoyEstimateResponse] = Json.writes[EoyEstimateResponse]
  implicit val reads: Reads[EoyEstimateResponse] =
    (JsPath \ "endOfYearEstimate").read[EoyEstimateResponse](Json.reads[EoyEstimateResponse])

  implicit object LinkFactory extends HateoasLinksFactory[EoyEstimateResponse, EoyEstimateResponseHateoasData] {
    override def links(appConfig: AppConfig, data: EoyEstimateResponseHateoasData): Seq[Link] = {
      import data._
      Seq(
        getMetadata(appConfig, nino, calculationId, isSelf = false),
        getEoyEstimate(appConfig, nino, calculationId, isSelf = true)
      )
    }
  }
}

case class EoyEstimateResponseHateoasData(nino: String, calculationId: String) extends HateoasData
