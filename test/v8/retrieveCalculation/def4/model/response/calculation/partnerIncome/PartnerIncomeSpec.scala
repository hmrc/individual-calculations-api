/*
 * Copyright 2026 HM Revenue & Customs
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

package v8.retrieveCalculation.def4.model.response.calculation.partnerIncome

import shared.utils.UnitSpec
import play.api.libs.json.*

class PartnerIncomeSpec extends UnitSpec {

  val model: PartnerIncome = PartnerIncome(
    totalPartnerIncome = 5000.99,
    listOfPartnerIncome = Some(
      Seq(
        ListOfPartnerIncome(
          partnershipUtr = "4564564564",
          partnershipTrades = Some(
            Seq(
              PartnershipTrades(
                lossesBroughtForwardAdjustedProfit = Some(5000.99),
                shareOfTaxableProfit = 5000.99,
                adjustedLoss = Some(5000.99),
                currentYearLossAppliedToGeneralIncome = Some(5000.99),
                transitionProfitArisingThisYear = Some(5000.99),
                lossesBroughtForwardTransitionProfit = Some(5000.99)
              )
            )),
          nationalInsuranceContributions = Some(
            NationalInsuranceContributions(
              voluntaryClass2Nics = Some(true),
              class4Exemption = Some(true),
              adjustmentToProfitsForClass4 = Some(5000.99)
            )),
          untaxedSavings = Some(5000.99),
          taxedSavings = Some(5000.99),
          dividendIncome = Some(5000.99),
          ukPropertyIncome = Some(
            UkPropertyIncome(
              shareOfProfitOrLoss = Some(-99999999999.99),
              profitOrLossAdjustment = Some(-99999999999.99),
              currentYearLossAppliedToGeneralIncome = Some(5000.99)
            )),
          taxedAndUntaxedIncome = Some(5000.99)
        )
      ))
  )

  val mtdJson: JsValue = Json.parse(
    """
      |{
      |      "totalPartnerIncome": 5000.99,
      |      "listOfPartnerIncome": [
      |        {
      |          "partnershipUtr": "4564564564",
      |          "partnershipTrades": [
      |            {
      |              "lossesBroughtForwardAdjustedProfit": 5000.99,
      |              "shareOfTaxableProfit": 5000.99,
      |              "adjustedLoss": 5000.99,
      |              "currentYearLossAppliedToGeneralIncome": 5000.99,
      |              "transitionProfitArisingThisYear": 5000.99,
      |              "lossesBroughtForwardTransitionProfit": 5000.99
      |            }
      |          ],
      |          "nationalInsuranceContributions": {
      |            "voluntaryClass2Nics": true,
      |            "class4Exemption": true,
      |            "adjustmentToProfitsForClass4": 5000.99
      |          },
      |          "untaxedSavings": 5000.99,
      |          "taxedSavings": 5000.99,
      |          "dividendIncome": 5000.99,
      |          "ukPropertyIncome": {
      |            "shareOfProfitOrLoss": -99999999999.99,
      |            "profitOrLossAdjustment": -99999999999.99,
      |            "currentYearLossAppliedToGeneralIncome": 5000.99
      |          },
      |          "taxedAndUntaxedIncome": 5000.99
      |        }
      |      ]
      |    }
      |""".stripMargin
  )

  val downstreamJson: JsValue = Json.parse(
    """
      {
      |      "totalPartnerIncome": 5000.99,
      |      "listOfPartnerIncome": [
      |        {
      |          "partnershipUTR": "4564564564",
      |          "partnershipTrades": [
      |            {
      |              "lossesBroughtForwardAdjustedProfit": 5000.99,
      |              "shareOfTaxableProfit": 5000.99,
      |              "adjustedLoss": 5000.99,
      |              "currentYearLossAppliedToGeneralIncome": 5000.99,
      |              "transitionProfitArisingThisYear": 5000.99,
      |              "lossesBroughtForwardTransitionProfit": 5000.99
      |            }
      |          ],
      |          "nationalInsuranceContributions": {
      |            "voluntaryClass2Nics": true,
      |            "class4Exemption": true,
      |            "adjustmentToProfitsForClass4": 5000.99
      |          },
      |          "untaxedSavings": 5000.99,
      |          "taxedSavings": 5000.99,
      |          "dividendIncome": 5000.99,
      |          "ukPropertyIncome": {
      |            "shareOfProfitOrLoss": -99999999999.99,
      |            "profitOrLossAdjustment": -99999999999.99,
      |            "currentYearLossAppliedToGeneralIncome": 5000.99
      |          },
      |          "taxedAndUntaxedIncome": 5000.99
      |        }
      |      ]
      |    }
      |""".stripMargin
  )

  "reads" when {
    "passed valid JSON" should {
      "return a valid model" in {
        downstreamJson.as[PartnerIncome] shouldBe model
      }
    }
  }

  "writes" when {
    "passed valid model" should {
      "return valid JSON" in {
        Json.toJson(model) shouldBe mtdJson
      }
    }
  }

}
