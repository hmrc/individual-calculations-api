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
import api.models.errors
import api.models.errors.{ErrorWrapper, NinoFormatError}
import mocks.MockAppConfig
import support.UnitSpec
import v3.mocks.validators.MockListCalculationsValidator
import v3.models.request.{ListCalculationsRawData, ListCalculationsRequest}

class ListCalculationsParserSpec extends UnitSpec {

  private val nino: Nino = Nino("AA111111A")

  trait Test extends MockListCalculationsValidator with MockAppConfig {
    implicit val correlationId: String = "a1e8057e-fbbc-47a8-a8b4-78d9f015c253"
    val parser: ListCalculationsParser = new ListCalculationsParser(mockListCalculationsValidator)
  }

  "ListCalculationsParser" when {
    "given valid parameters including a taxYear" must {
      "return parsed request data" in new Test {
        val rawData: ListCalculationsRawData = ListCalculationsRawData("AA111111A", Some("2018-19"))
        MockListCalculationsValidator.validate(rawData).returns(Nil)

        val result: Either[ErrorWrapper, ListCalculationsRequest] = parser.parseRequest(rawData)
        result shouldBe Right(ListCalculationsRequest(nino, TaxYear.fromMtd("2018-19")))
      }
    }

    "given valid parameters without a taxYear" must {
      "return parsed request data" in new Test {
        val rawData: ListCalculationsRawData = ListCalculationsRawData("AA111111A", None)
        MockListCalculationsValidator.validate(rawData).returns(Nil)
        val expectedTaxYear = TaxYear.now()

        val result: Either[ErrorWrapper, ListCalculationsRequest] = parser.parseRequest(rawData)
        result shouldBe Right(ListCalculationsRequest(nino, expectedTaxYear))
      }
    }

    "given invalid parameters" must {
      "return errors" in new Test {
        val rawData: ListCalculationsRawData = ListCalculationsRawData("AA111111", Some("2018-19"))
        MockListCalculationsValidator.validate(rawData).returns(List(NinoFormatError))

        val result: Either[ErrorWrapper, ListCalculationsRequest] =
          parser.parseRequest(ListCalculationsRawData("AA111111", Some("2018-19")))
        result shouldBe Left(errors.ErrorWrapper("a1e8057e-fbbc-47a8-a8b4-78d9f015c253", NinoFormatError, None))
      }
    }
  }

}
