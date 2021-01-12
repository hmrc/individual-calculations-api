/*
 * Copyright 2021 HM Revenue & Customs
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

package v1.controllers.requestParsers

import play.api.http.Status._
import play.api.libs.json.Json
import play.api.mvc.AnyContentAsJson
import support.UnitSpec
import uk.gov.hmrc.domain.Nino
import v1.mocks.validators.MockCrystallisationValidator
import v1.models.domain.DesTaxYear
import v1.models.errors._
import v1.models.request.crystallisation.{CrystallisationRawData, CrystallisationRequest}

class CrystallisationRequestParserSpec extends UnitSpec {
  private val nino = "AA123456B"
  private val taxYear = "2017-18"
  private val calculationId = "a1e8057e-fbbc-47a8-a8b4-78d9f015c253"
  implicit val correlationId = "a1e8057e-fbbc-47a8-a8b4-78d9f015c253"

  trait Test extends MockCrystallisationValidator {
    lazy val parser = new CrystallisationRequestParser(mockCrystallisationValidator)
  }

  "parse" when {
    "valid input" should {
      "parse the request" in new Test {
        val data = CrystallisationRawData(nino, taxYear, AnyContentAsJson(Json.obj("calculationId" -> calculationId)))
        MockValidator.validate(data).returns(Nil)

        parser.parseRequest(data) shouldBe Right(CrystallisationRequest(Nino(nino), DesTaxYear.fromMtd(taxYear), calculationId))
      }
    }

    "single validation error" should {
      "return the error" in new Test {
        val data = CrystallisationRawData("invalidNino", taxYear, AnyContentAsJson(Json.obj("calculationId" -> calculationId)))
        MockValidator.validate(data).returns(List(NinoFormatError))

        parser.parseRequest(data) shouldBe Left(ErrorWrapper(correlationId, NinoFormatError, None, BAD_REQUEST))
      }
    }

    "multiple validation errors" should {
      "return the errors" in new Test {
        val data = CrystallisationRawData("invalidNino", "2018-20", AnyContentAsJson(Json.obj("calculationId" -> calculationId)))
        MockValidator.validate(data).returns(List(NinoFormatError, RuleTaxYearRangeInvalidError))

        parser.parseRequest(data) shouldBe
          Left(ErrorWrapper(correlationId, BadRequestError, Some(Seq(NinoFormatError, RuleTaxYearRangeInvalidError)), BAD_REQUEST))
      }
    }
  }
}
