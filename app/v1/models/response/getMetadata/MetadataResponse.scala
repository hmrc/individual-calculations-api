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

package v1.models.response.getMetadata

import config.AppConfig
import play.api.libs.json.JsValue
import v1.hateoas.{HateoasLinks, HateoasLinksFactory}
import v1.models.hateoas.{HateoasData, Link}

object MetadataResponse extends HateoasLinks {

  implicit object LinksFactory extends HateoasLinksFactory[JsValue, MetadataHateoasData] {
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
