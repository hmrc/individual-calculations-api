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

package v3.models.response.retrieveCalculation.calculation.allowancesAndDeductions

import play.api.libs.json.{JsValue, Json}
import support.UnitSpec

class AllowancesAndDeductionsSpec extends UnitSpec {

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
      Some(
        PensionContributionsDetail(
          Some(1001.99),
          Some(1001.99),
          Some(1001.99)
        ))
    )

  val mtdJson: JsValue = Json.parse(
    """
      |    "allowancesAndDeductions": {
      |      "personalAllowance": 12500,
      |      "marriageAllowanceTransferOut": {
      |        "personalAllowanceBeforeTransferOut": 5000.99,
      |        "transferredOutAmount": 5000.99
      |      },
      |      "reducedPersonalAllowance": 12500,
      |      "giftOfInvestmentsAndPropertyToCharity": 12500,
      |      "blindPersonsAllowance": 12500,
      |      "lossesAppliedToGeneralIncome": 12500,
      |      "cgtLossSetAgainstInYearGeneralIncome": 12500,
      |      "qualifyingLoanInterestFromInvestments": 5000.99,
      |      "postCessationTradeReceipts": 5000.99,
      |      "paymentsToTradeUnionsForDeathBenefits": 5000.99,
      |      "grossAnnuityPayments": 5000.99,
      |      "annuityPayments": {
      |        "reliefClaimed": 5000.99,
      |        "rate": 20
      |      },
      |      "pensionContributions": 5000.99,
      |      "pensionContributionsDetail": {
      |        "retirementAnnuityPayments": 5000.99,
      |        "paymentToEmployersSchemeNoTaxRelief": 5000.99,
      |        "overseasPensionSchemeContributions": 5000.99
      |      }
      |    }
      |""".stripMargin)

  val ifsJson: JsValue = Json.parse(
    """
      |    "allowancesAndDeductions": {
      |      "personalAllowance": 12500,
      |      "marriageAllowanceTransferOut": {
      |        "personalAllowanceBeforeTransferOut": 5000.99,
      |        "transferredOutAmount": 5000.99
      |      },
      |      "reducedPersonalAllowance": 12500,
      |      "giftOfInvestmentsAndPropertyToCharity": 12500,
      |      "blindPersonsAllowance": 12500,
      |      "lossesAppliedToGeneralIncome": 12500,
      |      "cgtLossSetAgainstInYearGeneralIncome": 12500,
      |      "qualifyingLoanInterestFromInvestments": 5000.99,
      |      "post-cessationTradeReceipts": 5000.99,
      |      "paymentsToTradeUnionsForDeathBenefits": 5000.99,
      |      "grossAnnuityPayments": 5000.99,
      |      "annuityPayments": {
      |        "reliefClaimed": 5000.99,
      |        "rate": 20
      |      },
      |      "pensionContributions": 5000.99,
      |      "pensionContributionsDetail": {
      |        "retirementAnnuityPayments": 5000.99,
      |        "paymentToEmployersSchemeNoTaxRelief": 5000.99,
      |        "overseasPensionSchemeContributions": 5000.99
      |      }
      |    }
      |""".stripMargin)

  "reads" when {
    "passed valid JSON" should {
      "return a valid model" in {
        model shouldBe mtdJson.as[AllowancesAndDeductions]
      }
    }
  }
  "writes" when {
    "passed valid model" should {
      "return valid json" in {
        Json.toJson(model) shouldBe ifsJson
      }
    }
  }
}
