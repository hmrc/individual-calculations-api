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

package v4.fixtures.calculationWrappers

import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, JsValue, Json, Reads}
import v4.models.response.calculationWrappers.CalculationWrapperOrError
import v4.models.response.calculationWrappers.CalculationWrapperOrError.CalculationWrapper

object CalculationWrapperOrErrorFixture {

  type WrappedCalculation = CalculationWrapperOrError[TestCalculation]

  case class TestCalculation(value: String, id: String)

  object TestCalculation {

    implicit val reads: Reads[TestCalculation] = (
      (JsPath \ "calculation" \ "value").read[String] and
        (JsPath \ "metadata" \ "id").read[String]
    )(TestCalculation.apply _)

  }

  val wrappedCalculation: WrappedCalculation = CalculationWrapper(TestCalculation("someValue", "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c"))

  val calculationWrapperJsonWithErrors: JsValue = Json.parse(
    """
      |{
      |  "calculation": {
      |      "value": "someValue"
      |  },
      |  "metadata": {
      |    "id": "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c",
      |    "taxYear": "2018-19",
      |    "requestedBy": "customer",
      |    "calculationReason": "customerRequest",
      |    "calculationTimestamp": "2019-11-15T09:35:15.094Z",
      |    "calculationType": "crystallisation",
      |    "intentToCrystallise": true,
      |    "crystallised": false,
      |    "calculationErrorCount": 1
      |  }
      |}
    """.stripMargin
  )

  val calculationWrapperJsonWithoutErrors: JsValue = Json.parse(
    """
      |{
      |  "calculation": {
      |      "value": "someValue"
      |  },
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
      |  }
      |}
    """.stripMargin
  )

  val calculationWrapperJsonWithoutErrorCount: JsValue = Json.parse(
    """
      |{
      |  "calculation": {
      |      "value": "someValue"
      |  },
      |  "metadata": {
      |    "id": "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c",
      |    "taxYear": "2018-19",
      |    "requestedBy": "customer",
      |    "calculationReason": "customerRequest",
      |    "calculationTimestamp": "2019-11-15T09:35:15.094Z",
      |    "calculationType": "crystallisation",
      |    "intentToCrystallise": true,
      |    "crystallised": false
      |  }
      |}
    """.stripMargin
  )

  val calculationWrapperJsonWithoutMetadata: JsValue = Json.parse(
    """
      |{
      |  "calculation": {
      |      "value": "someValue"
      |  }
      |}
    """.stripMargin
  )

  val calculationWrapperJsonWithoutCalculation: JsValue = Json.parse(
    """
      |{
      |  "metadata": {
      |    "id": "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c",
      |    "taxYear": "2018-19",
      |    "requestedBy": "customer",
      |    "calculationReason": "customerRequest",
      |    "calculationTimestamp": "2019-11-15T09:35:15.094Z",
      |    "calculationType": "crystallisation",
      |    "intentToCrystallise": true,
      |    "crystallised": false
      |  }
      |}
    """.stripMargin
  )

}
