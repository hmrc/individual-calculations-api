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

package v9.createAmendProvisionalFiguresInformation

import api.models.domain.{Nino, TaxYear}
import api.models.errors.{BadRequestError, ErrorWrapper, RuleTaxYearNotEndedError}
import api.utils.UnitSpec
import play.api.libs.json.{JsValue, Json}
import v9.createAmendProvisionalFiguresInformation.model.request.{
  CreateAmendProvisionalFiguresInformationRequestBody,
  CreateAmendProvisionalFiguresInformationRequestData
}

class CreateAmendProvisionalFiguresInformationValidatorSpec extends UnitSpec {

  private implicit val correlationId: String = "1234"

  private val validNino    = "ZG903729C"
  private val validTaxYear = "2026-27"

  private val validBody: JsValue = Json.obj(
    "provisionalFiguresIndicator" -> true,
    "expectedDate"                -> "2027-10-05",
    "supplementaryInformation"    -> "Provisional figures are being confirmed."
  )

  private def validator(body: Option[JsValue], temporalValidationEnabled: Boolean = false) =
    new CreateAmendProvisionalFiguresInformationValidator(validNino, validTaxYear, body, temporalValidationEnabled)

  "CreateAmendProvisionalFiguresInformationValidator" should {
    "parse the body and path parameters when the request is valid" in {
      validator(Some(validBody)).validateAndWrapResult() shouldBe Right(
        CreateAmendProvisionalFiguresInformationRequestData(
          Nino(validNino),
          TaxYear.fromMtd(validTaxYear),
          CreateAmendProvisionalFiguresInformationRequestBody(
            provisionalFiguresIndicator = true,
            expectedDate = Some("2027-10-05"),
            supplementaryInformation = Some("Provisional figures are being confirmed.")
          )
        )
      )
    }

    "return an invalid request error when the body is absent or invalid" in {
      validator(None).validateAndWrapResult() shouldBe Left(ErrorWrapper(correlationId, BadRequestError))
      validator(Some(Json.obj())).validateAndWrapResult() shouldBe Left(ErrorWrapper(correlationId, BadRequestError))
    }

    "return a tax year not ended error when temporal validation is enabled" in {
      validator(Some(validBody), temporalValidationEnabled = true).validateAndWrapResult() shouldBe
        Left(ErrorWrapper(correlationId, RuleTaxYearNotEndedError))
    }
  }

}
