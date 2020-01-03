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

package v1.controllers.requestParsers

import java.time.LocalDate

import play.api.http.Status._
import support.UnitSpec
import uk.gov.hmrc.domain.Nino
import v1.mocks.MockCurrentDateProvider
import v1.mocks.validators.MockListCalculationsValidator
import v1.models.errors._
import v1.models.request.{ListCalculationsRawData, ListCalculationsRequest}

class ListCalculationsParserSpec extends UnitSpec {
  val nino = "AA123456B"
  val taxYear = "2017-18"

  trait Test extends MockListCalculationsValidator with MockCurrentDateProvider{
    lazy val parser = new ListCalculationsParser(mockValidator, mockCurrentDateProvider)
  }

  "parse" when {
    "valid data is supplied" should {
      "return a valid request object" in new Test {
        MockCurrentDateProvider.getCurrentDate().returns(LocalDate.now())
        val data = ListCalculationsRawData(nino, Some(taxYear))
        MockValidator.validate(data).returns(Nil)

        parser.parseRequest(data) shouldBe Right(ListCalculationsRequest(Nino(nino), taxYear))
      }
    }

    "data with out tax year supplied is after 5th April of the year" should {
      "return a valid request object" in new Test {

        MockCurrentDateProvider.getCurrentDate().returns(LocalDate.parse("2017-04-06"))
        val data = ListCalculationsRawData(nino, None)
        MockValidator.validate(data).returns(Nil)

        parser.parseRequest(data) shouldBe Right(ListCalculationsRequest(Nino(nino), taxYear))
      }
    }

    "data with out tax year supplied is on or before 5th April of the year" should {
      "return a valid request object" in new Test {

        MockCurrentDateProvider.getCurrentDate().returns(LocalDate.parse("2017-04-05"))
        val data = ListCalculationsRawData(nino, None)
        MockValidator.validate(data).returns(Nil)

        parser.parseRequest(data) shouldBe Right(ListCalculationsRequest(Nino(nino), "2016-17"))
      }
    }

    "single validation error" should {
      "return the error" in new Test {
        val data = ListCalculationsRawData(nino, Some(taxYear))
        MockValidator.validate(data).returns(List(NinoFormatError))

        parser.parseRequest(data) shouldBe Left(ErrorWrapper(None, MtdErrors(BAD_REQUEST, NinoFormatError)))
      }
    }

    "multiple validation errors" should {
      "return the errors" in new Test {
        val data = ListCalculationsRawData(nino, Some(taxYear))
        MockValidator.validate(data).returns(List(NinoFormatError, TaxYearFormatError))

        parser.parseRequest(data) shouldBe Left(ErrorWrapper(None, MtdErrors(BAD_REQUEST, BadRequestError, Some(Seq(NinoFormatError, TaxYearFormatError)))))
      }
    }
  }

}
