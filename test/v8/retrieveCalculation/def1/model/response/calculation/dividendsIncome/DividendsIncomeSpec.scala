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

package v8.retrieveCalculation.def1.model.response.calculation.dividendsIncome

import play.api.libs.json.{JsValue, Json}
import shared.models.utils.JsonErrorValidators
import shared.utils.UnitSpec
import v8.common.model.response.IncomeSourceType

class DividendsIncomeSpec extends UnitSpec with JsonErrorValidators {

  val ukModel: UkDividends = UkDividends(
    Some("000000000000210"),
    Some(IncomeSourceType.`uk-dividends`),
    Some(12500),
    Some(12500)
  )

  val otherModel: OtherDividends = OtherDividends(
    Some(TypeOfDividend.`stock-dividend`),
    Some("string"),
    Some(5000.99)
  )

  val foreignModel: CommonForeignDividend = CommonForeignDividend(
    Some(IncomeSourceType.`foreign-dividends`),
    "GER",
    Some(5000.99),
    Some(5000.99),
    Some(5000.99),
    Some(true)
  )

  val model: DividendsIncome = DividendsIncome(
    Some(12500),
    Some(12500),
    Some(ukModel),
    Some(Seq(otherModel)),
    Some(12500),
    Some(Seq(foreignModel)),
    Some(Seq(foreignModel))
  )

  val downstreamJson: JsValue = Json.parse(
    """
      |{
      |   "totalChargeableDividends":12500,
      |   "totalUkDividends":12500,
      |   "ukDividends":{
      |      "incomeSourceId":"000000000000210",
      |      "incomeSourceType":"10",
      |      "dividends":12500,
      |      "otherUkDividends":12500
      |   },
      |   "otherDividends":[
      |      {
      |         "typeOfDividend":"stockDividend",
      |         "customerReference":"string",
      |         "grossAmount":5000.99
      |      }
      |   ],
      |   "chargeableForeignDividends":12500,
      |   "foreignDividends":[
      |      {
      |         "incomeSourceType":"07",
      |         "countryCode":"GER",
      |         "grossIncome":5000.99,
      |         "netIncome":5000.99,
      |         "taxDeducted":5000.99,
      |         "foreignTaxCreditRelief":true
      |      }
      |   ],
      |   "dividendIncomeReceivedWhilstAbroad":[
      |      {
      |         "incomeSourceType":"07",
      |         "countryCode":"GER",
      |         "grossIncome":5000.99,
      |         "netIncome":5000.99,
      |         "taxDeducted":5000.99,
      |         "foreignTaxCreditRelief":true
      |      }
      |   ]
      |}
      |""".stripMargin
  )

  val mtdJson: JsValue = Json.parse(
    """
      |{
      |   "totalChargeableDividends":12500,
      |   "totalUkDividends":12500,
      |   "ukDividends":{
      |      "incomeSourceId":"000000000000210",
      |      "incomeSourceType":"uk-dividends",
      |      "dividends":12500,
      |      "otherUkDividends":12500
      |   },
      |   "otherDividends":[
      |      {
      |         "typeOfDividend":"stock-dividend",
      |         "customerReference":"string",
      |         "grossAmount":5000.99
      |      }
      |   ],
      |   "chargeableForeignDividends":12500,
      |   "foreignDividends":[
      |      {
      |         "incomeSourceType":"foreign-dividends",
      |         "countryCode":"GER",
      |         "grossIncome":5000.99,
      |         "netIncome":5000.99,
      |         "taxDeducted":5000.99,
      |         "foreignTaxCreditRelief":true
      |      }
      |   ],
      |   "dividendIncomeReceivedWhilstAbroad":[
      |      {
      |         "incomeSourceType":"foreign-dividends",
      |         "countryCode":"GER",
      |         "grossIncome":5000.99,
      |         "netIncome":5000.99,
      |         "taxDeducted":5000.99,
      |         "foreignTaxCreditRelief":true
      |      }
      |   ]
      |}
      |""".stripMargin
  )

  "reads" when {
    "passed valid JSON" should {
      "return a valid model" in {
        downstreamJson.as[DividendsIncome] shouldBe model
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
