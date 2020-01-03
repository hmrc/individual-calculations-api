/*
 * Copyright 2020 HM Revenue & Customs
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

package v1.fixtures.errors

import play.api.libs.json.{JsValue, Json}
import v1.models.errors.{BadRequestError, MtdErrors, NinoFormatError, TaxYearFormatError}

object MtdErrorsFixture {

  val statusCode: Int = 123

  val mtdErrorsModelSingle: MtdErrors = MtdErrors(statusCode = statusCode, error = NinoFormatError)

  val mtdErrorsModelMultiple: MtdErrors = MtdErrors(
    statusCode = statusCode,
    error = BadRequestError,
    errors = Some(Seq(NinoFormatError, TaxYearFormatError)
    )
  )

  val mtdErrorsJsonSingle: JsValue = Json.parse(
    """
      |{
      |   "code": "FORMAT_NINO",
      |   "message": "The provided NINO is invalid"
      |}
    """.stripMargin
  )

  val mtdErrorsJsonMultiple: JsValue = Json.parse(
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
}
