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

package v1.controllers.requestParsers.validators.validations

import play.api.libs.json.{Json, OFormat}
import support.UnitSpec
import v1.models.errors.RuleIncorrectOrEmptyBodyError
import v1.models.utils.JsonErrorValidators

class JsonFormatValidationSpec extends UnitSpec with JsonErrorValidators {

  case class TestDataObject(fieldOne: String, fieldTwo: String)
  case class TestDataWrapper(arrayField: Option[Seq[TestDataObject]])

  implicit val testDataObjectFormat: OFormat[TestDataObject] = Json.format[TestDataObject]
  implicit val testDataWrapperFormat: OFormat[TestDataWrapper] = Json.format[TestDataWrapper]

  "validate" should {
    "return no errors" when {
      "when a valid JSON object with all the necessary fields is supplied" in {

        val validJson = Json.parse("""{ "fieldOne" : "Something", "fieldTwo" : "SomethingElse" }""")

        val validationResult = JsonFormatValidation.validate[TestDataObject](validJson)
        validationResult shouldBe empty
      }
    }

    "return an error " when {
      "required field is missing" in {

        // fieldTwo is missing
        val json = Json.parse("""{ "fieldOne" : "Something" }""")

        val validationResult = JsonFormatValidation.validate[TestDataObject](json)
        validationResult shouldBe List(RuleIncorrectOrEmptyBodyError.copy(paths = Some(Seq("/fieldTwo"))))
      }

      "required field is missing in array object" in {

        // both fields are missing
        val json = Json.parse("""{ "arrayField" : [{}]}""")

        val validationResult = JsonFormatValidation.validate[TestDataWrapper](json)
        validationResult shouldBe List(RuleIncorrectOrEmptyBodyError.copy(paths = Some(Seq("/arrayField/0/fieldTwo", "/arrayField/0/fieldOne"))))
      }

      "required field is missing in multiple array objects" in {

        // both fields are missing
        val json = Json.parse("""{ "arrayField" : [{}, {}]}""")

        val validationResult = JsonFormatValidation.validate[TestDataWrapper](json)
        validationResult shouldBe List(RuleIncorrectOrEmptyBodyError.copy(paths =
          Some(Seq(
            "/arrayField/0/fieldTwo",
            "/arrayField/0/fieldOne",
            "/arrayField/1/fieldTwo",
            "/arrayField/1/fieldOne"
          ))
        ))
      }

      "empty body is submitted" in {

        val json = Json.parse("""{}""")

        val validationResult = JsonFormatValidation.validate[TestDataObject](json)
        validationResult shouldBe List(RuleIncorrectOrEmptyBodyError)
      }

      "a non-empty body is submitted with no valid data" in {

        val json = Json.parse("""{"aField": "aValue"}""")

        val validationResult = JsonFormatValidation.validate[TestDataWrapper](json)
        validationResult shouldBe List(RuleIncorrectOrEmptyBodyError)
      }

      "a non-empty body is supplied without any expected fields" in {

        val json = Json.parse("""{"field": "value"}""")

        val validationResult = JsonFormatValidation.validate[TestDataObject](json)
        validationResult shouldBe List(RuleIncorrectOrEmptyBodyError.copy(paths = Some(Seq("/fieldTwo", "/fieldOne"))))
      }

      "a field is supplied with the wrong data type" in {

        val json = Json.parse("""{"fieldOne": true, "fieldTwo": "value"}""")

        val validationResult = JsonFormatValidation.validate[TestDataObject](json)
        validationResult shouldBe List(RuleIncorrectOrEmptyBodyError.copy(paths = Some(Seq("/fieldOne"))))
      }
    }
  }
}
