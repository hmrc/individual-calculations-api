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
import support.UnitSpec
import uk.gov.hmrc.domain.Nino
import v1.mocks.validators.MockGetCalculationValidator
import v1.models.errors._
import v1.models.request.{GetCalculationRawData, GetCalculationRequest}

class GetCalculationParserSpec extends UnitSpec {

  val nino: String = "AA111111A"
  val calculationId: String = "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c"
  implicit val correlationId: String = "a1e8057e-fbbc-47a8-a8b4-78d9f015c253"

  trait Test extends MockGetCalculationValidator {
    lazy val parser = new GetCalculationParser(mockValidator)
  }

  "parse" when {
    "valid input" should {
      "parse the request" in new Test {
        val data: GetCalculationRawData = GetCalculationRawData(nino, calculationId)
        MockValidator.validate(data).returns(Nil)

        parser.parseRequest(data) shouldBe Right(GetCalculationRequest(Nino(nino), calculationId))
      }
    }
  }

  "single validation error" should {
    "return the error" in new Test {
      val data: GetCalculationRawData = GetCalculationRawData(nino, calculationId)
      MockValidator.validate(data).returns(List(NinoFormatError))

      parser.parseRequest(data) shouldBe Left(ErrorWrapper(correlationId, NinoFormatError, None, BAD_REQUEST))
    }
  }

  "multiple validation errors" should{
    "return the errors" in new Test{
      val data: GetCalculationRawData = GetCalculationRawData("AA111111F","f2fb30e5-4ab6-4a29-b3c1-c7264")
      MockValidator.validate(data).returns(List(NinoFormatError, CalculationIdFormatError))

      parser.parseRequest(data) shouldBe Left(ErrorWrapper(correlationId, BadRequestError, Some(Seq(NinoFormatError,CalculationIdFormatError)), BAD_REQUEST))
    }
  }
}