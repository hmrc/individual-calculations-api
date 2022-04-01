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

package v3.fixtures.getIncomeTaxAndNics.detail.marriageAllowanceTransferredIn

import play.api.libs.json.{JsValue, Json}
import v3.models.response.getIncomeTaxAndNics.detail.marriageAllowanceTransferredIn.MarriageAllowanceTransferredIn

object MarriageAllowanceTransferredInFixture {

  val marriageAllowanceTransferredInModel: MarriageAllowanceTransferredIn =
    MarriageAllowanceTransferredIn(
      amount = Some(100.25),
      rate = Some(99.99)
    )

  val marriageAllowanceTransferredInJson: JsValue = Json.parse(
    s"""
       |{
       |  "amount": 100.25,
       |  "rate": 99.99
       |}
       |""".stripMargin
  )
}
