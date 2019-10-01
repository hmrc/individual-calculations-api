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

package v1.fixtures

import play.api.libs.json.{ JsObject, JsValue, Json }
import v1.fixtures.getTaxableIncome.TaxableIncomeFixtures.metadataJson
import v1.models.response.getAllowancesDeductionsAndReliefs._

object AllowancesDeductionsAndReliefsFixture {

  val allowancesDeductionsAndReliefsModel: AllowancesDeductionsAndReliefs = AllowancesDeductionsAndReliefs(
    CalculationSummary(totalAllowancesAndDeductions = Some(12500), totalReliefs = Some(12500)),
    CalculationDetail(
      allowancesAndDeductions = Some(
        AllowancesAndDeductions(
          personalAllowance = Some(12500),
          reducedPersonalAllowance = Some(12500),
          giftOfInvestmentsAndPropertyToCharity = Some(12500),
          blindPersonsAllowance = Some(12500),
          lossesAppliedToGeneralIncome = Some(12500)
        )
      ),
      reliefs = Some(
        Reliefs(
          residentialFinanceCosts =
            Some(ResidentialFinanceCosts(amountClaimed = 12500, allowableAmount = Some(12500), rate = 20.0, propertyFinanceRelief = 12500)))
      )
    )
  )

  val allowancesDeductionsAndReliefsJson: JsValue = Json.parse("""
                          |{
                          |  "summary": {
                          |    "totalAllowancesAndDeductions": 12500,
                          |    "totalReliefs": 12500
                          |  },
                          |  "detail":{
                          |    "allowancesAndDeductions":{
                          |      "personalAllowance": 12500,
                          |      "reducedPersonalAllowance": 12500,
                          |      "giftOfInvestmentsAndPropertyToCharity": 12500,
                          |      "blindPersonsAllowance": 12500,
                          |      "lossesAppliedToGeneralIncome": 12500
                          |    },
                          |    "reliefs": {
                          |      "residentialFinanceCosts": {
                          |        "amountClaimed": 12500,
                          |        "allowableAmount": 12500,
                          |        "rate": 20,
                          |        "propertyFinanceRelief": 12500
                          |      }
                          |    }
                          |  }
                          |}
                          |""".stripMargin)

  val jsonFromBackend: JsValue = metadataJson.as[JsObject] ++ Json.obj("allowancesDeductionsAndReliefs" -> allowancesDeductionsAndReliefsJson)

  val noAllowancesDeductionsAndReliefsExistJsonFromBackend: JsValue =
    metadataJson.as[JsObject] ++
      Json.parse("""
                 |{
                 |  "allowancesDeductionsAndReliefs": {
                 |    "summary": {
                 |    },
                 |    "detail":{
                 |    }
                 |  }
                 |}
                 |""".stripMargin).as[JsObject]

  val noAllowancesDeductionsAndReliefsExistModel: AllowancesDeductionsAndReliefs =
    AllowancesDeductionsAndReliefs(CalculationSummary(None, None), CalculationDetail(None, None))
}
