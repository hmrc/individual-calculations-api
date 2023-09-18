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

package v4.controllers.requestParsers

import api.models.domain.{CalculationId, Nino, TaxYear}
import api.models.errors.{BadRequestError, ErrorWrapper, NinoFormatError, TaxYearFormatError}
import support.UnitSpec
import v4.mocks.validators.MockSubmitFinalDeclarationTriggerValidator
import v4.models.request.{SubmitFinalDeclarationRawData, SubmitFinalDeclarationRequest}

class SubmitFinalDeclarationParserSpec extends UnitSpec {

  val nino    = "AA123456B"
  val taxYear = "2017-18"
  val calcId  = "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c"

  implicit val correlationId = "a1e8057e-fbbc-47a8-a8b4-78d9f015c253"

  trait Test extends MockSubmitFinalDeclarationTriggerValidator {
    lazy val parser = new SubmitFinalDeclarationParser(mockValidator)
  }

  "parse" when {
    "valid input" should {
      "parse the request" in new Test {
        val data: SubmitFinalDeclarationRawData = SubmitFinalDeclarationRawData(nino, taxYear, calcId)
        MockValidator.validate(data).returns(Nil)

        parser.parseRequest(data) shouldBe Right(SubmitFinalDeclarationRequest(Nino(nino), TaxYear.fromMtd(taxYear), CalculationId(calcId)))
      }
    }

    "single validation error" should {
      "return the error" in new Test {
        val data: SubmitFinalDeclarationRawData = SubmitFinalDeclarationRawData(nino, taxYear, calcId)
        MockValidator.validate(data).returns(List(NinoFormatError))

        parser.parseRequest(data) shouldBe Left(ErrorWrapper(correlationId, NinoFormatError, None))
      }
    }

    "multiple validation errors" should {
      "return the errors" in new Test {
        val data: SubmitFinalDeclarationRawData = SubmitFinalDeclarationRawData(nino, taxYear, calcId)
        MockValidator.validate(data).returns(List(NinoFormatError, TaxYearFormatError))

        parser.parseRequest(data) shouldBe
          Left(ErrorWrapper(correlationId, BadRequestError, Some(Seq(NinoFormatError, TaxYearFormatError))))
      }
    }
  }

}
