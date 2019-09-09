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

import play.api.libs.json.Json
import v1.models.response.CalculationWrapperOrError
import v1.models.response.getIncomeTaxCalc._

object GetIncomeTaxCalcFixture {

  val incomeTaxSummary = IncomeTaxSummary(100.25, None, None)
  val nicSummary = NicSummary(Some(200.25), None, None)
  val calculationSummary = CalculationSummary(incomeTaxSummary, Some(nicSummary), Some(300.25), Some(400.25), 500.25, "UK")

  val incomeTaxDetail = IncomeTaxDetail(Some(IncomeTypeBreakdown(100.25, 200.25, None)), None, None, None)
  val nicDetail = NicDetail(Some(Class2NicDetail(Some(300.25), None, None, None, underSmallProfitThreshold = true,
    actualClass2Nic = Some(false))), None)
  val taxDeductedAtSource = TaxDeductedAtSource(Some(400.25), None)
  val calculationDetail: CalculationDetail = CalculationDetail(incomeTaxDetail, Some(nicDetail), Some(taxDeductedAtSource))

  val incomeTax = IncomeTax(calculationSummary, calculationDetail)

  val getIncomeTaxCalcResponseObj = CalculationWrapperOrError.CalculationWrapper(GetIncomeTaxCalcResponse(summary = calculationSummary,
    detail = calculationDetail))

  val successBodyFromBackEnd = Json.parse(s"""
                                  |{
                                  |  "metadata": {
                                  |    "id": "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c",
                                  |    "taxYear": "2018-19",
                                  |    "requestedBy": "customer",
                                  |    "calculationReason": "customerRequest",
                                  |    "calculationTimestamp": "2019-11-15T09:35:15.094Z",
                                  |    "calculationType": "crystallisation",
                                  |    "intentToCrystallise": true,
                                  |    "crystallised": false,
                                  |    "calculationErrorCount": 0
                                  |  },
                                  |  "incomeTax": {
                                  |   "summary": {
                                  |     "incomeTax" : ${Json.toJson(incomeTaxSummary).toString()},
                                  |     "nics" : ${Json.toJson(nicSummary).toString()},
                                  |     "totalIncomeTaxNicsCharged" : 300.25,
                                  |     "totalTaxDeducted" : 400.25,
                                  |     "totalIncomeTaxAndNicsDue" : 500.25,
                                  |     "taxRegime" : "UK"
                                  |  },
                                  |  "detail": {
                                  |     "incomeTax" : ${Json.toJson(incomeTaxDetail).toString()},
                                  |     "nics" : ${Json.toJson(nicDetail).toString()},
                                  |     "taxDeductedAtSource" : ${Json.toJson(taxDeductedAtSource).toString()}
                                  |  }
                                  |  }
                                  |}""".stripMargin)

  val errorBodyFromBackEnd = Json.parse(s"""
                                 |{
                                 |  "metadata": {
                                 |    "id": "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c",
                                 |    "taxYear": "2018-19",
                                 |    "requestedBy": "customer",
                                 |    "calculationReason": "customerRequest",
                                 |    "calculationTimestamp": "2019-11-15T09:35:15.094Z",
                                 |    "calculationType": "crystallisation",
                                 |    "intentToCrystallise": true,
                                 |    "crystallised": false,
                                 |    "calculationErrorCount": 2
                                 |  },
                                 |  "messages" :{
                                 |     "errors":[
                                 |        {"id":"err1", "text":"text1"},
                                 |        {"id":"err2", "text":"text2"}
                                 |     ]
                                 |  }
                                 |}""".stripMargin)

  val successOutputToVendor = Json.parse(s"""{
                                    |  "summary": {
                                    |     "incomeTax" : ${Json.toJson(incomeTaxSummary).toString()},
                                    |     "nics" : ${Json.toJson(nicSummary).toString()},
                                    |     "totalIncomeTaxNicsCharged" : 300.25,
                                    |     "totalTaxDeducted" : 400.25,
                                    |     "totalIncomeTaxAndNicsDue" : 500.25,
                                    |     "taxRegime" : "UK"
                                    |  },
                                    |  "detail": {
                                    |     "incomeTax" : ${Json.toJson(incomeTaxDetail).toString()},
                                    |     "nics" : ${Json.toJson(nicDetail).toString()},
                                    |     "taxDeductedAtSource" : ${Json.toJson(taxDeductedAtSource).toString()}
                                    |  }
                                    |}
                                    |""".stripMargin)
}
