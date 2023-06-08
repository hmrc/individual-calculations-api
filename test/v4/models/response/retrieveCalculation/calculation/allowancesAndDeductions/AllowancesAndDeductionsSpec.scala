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

package v4.models.response.retrieveCalculation.calculation.allowancesAndDeductions

import play.api.libs.json.{JsValue, Json}
import support.UnitSpec
import utils.enums.EnumJsonSpecSupport

class AllowancesAndDeductionsSpec extends UnitSpec with EnumJsonSpecSupport {

  val model: AllowancesAndDeductions =
    AllowancesAndDeductions(
      Some(12500),
      Some(
        MarriageAllowanceTransferOut(
          1001.99,
          1001.99
        )),
      Some(12500),
      Some(12500),
      Some(12500),
      Some(12500),
      Some(12500),
      Some(1001.99),
      Some(1001.99),
      Some(1001.99),
      Some(1001.99),
      Some(
        AnnuityPayments(
          Some(1001.99),
          Some(1001.99)
        )),
      Some(1001.99),
      Some(
        PensionContributionsDetail(
          Some(1001.99),
          Some(1001.99),
          Some(1001.99)
        ))
    )

  val mtdJson: JsValue = Json.parse("""
      |{
      |      "personalAllowance": 12500,
      |      "marriageAllowanceTransferOut": {
      |        "personalAllowanceBeforeTransferOut": 1001.99,
      |        "transferredOutAmount": 1001.99
      |      },
      |      "reducedPersonalAllowance": 12500,
      |      "giftOfInvestmentsAndPropertyToCharity": 12500,
      |      "blindPersonsAllowance": 12500,
      |      "lossesAppliedToGeneralIncome": 12500,
      |      "cgtLossSetAgainstInYearGeneralIncome": 12500,
      |      "qualifyingLoanInterestFromInvestments": 1001.99,
      |      "postCessationTradeReceipts": 1001.99,
      |      "paymentsToTradeUnionsForDeathBenefits": 1001.99,
      |      "grossAnnuityPayments": 1001.99,
      |      "annuityPayments": {
      |        "reliefClaimed": 1001.99,
      |        "rate": 1001.99
      |      },
      |      "pensionContributions": 1001.99,
      |      "pensionContributionsDetail": {
      |        "retirementAnnuityPayments": 1001.99,
      |        "paymentToEmployersSchemeNoTaxRelief": 1001.99,
      |        "overseasPensionSchemeContributions": 1001.99
      |      }
      |}
      |""".stripMargin)

  val ifsJson: JsValue = Json.parse("""
      |{
      |      "personalAllowance": 12500,
      |      "marriageAllowanceTransferOut": {
      |        "personalAllowanceBeforeTransferOut": 1001.99,
      |        "transferredOutAmount": 1001.99
      |      },
      |      "reducedPersonalAllowance": 12500,
      |      "giftOfInvestmentsAndPropertyToCharity": 12500,
      |      "blindPersonsAllowance": 12500,
      |      "lossesAppliedToGeneralIncome": 12500,
      |      "cgtLossSetAgainstInYearGeneralIncome": 12500,
      |      "qualifyingLoanInterestFromInvestments": 1001.99,
      |      "post-cessationTradeReceipts": 1001.99,
      |      "paymentsToTradeUnionsForDeathBenefits": 1001.99,
      |      "grossAnnuityPayments": 1001.99,
      |      "annuityPayments": {
      |        "reliefClaimed": 1001.99,
      |        "rate": 1001.99
      |      },
      |      "pensionContributions": 1001.99,
      |      "pensionContributionsDetail": {
      |        "retirementAnnuityPayments": 1001.99,
      |        "paymentToEmployersSchemeNoTaxRelief": 1001.99,
      |        "overseasPensionSchemeContributions": 1001.99
      |      }
      |}
      |""".stripMargin)

  "reads" when {
    "passed valid JSON" should {
      "return a valid model" in {
        model shouldBe ifsJson.as[AllowancesAndDeductions]
      }
    }
  }

  "writes" when {
    "passed valid model" should {
      "return valid json" in {
        Json.toJson(model) shouldBe mtdJson
      }
    }
  }

}
