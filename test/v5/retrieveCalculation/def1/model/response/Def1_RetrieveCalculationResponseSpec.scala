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

package v5.retrieveCalculation.def1.model.response

import api.hateoas
import api.hateoas.Method.{GET, POST}
import api.hateoas.{HateoasFactory, HateoasWrapper}
import api.models.domain.TaxYear
import api.models.utils.JsonErrorValidators
import mocks.MockAppConfig
import play.api.libs.json.Json
import support.UnitSpec
import v5.retrieveCalculation.def1.model.Def1_CalculationFixture
import v5.retrieveCalculation.def1.model.response.common.CalculationType
import v5.retrieveCalculation.def1.model.response.inputs.{IncomeSources, Inputs, PersonalInformation}
import v5.retrieveCalculation.def1.model.response.messages.{Message, Messages}
import v5.retrieveCalculation.def1.model.response.metadata.Metadata
import v5.retrieveCalculation.models.response.RetrieveCalculationResponse._
import v5.retrieveCalculation.models.response.{Def1_RetrieveCalculationResponse, RetrieveCalculationHateoasData, RetrieveCalculationResponse}

class Def1_RetrieveCalculationResponseSpec extends UnitSpec with Def1_CalculationFixture with JsonErrorValidators {

  "RetrieveCalculationResponse" must {
    "allow conversion from downstream JSON to MTD JSON" when {
      "JSON contains every field" in {
        val model = calculationDownstreamJson.as[Def1_RetrieveCalculationResponse]
        Json.toJson(model) shouldBe calculationMtdJson
      }
    }

    "have the correct fields optional" when {
      testJsonAllPropertiesOptionalExcept[Def1_RetrieveCalculationResponse](calculationDownstreamJson)("metadata", "inputs")
    }

    "return the correct TaxDeductedAtSource" in {
      taxDeductedAtSource.withoutTaxTakenOffTradingIncome shouldBe taxDeductedAtSource.copy(taxTakenOffTradingIncome = None)
    }

  }

  "LinksFactory" should {

    trait Test extends MockAppConfig {
      val hateoasFactory   = new HateoasFactory(mockAppConfig)
      val nino             = "AA999999A"
      val taxYear: TaxYear = TaxYear.fromMtd("2021-22")
      val calculationId    = "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c"
      MockAppConfig.apiGatewayContext.returns("some-context").anyNumberOfTimes()
    }

    def retrieveCalculationResponse(intentToSubmitFinalDeclaration: Boolean,
                                    finalDeclaration: Boolean,
                                    messages: Option[Messages]): RetrieveCalculationResponse =
      Def1_RetrieveCalculationResponse(
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
          PersonalInformation("", None, "", None, None, None, None, None, Some("status")),
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
            hateoas.Link(s"/some-context/$nino/self-assessment/2021-22", POST, "trigger"),
            hateoas.Link(s"/some-context/$nino/self-assessment/2021-22/$calculationId", GET, "self"),
            hateoas.Link(s"/some-context/$nino/self-assessment/2021-22/$calculationId/final-declaration", POST, "submit-final-declaration")
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
            hateoas.Link(s"/some-context/$nino/self-assessment/2021-22", POST, "trigger"),
            hateoas.Link(s"/some-context/$nino/self-assessment/2021-22/$calculationId", GET, "self"),
            hateoas.Link(s"/some-context/$nino/self-assessment/2021-22/$calculationId/final-declaration", POST, "submit-final-declaration")
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
            hateoas.Link(s"/some-context/$nino/self-assessment/2021-22", POST, "trigger"),
            hateoas.Link(s"/some-context/$nino/self-assessment/2021-22/$calculationId", GET, "self"),
            hateoas.Link(s"/some-context/$nino/self-assessment/2021-22/$calculationId/final-declaration", POST, "submit-final-declaration")
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
            hateoas.Link(s"/some-context/$nino/self-assessment/2021-22", POST, "trigger"),
            hateoas.Link(s"/some-context/$nino/self-assessment/2021-22/$calculationId", GET, "self")
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
            hateoas.Link(s"/some-context/$nino/self-assessment/2021-22", POST, "trigger"),
            hateoas.Link(s"/some-context/$nino/self-assessment/2021-22/$calculationId", GET, "self")
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
            hateoas.Link(s"/some-context/$nino/self-assessment/2021-22", POST, "trigger"),
            hateoas.Link(s"/some-context/$nino/self-assessment/2021-22/$calculationId", GET, "self")
          )
        )
      }
    }
  }

}
