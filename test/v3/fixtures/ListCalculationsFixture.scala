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

package v3.fixtures

import play.api.libs.json.{JsValue, Json}
import v3.models.domain.TaxYear
import v3.models.response.common.CalculationType
import v3.models.response.listCalculations.{Calculation, ListCalculationsResponse}

trait ListCalculationsFixture {

  val listCalculationsDownstreamJson: JsValue = Json.parse("""
      |[
      |   {
      |      "calculationId":"c432a56d-e811-474c-a26a-76fc3bcaefe5",
      |      "calculationTimestamp":"2021-07-12T07:51:43.112Z",
      |      "calculationType":"crystallisation",
      |      "requestedBy":"customer",
      |      "year":2021,
      |      "fromDate":"2021-01-01",
      |      "toDate":"2021-12-31",
      |      "totalIncomeTaxAndNicsDue":10000.12,
      |      "intentToCrystallise":true,
      |      "crystallised":true,
      |      "crystallisationTimestamp":"2021-07-13T07:51:43.112Z"
      |   }
      |]
    """.stripMargin)

  val listCalculationsMtdJson: JsValue = Json.parse("""
      |{
      |   "calculations": [
      |      {
      |         "calculationId":"c432a56d-e811-474c-a26a-76fc3bcaefe5",
      |         "calculationTimestamp":"2021-07-12T07:51:43.112Z",
      |         "calculationType":"finalDeclaration",
      |         "requestedBy":"customer",
      |         "taxYear":"2020-21",
      |         "totalIncomeTaxAndNicsDue":10000.12,
      |         "intentToSubmitFinalDeclaration":true,
      |         "finalDeclaration":true,
      |         "finalDeclarationTimestamp":"2021-07-13T07:51:43.112Z"
      |      }
      |   ]
      |}
    """.stripMargin)

  def listCalculationsMtdJsonWithHateoas(nino: String, taxYear: String): JsValue = Json.parse(s"""
    |{
    |   "calculations":[
    |      {
    |         "calculationId":"c432a56d-e811-474c-a26a-76fc3bcaefe5",
    |         "calculationTimestamp":"2021-07-12T07:51:43.112Z",
    |         "calculationType":"finalDeclaration",
    |         "requestedBy":"customer",
    |         "taxYear":"2020-21",
    |         "totalIncomeTaxAndNicsDue":10000.12,
    |         "intentToSubmitFinalDeclaration":true,
    |         "finalDeclaration":true,
    |         "finalDeclarationTimestamp":"2021-07-13T07:51:43.112Z",
    |         "links":[
    |            {
    |               "href":"/individuals/calculations/$nino/self-assessment/$taxYear/c432a56d-e811-474c-a26a-76fc3bcaefe5",
    |               "rel":"self",
    |               "method":"GET"
    |            }
    |         ]
    |      }
    |   ],
    |   "links":[
    |      {
    |         "href":"/individuals/calculations/$nino/self-assessment/$taxYear",
    |         "rel":"trigger",
    |         "method":"POST"
    |      },
    |      {
    |         "href":"/individuals/calculations/$nino/self-assessment",
    |         "rel":"self",
    |         "method":"GET"
    |      }
    |   ]
    |}
  """.stripMargin)

  val calculationModel: Calculation = Calculation(
    calculationId = "c432a56d-e811-474c-a26a-76fc3bcaefe5",
    calculationTimestamp = "2021-07-12T07:51:43.112Z",
    calculationType = CalculationType.`finalDeclaration`,
    requestedBy = Some("customer"),
    taxYear = Some(TaxYear.fromDownstreamInt(2021)),
    totalIncomeTaxAndNicsDue = 10000.12,
    intentToSubmitFinalDeclaration = Some(true),
    finalDeclaration = Some(true),
    finalDeclarationTimestamp = Some("2021-07-13T07:51:43.112Z")
  )

  val listCalculationsResponseModel: ListCalculationsResponse[Calculation] = ListCalculationsResponse(Seq(calculationModel))
}
