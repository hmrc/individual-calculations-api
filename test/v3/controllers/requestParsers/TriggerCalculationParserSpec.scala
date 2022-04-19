/*
 * Copyright 2022 HM Revenue & Customs
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

import support.UnitSpec
import v3.mocks.validators.MockTriggerCalculationValidator
import v3.models.domain.{Nino, TaxYear}
import v3.models.errors._
import v3.models.request.{TriggerCalculationRawData, TriggerCalculationRequest}

class TriggerCalculationParserSpec extends UnitSpec {

  val nino             = "AA123456B"
  val taxYear          = "2017-18"
  val finalDeclaration = true

  implicit val correlationId: String = "a1e8057e-fbbc-47a8-a8b4-78d9f015c253"

  trait Test extends MockTriggerCalculationValidator {
    lazy val parser = new TriggerCalculationParser(mockValidator)
  }

  "parse" when {
    "valid input" should {
      "parse the request" in new Test {
        val data: TriggerCalculationRawData = TriggerCalculationRawData(nino, taxYear, Some(finalDeclaration))
        MockValidator.validate(data).returns(Nil)

        parser.parseRequest(data) shouldBe Right(TriggerCalculationRequest(Nino(nino), TaxYear.fromMtd(taxYear), finalDeclaration))
      }
    }

    "single validation error" should {
      "return the error" in new Test {
        val data: TriggerCalculationRawData = TriggerCalculationRawData(nino, taxYear, Some(finalDeclaration))
        MockValidator.validate(data).returns(List(NinoFormatError))

        parser.parseRequest(data) shouldBe Left(ErrorWrapper(correlationId, NinoFormatError, None))
      }
    }

    "multiple validation errors" should {
      "return the errors" in new Test {
        val data: TriggerCalculationRawData = TriggerCalculationRawData(nino, taxYear, Some(finalDeclaration))
        MockValidator.validate(data).returns(List(NinoFormatError, TaxYearFormatError))

        parser.parseRequest(data) shouldBe
          Left(ErrorWrapper(correlationId, BadRequestError, Some(Seq(NinoFormatError, TaxYearFormatError))))
      }
    }
  }

}
