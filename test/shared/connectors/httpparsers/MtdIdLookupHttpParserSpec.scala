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

package shared.connectors.httpparsers

import play.api.http.Status.IM_A_TEAPOT
import play.api.libs.json.Writes.StringWrites
import play.api.libs.json.{JsResultException, Json}
import play.api.test.Helpers.OK
import shared.connectors.MtdIdLookupConnector
import shared.connectors.httpparsers.MtdIdLookupHttpParser.mtdIdLookupHttpReads
import shared.utils.UnitSpec
import uk.gov.hmrc.http.HttpResponse

class MtdIdLookupHttpParserSpec extends UnitSpec {

  private val method = "GET"
  private val url    = "test-url"

  "read" when {
    "the response contains a 200 status" when {
      "the response body contains an MtdId" must {
        "return the MtdId" in {
          val mtdId    = "test-mtd-id"
          val response = HttpResponse(OK, Json.obj("mtdbsa" -> mtdId), Map.empty[String, Seq[String]])
          val result   = mtdIdLookupHttpReads.read(method, url, response)

          result shouldBe Right(mtdId)
        }
      }

      "the response body is not valid" must {
        "throw an JsResultException" in {
          val response = HttpResponse(OK, Json.obj("hello" -> "world"), Map.empty[String, Seq[String]])

          intercept[JsResultException](mtdIdLookupHttpReads.read(method, url, response))
        }
      }
    }

    "the response contains a non-200 status" must {
      "return the status code as an error" in {
        val status   = IM_A_TEAPOT
        val response = HttpResponse(status, "ignored")
        val result   = mtdIdLookupHttpReads.read(method, url, response)

        result shouldBe Left(MtdIdLookupConnector.Error(status))
      }
    }
  }

}
