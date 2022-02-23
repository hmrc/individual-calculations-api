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

package v2.models.response.getMetadata

import config.AppConfig
import play.api.libs.json.{JsPath, Json, OWrites, Reads}
import v2.hateoas.{HateoasLinks, HateoasLinksFactory}
import v2.models.hateoas.{HateoasData, Link}
import v2.models.response.common.{CalculationRequestor, CalculationType}

case class MetadataResponse(id: String,
                            taxYear: String,
                            requestedBy: CalculationRequestor,
                            calculationReason: String,
                            calculationTimestamp: Option[String],
                            calculationType: CalculationType,
                            intentToCrystallise: Boolean,
                            crystallised: Boolean,
                            totalIncomeTaxAndNicsDue: Option[BigDecimal],
                            calculationErrorCount: Option[Int],
                            metadataExistence: Option[MetadataExistence])

object MetadataResponse extends HateoasLinks {

  implicit val writes: OWrites[MetadataResponse] = Json.writes[MetadataResponse]
  implicit val reads: Reads[MetadataResponse] =
    (JsPath \ "metadata").read[MetadataResponse](Json.reads[MetadataResponse])

  implicit object LinksFactory extends HateoasLinksFactory[MetadataResponse, MetadataHateoasData] {
    override def links(appConfig: AppConfig, data: MetadataHateoasData): Seq[Link] = {
      import data.{calculationId, errorCount, nino}
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