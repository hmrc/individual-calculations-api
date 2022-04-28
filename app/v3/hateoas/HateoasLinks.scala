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

package v3.hateoas

import config.AppConfig
import v3.models.hateoas.Link
import v3.models.hateoas.Method._
import v3.models.hateoas.RelType._

trait HateoasLinks {

  private def baseSaUri(appConfig: AppConfig, nino: String): String =
    s"/${appConfig.apiGatewayContext}/$nino/self-assessment"

  private def baseCrystallisationUri(appConfig: AppConfig, nino: String, taxYear: String): String =
    s"/${appConfig.apiGatewayContext}/crystallisation/$nino/$taxYear"

  // API resource links

  def trigger(appConfig: AppConfig, nino: String, taxYear: String, finalDeclaration: Boolean): Link =
    Link(href = s"${baseSaUri(appConfig, nino)}/$taxYear?finalDeclaration=$finalDeclaration", method = POST, rel = TRIGGER)

  def list(appConfig: AppConfig, nino: String): Link =
    Link(href = baseSaUri(appConfig, nino), method = GET, rel = SELF)

  def getMetadata(appConfig: AppConfig, nino: String, calcId: String, isSelf: Boolean): Link =
    Link(href = baseSaUri(appConfig, nino) + s"/$calcId", method = GET, rel = if (isSelf) SELF else METADATA)

  def getIncomeTax(appConfig: AppConfig, nino: String, calcId: String, isSelf: Boolean): Link =
    Link(href = baseSaUri(appConfig, nino) + s"/$calcId/income-tax-nics-calculated", method = GET, rel = if (isSelf) SELF else INCOME_TAX)

  def getTaxableIncome(appConfig: AppConfig, nino: String, calcId: String, isSelf: Boolean): Link =
    Link(href = baseSaUri(appConfig, nino) + s"/$calcId/taxable-income", method = GET, rel = if (isSelf) SELF else TAXABLE_INCOME)

  def getAllowances(appConfig: AppConfig, nino: String, calcId: String, isSelf: Boolean): Link =
    Link(href = baseSaUri(appConfig, nino) + s"/$calcId/allowances-deductions-reliefs", method = GET, rel = if (isSelf) SELF else ALLOWANCES)

  def getEoyEstimate(appConfig: AppConfig, nino: String, calcId: String, isSelf: Boolean): Link =
    Link(href = baseSaUri(appConfig, nino) + s"/$calcId/end-of-year-estimate", method = GET, rel = if (isSelf) SELF else EOY_ESTIMATE)

  def getMessages(appConfig: AppConfig, nino: String, calcId: String, isSelf: Boolean): Link =
    Link(href = baseSaUri(appConfig, nino) + s"/$calcId/messages", method = GET, rel = if (isSelf) SELF else MESSAGES)

  def crystallise(appConfig: AppConfig, nino: String, taxYear: String): Link =
    Link(href = baseCrystallisationUri(appConfig, nino, taxYear) + "/crystallise", method = POST, rel = CRYSTALLISE)

}
