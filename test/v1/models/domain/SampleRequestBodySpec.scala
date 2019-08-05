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

package v1.models.domain

import play.api.libs.json._
import support.UnitSpec
import v1.models.utils.JsonErrorValidators

class SampleRequestBodySpec extends UnitSpec with JsonErrorValidators {
  "reads" when {
    "passed valid JSON" should {
      val inputJson = Json.parse(
        """
          |{
          |   "data": "someData"
          |}
        """.stripMargin
      )

      "return a valid model" in {
        SampleRequestBody("someData") shouldBe inputJson.as[SampleRequestBody]
      }

      testMandatoryProperty[SampleRequestBody](inputJson)("/data")

      testPropertyType[SampleRequestBody](inputJson)(
        path = "/data",
        replacement = 12344.toJson,
        expectedError = JsonError.STRING_FORMAT_EXCEPTION
      )
    }
  }
}
