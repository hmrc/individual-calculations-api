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

package v1.models.response.getMetadata

import config.AppConfig
import play.api.libs.json.{JsPath, Json, OWrites, Reads}
import v1.hateoas.{HateoasLinks, HateoasLinksFactory}
import v1.models.hateoas.{HateoasData, Link}
import v1.models.response.common.{CalculationReason, CalculationRequestor, CalculationType}

case class MetadataResponse(id: String,
                            taxYear: String,
                            requestedBy: CalculationRequestor,
                            calculationReason: CalculationReason,
                            calculationTimestamp: Option[String],
                            calculationType: CalculationType,
                            intentToCrystallise: Boolean,
                            crystallised: Boolean,
                            totalIncomeTaxAndNicsDue: Option[BigDecimal],
                            calculationErrorCount: Option[Int])

object MetadataResponse extends HateoasLinks {

  implicit val writes: OWrites[MetadataResponse] = Json.writes[MetadataResponse]
  implicit val reads: Reads[MetadataResponse] =
    (JsPath \ "metadata").read[MetadataResponse](Json.reads[MetadataResponse])

  implicit object LinksFactory extends HateoasLinksFactory[MetadataResponse, MetadataHateoasData] {
    override def links(appConfig: AppConfig, data: MetadataHateoasData): Seq[Link] = {
      import data.{calculationId, errorCount, nino}
      if (errorCount.isEmpty) {
        Seq(
          getMetadata(appConfig, nino, calculationId, isSelf = true),
          getIncomeTax(appConfig, nino, calculationId, isSelf = false),
          getTaxableIncome(appConfig, nino, calculationId, isSelf = false),
          getAllowances(appConfig, nino, calculationId, isSelf = false),
          getEoyEstimate(appConfig, nino, calculationId, isSelf = false),
          getMessages(appConfig, nino, calculationId, isSelf = false)
        )
      } else {
        Seq(
          getMetadata(appConfig, nino, calculationId, isSelf = true),
          getMessages(appConfig, nino, calculationId, isSelf = false)
        )
      }
    }
  }

}

case class MetadataHateoasData(nino: String, calculationId: String, errorCount: Option[Int]) extends HateoasData