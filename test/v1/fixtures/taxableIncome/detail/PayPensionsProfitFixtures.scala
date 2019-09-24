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

package v1.fixtures.taxableIncome.detail

import play.api.libs.json.{JsValue, Json}
import v1.models.response.taxableIncome.detail.PayPensionsProfit

object PayPensionsProfitFixtures {
  val incomeReceivedPPP: BigInt                  = 1
  val taxableIncomePPP: BigInt                   = 2
  val totalSelfEmploymentProfit: Option[BigInt]  = Some(3)
  val totalPropertyProfit: Option[BigInt]        = Some(1)
  val totalFHLPropertyProfit: Option[BigInt]     = Some(2)
  val totalUKOtherPropertyProfit: Option[BigInt] = Some(3)

  val payPensionsProfitResponse: PayPensionsProfit = PayPensionsProfit(
    incomeReceivedPPP,
    taxableIncomePPP,
    totalSelfEmploymentProfit,
    totalPropertyProfit,
    totalFHLPropertyProfit,
    totalUKOtherPropertyProfit,
    None
  )

  val payPensionsProfitResponseWithoutOptionalFields: PayPensionsProfit =
    PayPensionsProfit(incomeReceivedPPP, taxableIncomePPP, None, None, None, None, None)

  val payPensionsProfitJson: JsValue = Json.parse(s"""{
      |    "incomeReceived" : $incomeReceivedPPP,
      |    "taxableIncome" : $taxableIncomePPP,
      |    "totalSelfEmploymentProfit" : ${totalSelfEmploymentProfit.get},
      |    "totalPropertyProfit" : ${totalPropertyProfit.get},
      |    "totalFHLPropertyProfit" : ${totalFHLPropertyProfit.get},
      |    "totalUKOtherPropertyProfit" : ${totalUKOtherPropertyProfit.get}
      |}""".stripMargin)

  val payPensionsProfitJsonWithoutOptionalFields: JsValue = Json.parse(s"""{
      |    "incomeReceived" : $incomeReceivedPPP,
      |    "taxableIncome" : $taxableIncomePPP
      |}""".stripMargin)

  val payPensionsProfitInvalidJson: JsValue = Json.parse(s"""{
      |    "totalSelfEmploymentProfit" : ${totalSelfEmploymentProfit.get},
      |    "totalPropertyProfit" : ${totalPropertyProfit.get},
      |    "totalFHLPropertyProfit" : ${totalFHLPropertyProfit.get},
      |    "totalUKOtherPropertyProfit" : ${totalUKOtherPropertyProfit.get}
      |}""".stripMargin)
}
