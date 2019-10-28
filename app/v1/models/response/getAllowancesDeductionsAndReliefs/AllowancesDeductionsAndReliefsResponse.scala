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

package v1.models.response.getAllowancesDeductionsAndReliefs

import config.AppConfig
import play.api.libs.json._
import v1.hateoas.{HateoasLinks, HateoasLinksFactory}
import v1.models.hateoas.{HateoasData, Link}

case class AllowancesDeductionsAndReliefsResponse(summary: CalculationSummary, detail: CalculationDetail) {

  def isEmpty: Boolean =
    (summary.totalAllowancesAndDeductions, summary.totalReliefs) match {
      case (Some(x), _) if x > 0 => false
      case (_, Some(x)) if x > 0 => false
      case _                     => true
    }
}

object AllowancesDeductionsAndReliefsResponse extends HateoasLinks{
  implicit val reads: Reads[AllowancesDeductionsAndReliefsResponse] =
    (__ \ "allowancesDeductionsAndReliefs").read(Json.reads[AllowancesDeductionsAndReliefsResponse])

  implicit val writes: Writes[AllowancesDeductionsAndReliefsResponse] = Json.writes[AllowancesDeductionsAndReliefsResponse]


  implicit object LinksFactory extends HateoasLinksFactory[AllowancesDeductionsAndReliefsResponse, AllowancesHateoasData] {
    override def links(appConfig: AppConfig, data: AllowancesHateoasData): Seq[Link] = {
      Seq(getMetadata(appConfig, data.nino, data.calculationId, isSelf = false), getAllowances(appConfig, data.nino, data.calculationId, isSelf = true))
    }
  }


}

case class AllowancesHateoasData(nino: String, calculationId: String) extends HateoasData