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

package v5.triggerCalculation

import api.controllers.{ControllerBaseSpec, ControllerTestRunner}
import api.hateoas.{HateoasWrapper, MockHateoasFactory}
import api.mocks.MockIdGenerator
import api.models.audit.{AuditEvent, AuditResponse, GenericAuditDetail}
import api.models.domain.{Nino, TaxYear}
import api.models.errors.{ErrorWrapper, NinoFormatError, RuleTaxYearNotSupportedError}
import api.models.outcomes.ResponseWrapper
import api.services.{MockAuditService, MockEnrolmentsAuthService, MockMtdIdLookupService}
import play.api.libs.json._
import play.api.mvc.Result
import v5.triggerCalculation.model.request.{Def1_TriggerCalculationRequestData, TriggerCalculationRequestData}
import v5.triggerCalculation.model.response.{Def1_TriggerCalculationResponse, TriggerCalculationHateoasData, TriggerCalculationResponse}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class TriggerCalculationControllerSpec
    extends ControllerBaseSpec
    with ControllerTestRunner
    with MockEnrolmentsAuthService
    with MockMtdIdLookupService
    with MockTriggerCalculationService
    with MockTriggerCalculationValidatorFactory
    with MockHateoasFactory
    with MockAuditService
    with MockIdGenerator {

  private val taxYear    = TaxYear.fromMtd("2017-18")
  private val rawTaxYear = taxYear.asMtd

  val requestDataWithFinalDeclaration: TriggerCalculationRequestData =
    Def1_TriggerCalculationRequestData(Nino(nino), taxYear, finalDeclaration = true)

  val requestDataWithFinalDeclarationFalse: TriggerCalculationRequestData =
    Def1_TriggerCalculationRequestData(Nino(nino), taxYear, finalDeclaration = false)

  private val calculationId                        = "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c"
  private val response: TriggerCalculationResponse = Def1_TriggerCalculationResponse(calculationId)

  private val responseJsonNoHateoas: JsValue = Json
    .parse(
      s"""
         |{
         |  "calculationId" : "$calculationId"
         |}
    """.stripMargin
    )

  private val mtdResponseJson: JsValue = responseJsonNoHateoas.as[JsObject] ++ hateoaslinksJson

  "handleRequest" should {
    "return ACCEPTED with a calculationId" when {

      "happy path with final declaration" in new Test {
        willUseValidator(returningSuccess(requestDataWithFinalDeclaration))

        MockTriggerCalculationService
          .triggerCalculation(requestDataWithFinalDeclaration)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, response))))

        MockHateoasFactory
          .wrap(
            response,
            TriggerCalculationHateoasData(
              nino,
              TaxYear.fromMtd(rawTaxYear),
              finalDeclaration = requestDataWithFinalDeclaration.finalDeclaration,
              calculationId)
          )
          .returns(HateoasWrapper(response, hateoaslinks))

        runOkTestWithAudit(
          expectedStatus = ACCEPTED,
          maybeExpectedResponseBody = Some(mtdResponseJson),
          maybeAuditResponseBody = Some(responseJsonNoHateoas)
        )
      }

      "happy path with final declaration undefined" in new Test {
        willUseValidator(returningSuccess(requestDataWithFinalDeclarationFalse))

        MockTriggerCalculationService
          .triggerCalculation(requestDataWithFinalDeclarationFalse)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, response))))

        MockHateoasFactory
          .wrap(
            response,
            TriggerCalculationHateoasData(
              nino,
              TaxYear.fromMtd(rawTaxYear),
              finalDeclaration = requestDataWithFinalDeclarationFalse.finalDeclaration,
              calculationId)
          )
          .returns(HateoasWrapper(response, hateoaslinks))

        runOkTestWithAudit(
          expectedStatus = ACCEPTED,
          maybeExpectedResponseBody = Some(mtdResponseJson),
          maybeAuditResponseBody = Some(responseJsonNoHateoas)
        )
      }
    }

    "return the error as per spec" when {
      "the parser validation fails" in new Test {
        willUseValidator(returning(NinoFormatError))

        runErrorTestWithAudit(NinoFormatError)
      }

      "the service returns an error" in new Test {
        willUseValidator(returningSuccess(requestDataWithFinalDeclarationFalse))

        MockTriggerCalculationService
          .triggerCalculation(requestDataWithFinalDeclarationFalse)
          .returns(Future.successful(Left(ErrorWrapper(correlationId, RuleTaxYearNotSupportedError))))

        runErrorTestWithAudit(RuleTaxYearNotSupportedError)
      }
    }

  }

  private trait Test extends ControllerTest with AuditEventChecking {

    val finalDeclaration: Option[String] = None

    private lazy val controller = new TriggerCalculationController(
      authService = mockEnrolmentsAuthService,
      lookupService = mockMtdIdLookupService,
      validatorFactory = mockTriggerCalculationValidatorFactory,
      service = mockService,
      cc = cc,
      auditService = mockAuditService,
      hateoasFactory = mockHateoasFactory,
      idGenerator = mockIdGenerator
    )

    protected def callController(): Future[Result] = controller.triggerCalculation(nino, rawTaxYear, finalDeclaration)(fakeRequest)

    override protected def event(auditResponse: AuditResponse, maybeRequestBody: Option[JsValue]): AuditEvent[GenericAuditDetail] =
      AuditEvent(
        "TriggerASelfAssessmentTaxCalculation",
        "trigger-a-self-assessment-tax-calculation",
        GenericAuditDetail(
          userType = "Individual",
          agentReferenceNumber = None,
          params = Map("nino" -> nino, "taxYear" -> rawTaxYear, "finalDeclaration" -> s"${finalDeclaration.getOrElse(false)}"),
          requestBody = None,
          `X-CorrelationId` = correlationId,
          versionNumber = apiVersion.name,
          auditResponse = auditResponse
        )
      )

  }

}
