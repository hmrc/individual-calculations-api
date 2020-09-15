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

package v1.fixtures.getAllowancesDeductionsAndReliefs.detail

import play.api.libs.json.{JsValue, Json}
import v1.models.response.getAllowancesDeductionsAndReliefs.detail.PensionContributions

object PensionContributionsFixture {

  val totalPensionContributions: Option[BigDecimal] = Some(12500.01)
  val retirementAnnuityPayments: Option[BigDecimal] = Some(12501.02)
  val paymentToEmployersSchemeNoTaxRelief: Option[BigDecimal] = Some(12502.03)
  val overseasPensionSchemeContributions: Option[BigDecimal] = Some(12502.04)

  val pensionContributionsModel: PensionContributions =
    PensionContributions(
      totalPensionContributions = totalPensionContributions,
      retirementAnnuityPayments = retirementAnnuityPayments,
      paymentToEmployersSchemeNoTaxRelief = paymentToEmployersSchemeNoTaxRelief,
      overseasPensionSchemeContributions = overseasPensionSchemeContributions
    )

  val pensionContributionsJson: JsValue = Json.parse(
    s"""
      |{
      |  "totalPensionContributions": ${totalPensionContributions.get},
      |  "retirementAnnuityPayments": ${retirementAnnuityPayments.get},
      |  "paymentToEmployersSchemeNoTaxRelief": ${paymentToEmployersSchemeNoTaxRelief.get},
      |  "overseasPensionSchemeContributions": ${overseasPensionSchemeContributions.get}
      |}
    """.stripMargin
  )
}