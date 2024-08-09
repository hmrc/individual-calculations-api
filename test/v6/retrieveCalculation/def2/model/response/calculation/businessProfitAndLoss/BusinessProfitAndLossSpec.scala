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

package v6.retrieveCalculation.def2.model.response.calculation.businessProfitAndLoss

import play.api.libs.json.{JsValue, Json}
import shared.utils.UnitSpec
import v6.common.model.response.IncomeSourceType

class BusinessProfitAndLossSpec extends UnitSpec {

  def downstreamJson(incomeSourceType: String): JsValue = Json.parse(s"""
       |{
       |  "incomeSourceId": "123456789012345",
       |  "incomeSourceType": "$incomeSourceType",
       |  "incomeSourceName": "Dave's Bar",
       |  "totalIncome": 456.00,
       |  "totalExpenses": 456.00,
       |  "netProfit": 456.00,
       |  "netLoss": 456.00,
       |  "totalAdditions": 456.00,
       |  "totalDeductions": 456.00,
       |  "accountingAdjustments": 456.00,
       |  "taxableProfit": 456,
       |  "adjustedIncomeTaxLoss": 456,
       |  "totalBroughtForwardIncomeTaxLosses": 456,
       |  "lossForCSFHL": 456,
       |  "broughtForwardIncomeTaxLossesUsed": 456,
       |  "taxableProfitAfterIncomeTaxLossesDeduction": 456,
       |  "carrySidewaysIncomeTaxLossesUsed": 456,
       |  "broughtForwardCarrySidewaysIncomeTaxLossesUsed": 456,
       |  "totalIncomeTaxLossesCarriedForward": 456,
       |  "class4Loss": 456,
       |  "totalBroughtForwardClass4Losses": 456,
       |  "broughtForwardClass4LossesUsed": 456,
       |  "carrySidewaysClass4LossesUsed": 456,
       |  "totalClass4LossesCarriedForward": 456
       |}
       |""".stripMargin)

  def model(incomeSourceType: IncomeSourceType): BusinessProfitAndLoss =
    BusinessProfitAndLoss(
      incomeSourceId = "123456789012345",
      incomeSourceType = incomeSourceType,
      incomeSourceName = Some("Dave's Bar"),
      totalIncome = Some(BigDecimal(456.00)),
      totalExpenses = Some(BigDecimal(456.00)),
      netProfit = Some(BigDecimal(456.00)),
      netLoss = Some(BigDecimal(456.00)),
      totalAdditions = Some(BigDecimal(456.00)),
      totalDeductions = Some(BigDecimal(456.00)),
      accountingAdjustments = Some(BigDecimal(456.00)),
      taxableProfit = Some(BigInt(456)),
      adjustedIncomeTaxLoss = Some(BigInt(456)),
      totalBroughtForwardIncomeTaxLosses = Some(BigInt(456)),
      lossForCSFHL = Some(BigInt(456)),
      broughtForwardIncomeTaxLossesUsed = Some(BigInt(456)),
      taxableProfitAfterIncomeTaxLossesDeduction = Some(BigInt(456)),
      carrySidewaysIncomeTaxLossesUsed = Some(BigInt(456)),
      broughtForwardCarrySidewaysIncomeTaxLossesUsed = Some(BigInt(456)),
      totalIncomeTaxLossesCarriedForward = Some(BigInt(456)),
      class4Loss = Some(BigInt(456)),
      totalBroughtForwardClass4Losses = Some(BigInt(456)),
      broughtForwardClass4LossesUsed = Some(BigInt(456)),
      carrySidewaysClass4LossesUsed = Some(BigInt(456)),
      totalClass4LossesCarriedForward = Some(BigInt(456))
    )

  def mtdJson(incomeSourceType: IncomeSourceType): JsValue = Json.parse(s"""
      |{
      | "incomeSourceId": "123456789012345",
      | "incomeSourceType": "$incomeSourceType",
      | "incomeSourceName": "Dave's Bar",
      | "totalIncome": 456.00,
      | "totalExpenses": 456.00,
      | "netProfit": 456.00,
      | "netLoss": 456.00,
      | "totalAdditions": 456.00,
      | "totalDeductions": 456.00,
      | "accountingAdjustments": 456.00,
      | "taxableProfit": 456,
      | "adjustedIncomeTaxLoss": 456,
      | "totalBroughtForwardIncomeTaxLosses": 456,
      | "lossForCSFHL": 456,
      | "broughtForwardIncomeTaxLossesUsed": 456,
      | "taxableProfitAfterIncomeTaxLossesDeduction": 456,
      | "carrySidewaysIncomeTaxLossesUsed": 456,
      | "broughtForwardCarrySidewaysIncomeTaxLossesUsed": 456,
      | "totalIncomeTaxLossesCarriedForward": 456,
      | "class4Loss": 456,
      | "totalBroughtForwardClass4Losses": 456,
      | "broughtForwardClass4LossesUsed": 456,
      | "carrySidewaysClass4LossesUsed": 456,
      | "totalClass4LossesCarriedForward": 456
      |}
      |""".stripMargin)

  case class Test(downstreamIncomeSourceType: String, incomeSourceType: IncomeSourceType)

  val testData: Seq[Test] = Seq[Test](
    Test("01", IncomeSourceType.`self-employment`),
    Test("02", IncomeSourceType.`uk-property-non-fhl`),
    Test("03", IncomeSourceType.`foreign-property-fhl-eea`),
    Test("04", IncomeSourceType.`uk-property-fhl`),
    Test("15", IncomeSourceType.`foreign-property`)
  )

  "reads" should {
    "successfully read in a model" when {

      testData.foreach { case Test(downstreamIncomeSourceType, incomeSourceType) =>
        s"provided downstream income source type $downstreamIncomeSourceType" in {
          downstreamJson(downstreamIncomeSourceType).as[BusinessProfitAndLoss] shouldBe
            model(incomeSourceType)
        }
      }
    }
  }

  "writes" should {
    "successfully write a model to json" when {

      testData.foreach { case Test(_, incomeSourceType) =>
        s"provided income source type $incomeSourceType" in {
          Json.toJson(model(incomeSourceType)) shouldBe
            mtdJson(incomeSourceType)
        }
      }
    }
  }

}
