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

import mocks.MockAppConfig
import support.UnitSpec
import v3.mocks.validators.MockListCalculationsValidator
import v3.models.domain.{Nino, TaxYear}
import v3.models.errors.{ErrorWrapper, NinoFormatError}
import v3.models.request.{ListCalculationsRawData, ListCalculationsRequest}

class ListCalculationsParserSpec extends UnitSpec {

  trait Test extends MockListCalculationsValidator with MockAppConfig {
    implicit val correlationId: String = "a1e8057e-fbbc-47a8-a8b4-78d9f015c253"
    val parser: ListCalculationsParser = new ListCalculationsParser(mockListCalculationsValidator)
  }

  "ListCalculationsParser" when {
    "valid parameters are supplied" must {
      "return parsed request data" in new Test {
        val rawData: ListCalculationsRawData = ListCalculationsRawData("AA111111A", Some("2018-19"))
        MockListCalculationsValidator.validate(rawData).returns(Nil)

        parser.parseRequest(rawData) shouldBe
          Right(ListCalculationsRequest(Nino("AA111111A"), Some(TaxYear.fromMtd("2018-19"))))
      }
    }

    "invalid parameters are supplied" must {
      "return errors" in new Test {
        val rawData: ListCalculationsRawData = ListCalculationsRawData("AA111111", Some("2018-19"))
        MockListCalculationsValidator.validate(rawData).returns(List(NinoFormatError))

        parser.parseRequest(ListCalculationsRawData("AA111111", Some("2018-19"))) shouldBe
          Left(ErrorWrapper("a1e8057e-fbbc-47a8-a8b4-78d9f015c253", NinoFormatError, None))
      }
    }
  }

}
