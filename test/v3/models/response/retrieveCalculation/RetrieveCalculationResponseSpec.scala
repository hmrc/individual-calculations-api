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

package v3.models.response.retrieveCalculation

import mocks.MockAppConfig
import play.api.libs.json.Json
import support.UnitSpec
import v3.hateoas.HateoasFactory
import v3.models.domain.TaxYear
import v3.models.hateoas.{HateoasWrapper, Link}
import v3.models.hateoas.Method.{GET, POST}
import v3.models.response.common.CalculationType
import v3.models.response.retrieveCalculation.inputs.{IncomeSources, Inputs, PersonalInformation}
import v3.models.response.retrieveCalculation.messages.{Message, Messages}
import v3.models.response.retrieveCalculation.metadata.Metadata
import v3.models.utils.JsonErrorValidators

class RetrieveCalculationResponseSpec extends UnitSpec with CalculationFixture with JsonErrorValidators {

  "RetrieveCalculationResponse" must {
    "allow conversion from downstream JSON to MTD JSON" when {
      "JSON contains every field" in {
        val model = calculationDownstreamJson.as[RetrieveCalculationResponse]
        Json.toJson(model) shouldBe calculationMtdJson
      }
    }

    "have the correct fields optional" when {
      testJsonAllPropertiesOptionalExcept[RetrieveCalculationResponse](calculationDownstreamJson)("metadata", "inputs")
    }

    "return valid model without reliefs.basicRateExtension when removeBasicRateExtension is called" when {
      "calculation is None" in {
        minimalCalculationResponseWithoutBasicRateExtension.removeBasicRateExtension() shouldBe minimalCalculationResponseWithoutBasicRateExtension
      }

      "basicRateExtension is provided" in {
        minimalCalculationResponseWithBasicRateExtension.removeBasicRateExtension() shouldBe minimalCalculationResponseWithoutBasicRateExtension
      }
    }

  }

