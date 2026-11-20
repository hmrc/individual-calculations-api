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

package v9.retrieveProvisionalFiguresInformation.model.response

import api.utils.UnitSpec
import play.api.libs.json.{JsError, JsObject, Json}
import v9.retrieveProvisionalFiguresInformation.RetrieveProvisionalFiguresInformationFixtures.*

class RetrieveProvisionalFiguresInformationResponseSpec extends UnitSpec {

  "RetrieveProvisionalFiguresInformationResponse" when {
    "read from valid JSON" should {
      "produce the expected response model" in {
        responseDownstreamJson.as[RetrieveProvisionalFiguresInformationResponse] shouldBe responseModel
      }
    }

    "read from valid JSON with only the mandatory fields" should {
      "produce the expected response model" in {
        minimumResponseDownstreamJson.as[RetrieveProvisionalFiguresInformationResponse] shouldBe minimumResponseModel
      }
    }

    "read from invalid JSON" should {
      "produce a JsError" in {
        JsObject.empty.validate[RetrieveProvisionalFiguresInformationResponse] shouldBe a[JsError]
      }
    }

    "written to JSON" should {
      "produce the expected JSON when all fields are present" in {
        Json.toJson(responseModel) shouldBe responseMtdJson
      }

      "produce the expected JSON when only the mandatory fields are present" in {
        Json.toJson(minimumResponseModel) shouldBe minimumResponseMtdJson
      }
    }
  }

}
