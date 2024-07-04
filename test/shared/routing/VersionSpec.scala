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

package shared.routing

import org.scalamock.scalatest.MockFactory
import play.api.http.HeaderNames.ACCEPT
import play.api.libs.json._
import play.api.mvc.{Headers, RequestHeader}
import play.api.test.FakeRequest
import shared.routing.Version.VersionReads
import shared.utils.UnitSpec

class VersionSpec extends UnitSpec with MockFactory {

  "Versions" when {
    "retrieved from a request header" should {
      "return Version for valid header" in {
        Versions.getFromRequest(FakeRequest().withHeaders((ACCEPT, "application/vnd.hmrc.9.0+json"))) shouldBe Right(Version9)
      }

      "return InvalidHeader when the version header is missing" in {
        Versions.getFromRequest(FakeRequest().withHeaders()) shouldBe Left(InvalidHeader)
      }

      "return VersionNotFound for unrecognised version" in {
        Versions.getFromRequest(FakeRequest().withHeaders((ACCEPT, "application/vnd.hmrc.0.0+json"))) shouldBe Left(VersionNotFound)
      }

      "return InvalidHeader for a header format that doesn't match regex" in {
        Versions.getFromRequest(FakeRequest().withHeaders((ACCEPT, "invalidHeaderFormat"))) shouldBe Left(InvalidHeader)
      }
    }
  }

  "Version" when {
    "serialized to Json" should {
      "return the expected Json output" in {
        val version: Version = Version3

        val result = Json.toJson(version)
        result shouldBe JsString("3.0")
      }
    }
  }

  "Version.apply(RequestHeader)" when {

    def mockRequestHeader(keyValue: (String, String)): RequestHeader = {
      val (k, v)  = keyValue
      val header  = mock[RequestHeader]
      val headers = Headers(k -> v)

      (() => header.headers: Headers).expects().returning(headers)
      header
    }

    "given a valid Accept header" should {
      "return the expected API Version" in {
        val header = mockRequestHeader(ACCEPT -> "application/vnd.hmrc.9.0+json")
        val result = Version(header)
        result shouldBe Version9
      }
    }

    "given an invalid Accept header" should {
      "throw the expected exception (code shouldn't have reached this point)" in {
        val header = mockRequestHeader(ACCEPT -> "not-a-valid-request-header")
        the[Exception] thrownBy Version(header) should have message "Missing or unsupported version found in request accept header"
      }
    }
  }

  "VersionReads" should {
    "successfully read Version3" in {
      val versionJson: JsValue      = JsString(Version3.name)
      val result: JsResult[Version] = VersionReads.reads(versionJson)

      result shouldEqual JsSuccess(Version3)
    }

    "successfully read Version4" in {
      val versionJson: JsValue      = JsString(Version4.name)
      val result: JsResult[Version] = VersionReads.reads(versionJson)

      result shouldEqual JsSuccess(Version4)
    }

    "return error for unrecognised version" in {
      val versionJson: JsValue      = JsString("UnknownVersion")
      val result: JsResult[Version] = VersionReads.reads(versionJson)

      result shouldBe a[JsError]
    }
  }

  "toString" should {
    "return the version name" in {
      val result = Version3.toString
      result shouldBe Version3.name
    }
  }

}