  "LinksFactory" should {

    trait Test extends MockAppConfig {
      val hateoasFactory = new HateoasFactory(mockAppConfig)
      val nino           = "AA999999A"
      val taxYear        = TaxYear.fromMtd("2021-22")
      val calculationId  = "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c"
      MockAppConfig.apiGatewayContext.returns("some-context").anyNumberOfTimes()
    }

    def retrieveCalculationResponse(intentToSubmitFinalDeclaration: Boolean,
                                    finalDeclaration: Boolean,
                                    messages: Option[Messages]): RetrieveCalculationResponse =
      RetrieveCalculationResponse(
        metadata = Metadata(
          "",
          taxYear = TaxYear.fromDownstream("2021"),
          "",
          None,
          "",
          None,
          CalculationType.`inYear`,
          intentToSubmitFinalDeclaration = intentToSubmitFinalDeclaration,
          finalDeclaration = finalDeclaration,
          None,
          "",
          ""
        ),
        inputs = Inputs(
          PersonalInformation("", None, "", None, None, None, None, None),
          IncomeSources(None, None),
          None,
          None,
          None,
          None,
          None,
          None,
          None),
        calculation = None,
        messages = messages
      )

    "return both retrieve and submit links" when {
      "intentToSubmitFinalDeclaration is true, finalDeclaration is false, messages is not present" in new Test {
        private val model = retrieveCalculationResponse(
          intentToSubmitFinalDeclaration = true,
          finalDeclaration = false,
          messages = None
        )

        hateoasFactory.wrap(
          model,
          RetrieveCalculationHateoasData(
            nino = nino,
            taxYear = taxYear,
            calculationId = calculationId,
            response = model
          )) shouldBe HateoasWrapper(
          model,
          Seq(
            Link(s"/some-context/$nino/self-assessment/2021-22", POST, "trigger"),
            Link(s"/some-context/$nino/self-assessment/2021-22/$calculationId", GET, "self"),
            Link(s"/some-context/$nino/self-assessment/2021-22/$calculationId/final-declaration", POST, "submit-final-declaration")
          )
        )
      }
      "intentToSubmitFinalDeclaration is true, finalDeclaration is false, errors array is not present" in new Test {
        private val model = retrieveCalculationResponse(
          intentToSubmitFinalDeclaration = true,
          finalDeclaration = false,
          messages = Some(Messages(None, None, None))
        )

        hateoasFactory.wrap(
          model,
          RetrieveCalculationHateoasData(
            nino = nino,
            taxYear = taxYear,
            calculationId = calculationId,
            response = model
          )) shouldBe HateoasWrapper(
          model,
          Seq(
            Link(s"/some-context/$nino/self-assessment/2021-22", POST, "trigger"),
            Link(s"/some-context/$nino/self-assessment/2021-22/$calculationId", GET, "self"),
            Link(s"/some-context/$nino/self-assessment/2021-22/$calculationId/final-declaration", POST, "submit-final-declaration")
          )
        )
      }
      "intentToSubmitFinalDeclaration is true, finalDeclaration is false, errors array is present but empty" in new Test {
        private val model = retrieveCalculationResponse(
          intentToSubmitFinalDeclaration = true,
          finalDeclaration = false,
          messages = Some(Messages(None, None, Some(Seq())))
        )

        hateoasFactory.wrap(
          model,
          RetrieveCalculationHateoasData(
            nino = nino,
            taxYear = taxYear,
            calculationId = calculationId,
            response = model
          )) shouldBe HateoasWrapper(
          model,
          Seq(
            Link(s"/some-context/$nino/self-assessment/2021-22", POST, "trigger"),
            Link(s"/some-context/$nino/self-assessment/2021-22/$calculationId", GET, "self"),
            Link(s"/some-context/$nino/self-assessment/2021-22/$calculationId/final-declaration", POST, "submit-final-declaration")
          )
        )
      }
    }

    "return only a retrieve link" when {
      "intentToSubmitFinalDeclaration is false" in new Test {
        private val model = retrieveCalculationResponse(
          intentToSubmitFinalDeclaration = false,
          finalDeclaration = false,
          messages = None
        )

        hateoasFactory.wrap(
          model,
          RetrieveCalculationHateoasData(
            nino = nino,
            taxYear = taxYear,
            calculationId = calculationId,
            response = model
          )) shouldBe HateoasWrapper(
          model,
          Seq(
            Link(s"/some-context/$nino/self-assessment/2021-22", POST, "trigger"),
            Link(s"/some-context/$nino/self-assessment/2021-22/$calculationId", GET, "self")
          )
        )
      }
      "finalDeclaration is true" in new Test {
        private val model = retrieveCalculationResponse(
          intentToSubmitFinalDeclaration = true,
          finalDeclaration = true,
          messages = None
        )

        hateoasFactory.wrap(
          model,
          RetrieveCalculationHateoasData(
            nino = nino,
            taxYear = taxYear,
            calculationId = calculationId,
            response = model
          )) shouldBe HateoasWrapper(
          model,
          Seq(
            Link(s"/some-context/$nino/self-assessment/2021-22", POST, "trigger"),
            Link(s"/some-context/$nino/self-assessment/2021-22/$calculationId", GET, "self")
          )
        )
      }
      "errors array is present and contains errors" in new Test {
        private val model = retrieveCalculationResponse(
          intentToSubmitFinalDeclaration = true,
          finalDeclaration = false,
          messages = Some(Messages(None, None, Some(Seq(Message("", "")))))
        )

        hateoasFactory.wrap(
          model,
          RetrieveCalculationHateoasData(
            nino = nino,
            taxYear = taxYear,
            calculationId = calculationId,
            response = model
          )) shouldBe HateoasWrapper(
          model,
          Seq(
            Link(s"/some-context/$nino/self-assessment/2021-22", POST, "trigger"),
            Link(s"/some-context/$nino/self-assessment/2021-22/$calculationId", GET, "self")
          )
        )
      }
    }
  }

}
