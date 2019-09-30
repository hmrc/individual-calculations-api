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
import v1.fixtures.getEndOfYearEstimate.EoyEstimateResponseFixture
import v1.models.response.getEndOfYearEstimate.EoyEstimateResponse

class EoyEstimateWrapperOrErrorSpec extends UnitSpec {

  "reading from JSON" when {
    "error count positive" should {
      val json =
        Json.parse(s"""{
                     |  "endOfYearEstimate": ${EoyEstimateResponseFixture.outputJson},
                     |  "metadata": {
                     |    "id": "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c",
                     |    "taxYear": "2018-19",
                     |    "requestedBy": "customer",
                     |    "calculationReason": "customerRequest",
                     |    "calculationTimestamp": "2019-11-15T09:35:15.094Z",
                     |    "calculationType": "inYear",
                     |    "intentToCrystallise": true,
                     |    "crystallised": false,
                     |    "calculationErrorCount": 1
                     |  }
                     |}
                     |""".stripMargin)

      "return.EoyErrorMessages" in {
        json.as[EoyEstimateWrapperOrError] shouldBe EoyEstimateWrapperOrError.EoyErrorMessages
      }
    }

    "error count positive and no endOfYearEstimate at all" should {
      val json =
        Json.parse("""{
                     |  "metadata": {
                     |    "id": "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c",
                     |    "taxYear": "2018-19",
                     |    "requestedBy": "customer",
                     |    "calculationReason": "customerRequest",
                     |    "calculationTimestamp": "2019-11-15T09:35:15.094Z",
                     |    "calculationType": "inYear",
                     |    "intentToCrystallise": true,
                     |    "crystallised": false,
                     |    "calculationErrorCount": 1
                     |  }
                     |}
                     |""".stripMargin)

      "return.EoyErrorMessages" in {
        json.as[EoyEstimateWrapperOrError] shouldBe EoyEstimateWrapperOrError.EoyErrorMessages
      }
    }

    "no error count" should {
      val json = Json.parse(
        s"""{
          |  "endOfYearEstimate": ${EoyEstimateResponseFixture.outputJson},
          |  "metadata": {
          |    "id": "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c",
          |    "taxYear": "2018-19",
          |    "requestedBy": "customer",
          |    "calculationReason": "customerRequest",
          |    "calculationTimestamp": "2019-11-15T09:35:15.094Z",
          |    "calculationType": "inYear",
          |    "intentToCrystallise": true,
          |    "crystallised": false
          |  }
          |}
          |""".stripMargin)

      "return a EoyEstimateWrapper" in {
        json.as[EoyEstimateWrapperOrError] shouldBe EoyEstimateWrapperOrError.EoyEstimateWrapper(EoyEstimateResponseFixture.model)
      }
    }

    "error count zero" should {
      val json = Json.parse(
        s"""{
          |  "endOfYearEstimate": ${EoyEstimateResponseFixture.outputJson},
          |  "metadata": {
          |    "id": "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c",
          |    "taxYear": "2018-19",
          |    "requestedBy": "customer",
          |    "calculationReason": "customerRequest",
          |    "calculationTimestamp": "2019-11-15T09:35:15.094Z",
          |    "calculationType": "inYear",
          |    "intentToCrystallise": true,
          |    "crystallised": false,
          |    "calculationErrorCount": 0
          |  }
          |}
          |""".stripMargin)

      "return a EoyEstimateWrapper" in {
        json.as[EoyEstimateWrapperOrError] shouldBe EoyEstimateWrapperOrError.EoyEstimateWrapper(EoyEstimateResponseFixture.model)
      }
    }

    "no calculation type" should {
      val json = Json.parse(
        s"""{
           |  "endOfYearEstimate": ${EoyEstimateResponseFixture.outputJson},
           |  "metadata": {
           |    "id": "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c",
           |    "taxYear": "2018-19",
           |    "requestedBy": "customer",
           |    "calculationReason": "customerRequest",
           |    "calculationTimestamp": "2019-11-15T09:35:15.094Z",
           |    "intentToCrystallise": true,
           |    "crystallised": false
           |  }
           |}
           |""".stripMargin)

      "return a EoyEstimateWrapper" in {
        json.as[EoyEstimateWrapperOrError] shouldBe EoyEstimateWrapperOrError.EoyEstimateWrapper(EoyEstimateResponseFixture.model)
      }
    }

    "calculation type of crystallisation" should {
      val json = Json.parse(
        s"""{
           |  "endOfYearEstimate": ${EoyEstimateResponseFixture.outputJson},
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

      "return a EoyEstimateWrapper" in {
        json.as[EoyEstimateWrapperOrError] shouldBe EoyEstimateWrapperOrError.EoyCrystallisedError
      }
    }
  }
}
