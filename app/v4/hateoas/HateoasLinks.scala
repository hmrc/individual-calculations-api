/*
 * Copyright 2023 HM Revenue & Customs
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

package v4.hateoas

import api.models.domain.TaxYear
import api.models.hateoas.Link
import api.models.hateoas.Method.{GET, POST}
import api.models.hateoas.RelType.{LIST, SELF, SUBMIT_FINAL_DECLARATION, TRIGGER}
import config.AppConfig
import v4.models.hateoas.RelType._

trait HateoasLinks {

  private def baseSaUri(appConfig: AppConfig, nino: String): String =
    s"/${appConfig.apiGatewayContext}/$nino/self-assessment"

  private def baseSaUriWithTaxYear(appConfig: AppConfig, nino: String, taxYear: TaxYear): String =
    s"/${appConfig.apiGatewayContext}/$nino/self-assessment/${taxYear.asMtd}"

  // API resource links
  def trigger(appConfig: AppConfig, nino: String): Link =
    Link(href = baseSaUri(appConfig, nino), method = POST, rel = TRIGGER)

  def list(appConfig: AppConfig, nino: String): Link =
    Link(href = baseSaUri(appConfig, nino), method = GET, rel = SELF)

  def getMetadata(appConfig: AppConfig, nino: String, calcId: String, isSelf: Boolean): Link =
    Link(href = baseSaUri(appConfig, nino) + s"/$calcId", method = GET, rel = if (isSelf) SELF else METADATA)

  def getIncomeTax(appConfig: AppConfig, nino: String, calcId: String, isSelf: Boolean): Link =
    Link(href = baseSaUri(appConfig, nino) + s"/$calcId/income-tax-nics-calculated", method = GET, rel = if (isSelf) SELF else INCOME_TAX)

  def trigger(appConfig: AppConfig, nino: String, taxYear: TaxYear): Link = Link(
    href = baseSaUriWithTaxYear(appConfig, nino, taxYear),
    method = POST,
    rel = TRIGGER
  )

  def list(appConfig: AppConfig, nino: String, taxYear: TaxYear, isSelf: Boolean): Link = {
    val href = baseSaUri(appConfig, nino) + s"?taxYear=${taxYear.asMtd}"

    Link(href = href, method = GET, rel = if (isSelf) SELF else LIST)
  }

  def retrieve(appConfig: AppConfig, nino: String, taxYear: TaxYear, calculationId: String): Link = Link(
    href = baseSaUriWithTaxYear(appConfig, nino, taxYear) + s"/$calculationId",
    method = GET,
    rel = SELF
  )

  def submitFinalDeclaration(appConfig: AppConfig, nino: String, taxYear: TaxYear, calculationId: String): Link = Link(
    href = s"${baseSaUriWithTaxYear(appConfig, nino, taxYear)}/$calculationId/final-declaration",
    method = POST,
    rel = SUBMIT_FINAL_DECLARATION
  )
}
