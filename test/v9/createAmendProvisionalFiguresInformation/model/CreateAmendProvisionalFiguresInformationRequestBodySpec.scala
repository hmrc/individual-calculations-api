/*
 * Copyright 2026 HM Revenue & Customs
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

package v9.createAmendProvisionalFiguresInformation.model

import api.utils.UnitSpec
import play.api.libs.json.Json
import v9.createAmendProvisionalFiguresInformation.CreateAmendProvisionalFiguresInformationFixtures.{
  validDownstreamRequestJson,
  validMtdRequestJson,
  validMinimumMtdRequestJson,
  validMinimumDownstreamRequestJson,
  validRequestBody,
  validMinimumRequestBody
}
import v9.createAmendProvisionalFiguresInformation.model.request.CreateAmendProvisionalFiguresInformationRequestBody

class CreateAmendProvisionalFiguresInformationRequestBodySpec extends UnitSpec {

  "reads" should {
    "read from json" in {
      validMtdRequestJson.as[CreateAmendProvisionalFiguresInformationRequestBody] shouldBe validRequestBody
    }
  }

  "writes" should {
    "write to json" in {
      Json.toJson(validRequestBody) shouldBe validDownstreamRequestJson
    }
  }

  "minimum request body (provisionalFiguresIndicator only)" should {
    "read from json" in {
      validMinimumMtdRequestJson.as[CreateAmendProvisionalFiguresInformationRequestBody] shouldBe validMinimumRequestBody
    }

    "write to json" in {
      Json.toJson(validMinimumRequestBody) shouldBe validMinimumDownstreamRequestJson
    }
  }

  "JsError" should {
    "return error when mandatory field provisionalFiguresIndicator is missing" in {
      val provisionalFiguresIndicatorIsMissing = Json.obj(
        "expectedDate"             -> "2027-10-05",
        "supplementaryInformation" -> "Provisional figures used pending final confirmation."
      )

      val result = provisionalFiguresIndicatorIsMissing.validate[CreateAmendProvisionalFiguresInformationRequestBody]
      result.isError shouldBe true
    }
  }

}
