/*
 * Copyright 2019 HM Revenue & Customs
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

import play.api.libs.json.Json
import support.UnitSpec
import uk.gov.hmrc.domain.Nino
import v1.mocks.validators.MockSampleValidator
import v1.models.domain.SampleRequestBody
import v1.models.errors._
import v1.models.requestData.{TaxYear, SampleRawData, SampleRequestData}

class SampleRequestDataParserSpec extends UnitSpec {
  val nino = "AA123456B"
  val taxYear = "2017-18"
  val calcId = "someCalcId"

  private val requestBodyJson = Json.parse(
    """{
      |  "data" : "someData"
      |}
    """.stripMargin)

  val inputData =
    SampleRawData(nino, taxYear, requestBodyJson)

  trait Test extends MockSampleValidator {
    lazy val parser = new SampleRequestDataParser(mockValidator)
  }

  "parse" should {

    "return a request object" when {
      "valid request data is supplied" in new Test {
        MockSampleValidator.validate(inputData).returns(Nil)

        parser.parseRequest(inputData) shouldBe
          Right(SampleRequestData(Nino(nino), "2018", SampleRequestBody("someData")))
      }
    }

    "return an ErrorWrapper" when {

      "a single validation error occurs" in new Test {
        MockSampleValidator.validate(inputData)
          .returns(List(NinoFormatError))

        parser.parseRequest(inputData) shouldBe
          Left(ErrorWrapper(None, NinoFormatError, None))
      }

      "multiple validation errors occur" in new Test {
        MockSampleValidator.validate(inputData)
          .returns(List(NinoFormatError, TaxYearFormatError))

        parser.parseRequest(inputData) shouldBe
          Left(ErrorWrapper(None, BadRequestError, Some(Seq(NinoFormatError, TaxYearFormatError))))
      }
    }
  }
}
