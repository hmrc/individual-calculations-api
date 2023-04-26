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

package v3.controllers.requestParsers

import api.models.domain.{Nino, TaxYear}
import api.models.errors.{BadRequestError, ErrorWrapper, NinoFormatError, TaxYearFormatError}
import support.UnitSpec
import v3.mocks.validators.MockRetrieveCalculationValidator
import v3.models.request.{RetrieveCalculationRawData, RetrieveCalculationRequest}

class RetrieveCalculationParserSpec extends UnitSpec {

  val nino: String                   = "AA123456B"
  val taxYear: String                = "2017-18"
  val calculationId: String          = "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c"
  implicit val correlationId: String = "a1e8057e-fbbc-47a8-a8b4-78d9f015c253"

  trait Test extends MockRetrieveCalculationValidator {
    lazy val parser = new RetrieveCalculationParser(mockValidator)
  }

  "parse" when {
    "valid input" should {
      "parse the request" in new Test {
        val data: RetrieveCalculationRawData = RetrieveCalculationRawData(nino, taxYear, calculationId)
        MockValidator.validate(data).returns(Nil)

        parser.parseRequest(data) shouldBe Right(RetrieveCalculationRequest(Nino(nino), TaxYear.fromMtd(taxYear), calculationId))
      }
    }

    "single validation error" should {
      "return the error" in new Test {
        val data: RetrieveCalculationRawData = RetrieveCalculationRawData(nino, taxYear, calculationId)
        MockValidator.validate(data).returns(List(NinoFormatError))

        parser.parseRequest(data) shouldBe Left(ErrorWrapper(correlationId, NinoFormatError, None))
      }
    }

    "multiple validation errors" should {
      "return the errors" in new Test {
        val data: RetrieveCalculationRawData = RetrieveCalculationRawData(nino, taxYear, calculationId)
        MockValidator.validate(data).returns(List(NinoFormatError, TaxYearFormatError))

        parser.parseRequest(data) shouldBe
          Left(ErrorWrapper(correlationId, BadRequestError, Some(Seq(NinoFormatError, TaxYearFormatError))))
      }
    }
  }

}
