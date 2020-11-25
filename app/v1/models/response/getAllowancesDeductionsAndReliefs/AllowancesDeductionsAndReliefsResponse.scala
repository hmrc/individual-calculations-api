/*
 * Copyright 2020 HM Revenue & Customs
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

object AllowancesDeductionsAndReliefsResponse extends HateoasLinks {

  def isEmpty(json: JsValue): Boolean = {
    val totalAllowancesAndDeductions: Option[BigInt] =
      (json \ "data" \ "allowancesDeductionsAndReliefs" \ "summary" \ "totalAllowancesAndDeductions").asOpt[BigInt]

    val totalReliefs: Option[BigInt] =
      (json \ "data" \ "allowancesDeductionsAndReliefs" \ "summary" \ "totalReliefs").asOpt[BigInt]

    (totalAllowancesAndDeductions, totalReliefs) match {
      case (Some(x), _) if x > 0 => false
      case (_, Some(x)) if x > 0 => false
      case _                     => true
    }
  }

  implicit object LinksFactory extends HateoasLinksFactory[JsValue, AllowancesDeductionsAndReliefsHateoasData] {
    override def links(appConfig: AppConfig, data: AllowancesDeductionsAndReliefsHateoasData): Seq[Link] = {
      Seq(
        getMetadata(appConfig, data.nino, data.calculationId, isSelf = false),
        getAllowances(appConfig, data.nino, data.calculationId, isSelf = true)
      )
    }
  }

}

case class AllowancesDeductionsAndReliefsHateoasData(nino: String, calculationId: String) extends HateoasData
