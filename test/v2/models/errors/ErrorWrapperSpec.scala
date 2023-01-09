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

package v2.models.errors

import play.api.http.Status._
import play.api.libs.json.Json
import support.UnitSpec
import v2.models.audit.AuditError

class ErrorWrapperSpec extends UnitSpec {

  val correlationId: String = "X-123"

  "Rendering a error response with one error" should {
    val error = ErrorWrapper(correlationId, NinoFormatError, Some(Seq.empty), BAD_REQUEST)

    val json = Json.parse(
      """
        |{
        |   "code": "FORMAT_NINO",
        |   "message": "The provided NINO is invalid"
        |}
      """.stripMargin
    )

    "generate the correct JSON" in {
      Json.toJson(error) shouldBe json
    }
  }

  "Rendering a error response with one error and an empty sequence of errors" should {
    val error = ErrorWrapper(correlationId, NinoFormatError, Some(Seq.empty), BAD_REQUEST)

    val json = Json.parse(
      """
        |{
        |   "code": "FORMAT_NINO",
        |   "message": "The provided NINO is invalid"
        |}
      """.stripMargin
    )

    "generate the correct JSON" in {
      Json.toJson(error) shouldBe json
    }
  }

  "Rendering a error response with two errors" should {
    val error = ErrorWrapper(
      correlationId = correlationId,
      error = BadRequestError,
      errors = Some(
        Seq(
          NinoFormatError,
          TaxYearFormatError
        )
      ),
      statusCode = BAD_REQUEST
    )

    val json = Json.parse(
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

    "generate the correct JSON" in {
      Json.toJson(error) shouldBe json
    }
  }

  "rendering an audit error" should {
    "render correctly" when {
      "there is one error" in {
        val errorWrapper = ErrorWrapper(correlationId, BadRequestError, None, BAD_REQUEST)

        errorWrapper.auditErrors shouldBe Seq(AuditError(BadRequestError.code))
      }
      "there are multiple errors" in {
        val errorWrapper = ErrorWrapper(correlationId, BadRequestError, Some(Seq(NinoFormatError, TaxYearFormatError)), BAD_REQUEST)

        errorWrapper.auditErrors shouldBe Seq(AuditError(NinoFormatError.code), AuditError(TaxYearFormatError.code))
      }
    }
  }

}
