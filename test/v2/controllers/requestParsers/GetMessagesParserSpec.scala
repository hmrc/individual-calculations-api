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

package v2.controllers.requestParsers

import play.api.http.Status._
import support.UnitSpec
import uk.gov.hmrc.domain.Nino
import v2.mocks.validators.MockGetMessagesValidator
import v2.models.domain.MessageType
import v2.models.errors._
import v2.models.request.{GetMessagesRawData, GetMessagesRequest}

class GetMessagesParserSpec extends UnitSpec {

  val nino: String = "AA111111A"
  val calculationId: String = "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c"
  implicit val correlationId: String = "a1e8057e-fbbc-47a8-a8b4-78d9f015c253"

  trait Test extends MockGetMessagesValidator {
    lazy val parser = new GetMessagesParser(mockValidator)
  }

  "parse" when {
    "valid input" should {
      "parse the request" in new Test {
        val data: GetMessagesRawData = GetMessagesRawData(nino, calculationId, Seq("errors"))
        MockValidator.validate(data).returns(Nil)

        parser.parseRequest(data) shouldBe Right(GetMessagesRequest(Nino(nino), calculationId, Seq(MessageType.toTypeClass("errors"))))
      }
    }
  }

  "single validation error" should {
    "return the error" in new Test {
      val data: GetMessagesRawData = GetMessagesRawData(nino, calculationId, Seq("errors"))
      MockValidator.validate(data).returns(List(NinoFormatError))

      parser.parseRequest(data) shouldBe Left(ErrorWrapper(correlationId, NinoFormatError, None, BAD_REQUEST))
    }
  }

  "multiple validation errors" should {
    "return the errors" in new Test {
      val data: GetMessagesRawData = GetMessagesRawData("AA111111F", "f2fb30e5-4ab6-4a29-b3c1-c7264", Seq("shmerrors", "shmimfo"))
      MockValidator.validate(data).returns(List(NinoFormatError, CalculationIdFormatError, TypeFormatError))

      parser.parseRequest(data) shouldBe Left(
        ErrorWrapper(correlationId, BadRequestError, Some(Seq(NinoFormatError, CalculationIdFormatError, TypeFormatError)), BAD_REQUEST))
    }
  }
}