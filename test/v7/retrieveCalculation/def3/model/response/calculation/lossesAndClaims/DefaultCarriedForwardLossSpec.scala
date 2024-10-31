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

package v7.retrieveCalculation.def3.model.response.calculation.lossesAndClaims

import play.api.libs.json.{JsValue, Json}
import shared.models.domain.TaxYear
import shared.utils.UnitSpec
import v7.common.model.response.IncomeSourceType

class DefaultCarriedForwardLossSpec extends UnitSpec {

  def downstreamJson(incomeSourceType: String): JsValue = Json.parse(s"""
       |{
       |  "incomeSourceId": "123456789012345",
       |  "incomeSourceType": "$incomeSourceType",
       |  "taxYearLossIncurred": 2019,
       |  "currentLossValue": 456
       |}
       |""".stripMargin)

  def model(incomeSourceType: IncomeSourceType): DefaultCarriedForwardLoss =
    DefaultCarriedForwardLoss(
      incomeSourceId = "123456789012345",
      incomeSourceType = incomeSourceType,
      taxYearLossIncurred = TaxYear.fromDownstream("2019"),
      currentLossValue = BigInt(456)
    )

  def mtdJson(incomeSourceType: IncomeSourceType): JsValue = Json.parse(s"""
       |{
       |  "incomeSourceId": "123456789012345",
       |  "incomeSourceType": "$incomeSourceType",
       |  "taxYearLossIncurred": "2018-19",
       |  "currentLossValue": 456
       |}
       |""".stripMargin)

  case class Test(downstreamIncomeSourceType: String, incomeSourceType: IncomeSourceType)

  val testData: Seq[Test] = Seq[Test](
    Test("01", IncomeSourceType.`self-employment`),
    Test("02", IncomeSourceType.`uk-property`),
    Test("15", IncomeSourceType.`foreign-property`)
  )

  "reads" should {
    "successfully read in a model" when {

      testData.foreach { case Test(downstreamIncomeSourceType, incomeSourceType) =>
        s"provided downstream income source type $downstreamIncomeSourceType" in {
          downstreamJson(downstreamIncomeSourceType).as[DefaultCarriedForwardLoss] shouldBe
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
