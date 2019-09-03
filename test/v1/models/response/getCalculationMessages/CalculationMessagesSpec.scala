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

package v1.models.response.getCalculationMessages

import play.api.libs.json.Json
import support.UnitSpec
import v1.fixtures.Fixtures._

class CalculationMessagesSpec extends UnitSpec {

  def messagesResponse(info: Boolean, warn: Boolean, error: Boolean): CalculationMessages =
    CalculationMessages(if (info) Some(Seq(info1, info2)) else None,
      if (warn) Some(Seq(warn1, warn2)) else None,
      if (error) Some(Seq(err1, err2)) else None)

  "GetCalculationMessages" when {

    "read from json" should {
      "read from the json as a  whole CalculationMessage" in {
        backendMessagesJson.as[CalculationMessages] shouldBe messagesResponse(info = true, warn = true, error = true)
      }

      "read from the json as Info CalculationMessage" in {
        backendMessagesInfoJson.as[CalculationMessages] shouldBe messagesResponse(info = true, warn = false, error = false)
      }

      "read from the json as Warnings CalculationMessage" in {
        backendMessagesWarningsJson.as[CalculationMessages] shouldBe messagesResponse(info = false, warn = true, error = false)
      }

      "read from the json as Errors CalculationMessage" in {
        backendMessagesErrorsJson.as[CalculationMessages] shouldBe messagesResponse(info = false, warn = false, error = true)
      }
    }
    "written to JSON" should {
      "take the full messages form" in {
        Json.toJson[CalculationMessages](messagesResponse(info = true, warn = true, error = true)) shouldBe outputMessagesJson
      }

      "display info only" in {
        Json.toJson[CalculationMessages](messagesResponse(info = true, warn = false, error = false)) shouldBe outputMessagesInfoJson
      }

      "display warnings only" in {
        Json.toJson[CalculationMessages](messagesResponse(info = false, warn = true, error = false)) shouldBe outputMessagesWarningsJson
      }

      "display errors only" in {
        Json.toJson[CalculationMessages](messagesResponse(info = false, warn = false, error = true)) shouldBe outputMessagesErrorsJson
      }
    }
  }
}
