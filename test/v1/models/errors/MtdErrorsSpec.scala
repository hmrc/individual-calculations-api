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

package v1.models.errors

import play.api.libs.json.{JsValue, Json}
import support.UnitSpec

class MtdErrorsSpec extends UnitSpec {

  val correlationId = "X-123"
  val statusCode = 123

  val mtdErrorsModelSingle = MtdErrors(statusCode, NinoFormatError)
  val mtdErrorsModelMultiple: MtdErrors = MtdErrors(statusCode, BadRequestError, Some(Seq(NinoFormatError, TaxYearFormatError)))

  val mtdErrorJsonSingle: JsValue = Json.parse(
    """
      |{
      |   "code": "FORMAT_NINO",
      |   "message": "The provided NINO is invalid"
      |}
    """.stripMargin
  )

  val mtdErrorJsonMultiple: JsValue = Json.parse(
    """
      |{
      |   "code": "INVALID_REQUEST",
      |   "message": "Invalid request",
      |   "errors": [
      |       {
      |         "code": "FORMAT_NINO",
      |         "message": "The provided NINO is invalid"
      |       },
      |       {
      |         "code": "FORMAT_TAX_YEAR",
      |         "message": "The provided tax year is invalid"
      |       }
      |   ]
      |}
    """.stripMargin
  )

  "Rendering a error response with one error" should {
    "generate the correct JSON" in {
      Json.toJson(mtdErrorsModelSingle) shouldBe mtdErrorJsonSingle
    }
  }

  "Rendering a error response with one error and an empty sequence of errors" should {
    "generate the correct JSON" in {
      Json.toJson(mtdErrorsModelSingle.copy(errors = Some(Seq.empty))) shouldBe mtdErrorJsonSingle
    }
  }

  "Rendering a error response with two errors" should {
    "generate the correct JSON" in {
      Json.toJson(mtdErrorsModelMultiple) shouldBe mtdErrorJsonMultiple
    }
  }
}
