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
      import data._
      val hateoasLinks = LinkExists(getMetadata(appConfig, nino, calculationId, isSelf = true),dataIsPresent = true)
      val incomeTaxLink = LinkExists(getIncomeTax(appConfig, nino, calculationId, isSelf = false),data.metadataExistence.incomeTaxAndNicsCalculated)
      val taxableIncomeLink = LinkExists(getTaxableIncome(appConfig, nino, calculationId, isSelf = false),data.metadataExistence.taxableIncome)
      val allowancesLink = LinkExists(getAllowances(appConfig, nino, calculationId, isSelf = false),data.metadataExistence.allowancesDeductionsAndReliefs)
      val eoyLink = LinkExists(getEoyEstimate(appConfig, nino, calculationId, isSelf = false),data.metadataExistence.endOfYearEstimate)
      val messagesLink = LinkExists(getMessages(appConfig, nino, calculationId, isSelf = false),data.metadataExistence.messages)

      if (errorCount.isEmpty) {
        addHateoasLinksIfNonEmpty(hateoasLinks, incomeTaxLink, taxableIncomeLink, allowancesLink, eoyLink, messagesLink)
      } else {
        addHateoasLinksIfNonEmpty(hateoasLinks, messagesLink)
      }
    }
  }

  private def addHateoasLinksIfNonEmpty(links: LinkExists*): Seq[Link] = {
    links.filter(_.dataIsPresent).map(_.hateoasLink)
  }
}

case class LinkExists(hateoasLink: Link, dataIsPresent: Boolean)

case class MetadataHateoasData(nino: String, calculationId: String, errorCount: Option[Int],
                               metadataExistence: MetadataExistence) extends HateoasData
