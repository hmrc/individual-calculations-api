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

package v9.retrieveCalculation.def4.model.response.calculation.otherIncome

import api.utils.UnitSpec
import play.api.libs.json.*

class PostCessationIncomeSpec extends UnitSpec {

  val postCessationReceiptJson: JsValue = Json.parse("""
      |{
      |  "amount": 5000.99,
      |  "taxYearIncomeToBeTaxed": "2026-27"
      |}
      |""".stripMargin)

  val singlePostCessationIncomeJson: JsValue = Json.parse(s"""
      |{
      |  "totalPostCessationReceipts": 5000.99,
      |  "postCessationReceipts": [$postCessationReceiptJson]
      |}
      |""".stripMargin)

  val multiplePostCessationIncomeJson: JsValue = Json.parse(s"""
       |{
       |  "totalPostCessationReceipts": 5000.99,
       |  "postCessationReceipts": [$postCessationReceiptJson, $postCessationReceiptJson]
       |}
       |""".stripMargin)

  val postCessationReceiptModel: PostCessationReceipt = PostCessationReceipt(
    amount = BigDecimal(5000.99),
    taxYearIncomeToBeTaxed = "2026-27"
  )

  val singlePostCessationIncomeModel: PostCessationIncome = PostCessationIncome(
    totalPostCessationReceipts = 5000.99,
    postCessationReceipts = Seq(postCessationReceiptModel)
  )

  val multiplePostCessationIncomeModel: PostCessationIncome = PostCessationIncome(
    totalPostCessationReceipts = 5000.99,
    postCessationReceipts = Seq(postCessationReceiptModel, postCessationReceiptModel)
  )

  "reads" should {
    "successfully read in a model with a single PostCessationReceipt" in {
      singlePostCessationIncomeJson.as[PostCessationIncome] shouldBe singlePostCessationIncomeModel
    }

    "successfully read in a model with multiple single PostCessationReceipt" in {
      multiplePostCessationIncomeJson.as[PostCessationIncome] shouldBe multiplePostCessationIncomeModel
    }
  }

  "writes" should {
    "successfully write a model with a single PostCessationReceipt to json" in {
      Json.toJson(singlePostCessationIncomeModel) shouldBe singlePostCessationIncomeJson
    }

    "successfully write a model with multiple single PostCessationReceipt to json" in {
      Json.toJson(multiplePostCessationIncomeModel) shouldBe multiplePostCessationIncomeJson
    }
  }

  "error when JSON is invalid" in {
    JsObject.empty.validate[PostCessationIncome] shouldBe a[JsError]
  }

}
