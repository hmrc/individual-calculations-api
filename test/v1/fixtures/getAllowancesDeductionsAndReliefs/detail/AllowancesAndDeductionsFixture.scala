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

package v1.fixtures.getAllowancesDeductionsAndReliefs.detail

import play.api.libs.json.{JsObject, Json}
import v1.models.response.getAllowancesDeductionsAndReliefs.detail.AllowancesAndDeductions

object AllowancesAndDeductionsFixture {

  val allowancesAndDeductionsModel: AllowancesAndDeductions =
    AllowancesAndDeductions(
      personalAllowance = Some(12500),
      reducedPersonalAllowance = Some(12500),
      giftOfInvestmentsAndPropertyToCharity = Some(12500),
      blindPersonsAllowance = Some(12500),
      lossesAppliedToGeneralIncome = Some(12500)
    )

  val allowancesAndDeductionsJson: JsObject = Json.parse(
    """
      |{
      |  "personalAllowance": 12500,
      |  "reducedPersonalAllowance": 12500,
      |  "giftOfInvestmentsAndPropertyToCharity": 12500,
      |  "blindPersonsAllowance": 12500,
      |  "lossesAppliedToGeneralIncome": 12500
      |}
    """.stripMargin
  ).as[JsObject]
}