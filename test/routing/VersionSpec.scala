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

package routing

import play.api.http.HeaderNames.ACCEPT
import play.api.libs.json.{JsError, JsResult, JsString, JsSuccess, JsValue}
import play.api.test.FakeRequest
import routing.Version.VersionReads
import support.UnitSpec

class VersionSpec extends UnitSpec {

  "Versions" when {
    "v4 retrieved from a request header" must {
      "work" in {
        Versions.getFromRequest(FakeRequest().withHeaders((ACCEPT, "application/vnd.hmrc.4.0+json"))) shouldBe Right(Version4)
      }
    }
    "v5 retrieved from a request header" must {
      "work" in {
        Versions.getFromRequest(FakeRequest().withHeaders((ACCEPT, "application/vnd.hmrc.5.0+json"))) shouldBe Right(Version5)
      }
    }
  }

  "VersionReads" should {
    "successfully read Version4" in {
      val versionJson: JsValue      = JsString(Version4.name)
      val result: JsResult[Version] = VersionReads.reads(versionJson)

      result shouldEqual JsSuccess(Version4)
    }

    "successfully read Version5" in {
      val versionJson: JsValue      = JsString(Version5.name)
      val result: JsResult[Version] = VersionReads.reads(versionJson)

      result shouldEqual JsSuccess(Version5)
    }

    "return error for unrecognised version" in {
      val versionJson: JsValue      = JsString("UnknownVersion")
      val result: JsResult[Version] = VersionReads.reads(versionJson)

      result shouldBe a[JsError]
    }
  }

}
