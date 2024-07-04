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

package shared.models.errors

import play.api.libs.json.Json
import shared.models.audit.AuditError
import shared.utils.UnitSpec

class ErrorWrapperSpec extends UnitSpec {

  private val correlationId = "X-123"

  private val ninoFormatJson = Json.parse(
    s"""
       |{
       |   "code": "${NinoFormatError.code}",
       |   "message": "${NinoFormatError.message}"
       |}
      """.stripMargin
  )

  "Rendering a error response with one error" should {
    val error = ErrorWrapper(correlationId, NinoFormatError, Some(Seq.empty))

    "generate the correct JSON" in {
      val result = Json.toJson(error)
      result shouldBe ninoFormatJson
    }
  }

  "Rendering a error response with one error and an empty sequence of errors" should {
    val error = ErrorWrapper(correlationId, NinoFormatError, Some(Seq.empty))

    "generate the correct JSON" in {
      Json.toJson(error) shouldBe ninoFormatJson
    }
  }

  "Rendering a error response with two errors" should {
    val error = ErrorWrapper(
      correlationId,
      BadRequestError,
      Some(
        List(
          NinoFormatError,
          TaxYearFormatError
        )
      ))

    val json = Json.parse(
      s"""
         |{
         |   "code": "${BadRequestError.code}",
         |   "message": "${BadRequestError.message}",
         |   "errors": [
         |       {
         |         "code": "${NinoFormatError.code}",
         |         "message": "${NinoFormatError.message}"
         |       },
         |       {
         |         "code": "${TaxYearFormatError.code}",
         |         "message": "${TaxYearFormatError.message}"
         |       }
         |   ]
         |}
      """.stripMargin
    )

    "generate the correct JSON" in {
      val result = Json.toJson(error)
      result shouldBe json
    }
  }

  "auditErrors" should {
    "return the correct AuditError list" when {
      "given a single error" in {
        val errorWrapper = ErrorWrapper(correlationId, BadRequestError, None)
        val result       = errorWrapper.auditErrors
        result shouldBe List(AuditError(BadRequestError.code))
      }

      "given multiple errors" in {
        val errorWrapper = ErrorWrapper(correlationId, BadRequestError, Some(Seq(NinoFormatError, TaxYearFormatError)))
        val result       = errorWrapper.auditErrors
        result shouldBe List(AuditError(NinoFormatError.code), AuditError(TaxYearFormatError.code))
      }
    }
  }

}
