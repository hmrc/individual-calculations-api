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

package shared.models.domain

import play.api.libs.json.{Json, OWrites, Reads}
import shared.utils.UnitSpec

class TimestampSpec extends UnitSpec {

  private val response = AnyDownstreamResponse(3, "payments", Timestamp("2023-01-20T01:20:30.000Z"))

  private val responseJs = Json.parse("""
      | {
      |   "amount": 3,
      |   "category": "payments",
      |   "lastUpdated": "2023-01-20T01:20:30.000Z"
      | }
      | """.stripMargin)

  private val responseJsNoMillis = Json.parse(""" {
      |   "amount": 3,
      |   "category": "payments",
      |   "lastUpdated": "2023-01-20T01:20:30Z"
      | }
      | """.stripMargin)

  "Timestamp.apply()" should {
    "parse correctly and return a ts with milliseconds" when {
      "given a ts without milliseconds" in {
        val str    = "2023-01-20T01:20:30Z"
        val result = Timestamp(str)
        result.value shouldBe "2023-01-20T01:20:30.000Z"
      }

      "given a ts with milliseconds" in {
        val str    = "2023-01-20T01:20:30.123Z"
        val result = Timestamp(str)
        result.value shouldBe str
      }

      "given a ts with > millisecond precision" in {
        val str    = "2023-01-20T01:20:30.123456789Z"
        val result = Timestamp(str)
        result.value shouldBe "2023-01-20T01:20:30.123Z"
      }

      "given the following ts formats" in {
        Timestamp("2021-06-17T10:53:38Z").value shouldBe "2021-06-17T10:53:38.000Z"
        Timestamp("2021-06-17T10:53:38.1Z").value shouldBe "2021-06-17T10:53:38.100Z"
        Timestamp("2021-06-17T10:53:38.12Z").value shouldBe "2021-06-17T10:53:38.120Z"
        Timestamp("2021-06-17T10:53:38.123Z").value shouldBe "2021-06-17T10:53:38.123Z"

        withClue("more than 3 (4,5,6 ) digit precision") {
          Timestamp("2021-06-17T10:53:38.1234Z").value shouldBe "2021-06-17T10:53:38.123Z"
        }
      }
    }
  }

  "Timestamp deserialized from a JSON string field" should {
    "parse correctly" when {

      "the JSON string has milliseconds" in {
        val result = responseJs.as[AnyDownstreamResponse]
        result shouldBe response
      }

      "the JSON string has no milliseconds" in {
        val result = responseJsNoMillis.as[AnyDownstreamResponse]
        result shouldBe response
      }
    }
  }

  "Timestamp serialised to a JSON string field" should {
    "serialise correctly" in {
      val result = Json.toJson(response)
      result shouldBe responseJs
    }
  }

  private case class AnyDownstreamResponse(amount: Int, category: String, lastUpdated: Timestamp)

  private object AnyDownstreamResponse {
    implicit val reads: Reads[AnyDownstreamResponse]    = Json.reads[AnyDownstreamResponse]
    implicit val writes: OWrites[AnyDownstreamResponse] = Json.writes[AnyDownstreamResponse]
  }

}
