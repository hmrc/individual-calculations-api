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
package v1.models.response

import play.api.libs.json.{JsPath, Json, Reads}
import support.UnitSpec

class CalculationWrapperOrErrorTest extends UnitSpec {

  case class Calculation(value: String)

  object Calculation {
    implicit val reads: Reads[Calculation] =
      (JsPath \ "calculation").read[Calculation](Json.reads[Calculation])
  }

  "reading from JSON" when {
    "error count positive" should {
      val json =
        Json.parse("""{
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
          |""".stripMargin)

      "return ErrorsInCalculation" in {
        json.as[CalculationWrapperOrError[Calculation]] shouldBe CalculationWrapperOrError.ErrorsInCalculation
      }
    }

    "error count positive and no calculation at all" should {
      val json =
        Json.parse("""{
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
                     |""".stripMargin)

      "return ErrorsInCalculation" in {
        json.as[CalculationWrapperOrError[Calculation]] shouldBe CalculationWrapperOrError.ErrorsInCalculation
      }
    }

    "no error count" should {
      val json = Json.parse(
        """{
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
          |""".stripMargin)

      "return a CalculationWrapper" in {
        json.as[CalculationWrapperOrError[Calculation]] shouldBe CalculationWrapperOrError.CalculationWrapper(Calculation("someValue"))
      }
    }

    // for completeness...
    "error count zero" should {
      val json = Json.parse(
        """{
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
          |""".stripMargin)

      "return a CalculationWrapper" in {
        json.as[CalculationWrapperOrError[Calculation]] shouldBe CalculationWrapperOrError.CalculationWrapper(Calculation("someValue"))
      }
    }
  }
}
