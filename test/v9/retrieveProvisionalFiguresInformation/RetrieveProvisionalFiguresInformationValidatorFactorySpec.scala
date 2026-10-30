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

package v9.retrieveProvisionalFiguresInformation

import api.models.domain.{Nino, TaxYear}
import api.models.errors.*
import api.utils.UnitSpec
import v9.retrieveProvisionalFiguresInformation.model.request.RetrieveProvisionalFiguresInformationRequestData

class RetrieveProvisionalFiguresInformationValidatorFactorySpec extends UnitSpec {

  private implicit val correlationId: String = "1234"

  private val validNino    = "AA123456A"
  private val validTaxYear = "2026-27"

  private val validatorFactory = new RetrieveProvisionalFiguresInformationValidatorFactory

  private def validator(nino: String, taxYear: String) = validatorFactory.validator(nino, taxYear)

  "validator" should {
    "return the parsed domain object" when {
      "given a valid request" in {
        val result = validator(validNino, validTaxYear).validateAndWrapResult()

        result shouldBe Right(RetrieveProvisionalFiguresInformationRequestData(Nino(validNino), TaxYear.fromMtd(validTaxYear)))
      }
    }

    "return NinoFormatError" when {
      "given an invalid nino" in {
        val result = validator("A12344A", validTaxYear).validateAndWrapResult()

        result shouldBe Left(ErrorWrapper(correlationId, NinoFormatError))
      }
    }

    "return TaxYearFormatError" when {
      "given an invalid tax year" in {
        val result = validator(validNino, "2026").validateAndWrapResult()

        result shouldBe Left(ErrorWrapper(correlationId, TaxYearFormatError))
      }
    }

    "return RuleTaxYearRangeInvalidError" when {
      "given a tax year range of more than one year" in {
        val result = validator(validNino, "2026-28").validateAndWrapResult()

        result shouldBe Left(ErrorWrapper(correlationId, RuleTaxYearRangeInvalidError))
      }
    }

    "return RuleTaxYearNotSupportedError" when {
      "given a tax year before the minimum" in {
        val result = validator(validNino, "2025-26").validateAndWrapResult()

        result shouldBe Left(ErrorWrapper(correlationId, RuleTaxYearNotSupportedError))
      }
    }
  }

}
