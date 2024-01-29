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

package v4.controllers.requestParsers.validators

import api.models.errors._
import support.UnitSpec
import v4.models.request.ListCalculationsRawData

class ListCalculationsValidatorSpec extends UnitSpec {
  val validator: ListCalculationsValidator = new ListCalculationsValidator

  "ListCalculationsValidator" when {
    "valid parameters are supplied" must {
      "return an empty list" in {
        validator.validate(ListCalculationsRawData("AA111111A", Some("2018-19"))) shouldBe Nil
      }
    }

    "invalid parameters are supplied" should {
      val params: Seq[(String, Option[String], Seq[MtdError])] = Seq(
        ("AA111111", None, Seq(NinoFormatError)),
        ("AA111111A", Some("2018"), Seq(TaxYearFormatError)),
        ("AA111111A", Some("2018-20"), Seq(RuleTaxYearRangeInvalidError)),
        ("AA111111", Some("2018-20"), Seq(NinoFormatError, RuleTaxYearRangeInvalidError)),
        ("AA111111A", Some("2012-13"), Seq(RuleTaxYearNotSupportedError))
      )

      params.foreach(args => (doValidationErrorCheck _).tupled(args))
    }

    def doValidationErrorCheck(nino: String, taxYear: Option[String], errorResult: Seq[MtdError]): Unit = {
      s"return the expected errors: $errorResult" when {
        s"when nino: $nino, and taxYear: $taxYear parameters are supplied" in {
          validator.validate(ListCalculationsRawData(nino, taxYear)) shouldBe errorResult
        }
      }
    }
  }

}
