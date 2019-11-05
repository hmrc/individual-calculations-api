/*
 * Copyright 2019 HM Revenue & Customs
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

package v1.models.response.getIncomeTaxAndNics

import play.api.libs.json.{JsValue, Json}
import support.UnitSpec
import v1.mocks.MockAppConfig
import v1.models.hateoas.Link
import v1.models.hateoas.Method.GET
import v1.models.response.getIncomeTaxAndNics.detail.{CalculationDetail, IncomeTaxDetail, IncomeTypeBreakdown}
import v1.models.response.getIncomeTaxAndNics.summary.{CalculationSummary, IncomeTaxSummary}

class GetIncomeTaxAndNicsResponseSpec extends  UnitSpec with MockAppConfig{

  val incomeTaxSummary = IncomeTaxSummary(100.25, None, None)
  val incomeTaxDetail = IncomeTaxDetail(Some(IncomeTypeBreakdown(200.25, 300.25, None)), None, None, None)
  val summaryModel = CalculationSummary(incomeTaxSummary, None, None, None, 400.25, "UK")
  val detailModel = CalculationDetail(incomeTaxDetail, None, None)
  val model = GetIncomeTaxAndNicsResponse(summaryModel, detailModel)

  val nino: String = "AA123456A"
  val calculationId: String = "calcId"

  val outputJson: JsValue = Json.parse(
    s"""
      |{
      | "summary" : ${Json.toJson(summaryModel).toString()},
      | "detail" : ${Json.toJson(detailModel).toString()}
      |}
    """.stripMargin)

  val inputJson: JsValue = Json.parse(
    s"""
       |{
       | "incomeTaxAndNicsCalculated": {
       |   "summary" : ${Json.toJson(summaryModel).toString()},
       |   "detail" : ${Json.toJson(detailModel).toString()}
       | }
       |}
    """.stripMargin)

  "GetIncomeTaxCalcResponse" should {

    "write to json correctly" in {
      Json.toJson(model) shouldBe outputJson
    }

    "read from json correctly" in {
      inputJson.as[GetIncomeTaxAndNicsResponse] shouldBe model
    }
  }

  "Links Factory" should {

    "expose the correct links for retrieve" in {
      MockedAppConfig.apiGatewayContext.returns("individuals/calculations").anyNumberOfTimes
      GetIncomeTaxAndNicsResponse.LinksFactory.links(mockAppConfig, TaxAndNicsHateoasData(nino, calculationId)) shouldBe
        Seq(
          Link(s"/individuals/calculations/$nino/self-assessment/$calculationId", GET, "metadata"),
          Link(s"/individuals/calculations/$nino/self-assessment/$calculationId/income-tax-nics-calculated", GET, "self")
        )
    }
  }

}
