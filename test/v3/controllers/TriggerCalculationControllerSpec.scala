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

package v3.controllers

import api.controllers.{ControllerBaseSpec, ControllerTestRunner, UserRequest}
import api.mocks.MockIdGenerator
import api.mocks.hateoas.MockHateoasFactory
import api.mocks.services.{MockAuditService, MockEnrolmentsAuthService, MockMtdIdLookupService}
import api.models.audit.{AuditEvent, AuditResponse, GenericAuditDetail}
import api.models.auth.UserDetails
import api.models.domain.{Nino, TaxYear}
import api.models.errors.{ErrorWrapper, NinoFormatError, RuleTaxYearNotSupportedError}
import api.models.hateoas.HateoasWrapper
import api.models.outcomes.ResponseWrapper
import config.AppConfig
import mocks.MockAppConfig
import play.api.http.HeaderNames
import play.api.libs.json._
import play.api.mvc.{AnyContent, AnyContentAsEmpty, Result}
import play.api.test.FakeRequest
import routing.Version
import v3.mocks.requestParsers.MockTriggerCalculationParser
import v3.mocks.services.MockTriggerCalculationService
import v3.models.request.{TriggerCalculationRawData, TriggerCalculationRequest}
import v3.models.response.triggerCalculation.{TriggerCalculationHateoasData, TriggerCalculationResponse}

import java.time.LocalDateTime
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class TriggerCalculationControllerSpec
    extends ControllerBaseSpec
    with ControllerTestRunner
    with MockEnrolmentsAuthService
    with MockMtdIdLookupService
    with MockTriggerCalculationService
    with MockTriggerCalculationParser
    with MockHateoasFactory
    with MockAuditService
    with MockIdGenerator
    with MockAppConfig {

  private val taxYear       = TaxYear.fromMtd("2017-18")
  private val rawTaxYear    = taxYear.asMtd
  private val calculationId = "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c"

  private val response: TriggerCalculationResponse = TriggerCalculationResponse(calculationId)

  private val responseJsonNoHateoas: JsValue = Json
    .parse(
      s"""
         |{
         |  "calculationId" : "$calculationId"
         |}
    """.stripMargin
    )

  private val mtdResponseJson: JsValue = responseJsonNoHateoas.as[JsObject] ++ hateoaslinksJson

  val rawDataWithFinalDeclaration: TriggerCalculationRawData          = TriggerCalculationRawData(nino, rawTaxYear, finalDeclaration = Some("true"))
  val rawDataWithFinalDeclarationFalse: TriggerCalculationRawData     = TriggerCalculationRawData(nino, rawTaxYear, finalDeclaration = Some("false"))
  val rawDataWithFinalDeclarationUndefined: TriggerCalculationRawData = TriggerCalculationRawData(nino, rawTaxYear, None)

  val requestDataWithFinalDeclaration: TriggerCalculationRequest      = TriggerCalculationRequest(Nino(nino), taxYear, finalDeclaration = true)
  val requestDataWithFinalDeclarationFalse: TriggerCalculationRequest = TriggerCalculationRequest(Nino(nino), taxYear, finalDeclaration = false)

  private val userDetails = UserDetails("mtdId", "Individual", Some("agentReferenceNumber"))

  override lazy val fakeRequest: FakeRequest[AnyContentAsEmpty.type] =
    FakeRequest().withHeaders(HeaderNames.ACCEPT -> "application/vnd.hmrc.3.0+json")

  implicit val userRequest: UserRequest[AnyContent] = UserRequest[AnyContent](userDetails, fakeRequest)
  implicit val appConfig: AppConfig                 = mockAppConfig
  implicit val apiVersion: Version                  = Version(userRequest)

  trait Test extends ControllerTest with AuditEventChecking {

    val finalDeclaration: Option[String] = None

    val controller = new TriggerCalculationController(
      authService = mockEnrolmentsAuthService,
      lookupService = mockMtdIdLookupService,
      parser = mockTriggerCalculationParser,
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
          versionNumber = "3.0",
          auditResponse = auditResponse
        )
      )

    MockAppConfig
      .isApiDeprecated(apiVersion)
      .returns(false)
      .anyNumberOfTimes()

    MockAppConfig
      .deprecatedOn(apiVersion)
      .returns(Some(LocalDateTime.of(2023, 1, 17, 12, 0)))
      .anyNumberOfTimes()

    MockAppConfig
      .sunsetDate(apiVersion)
      .returns(Some(LocalDateTime.of(2023, 1, 17, 12, 0)))
      .anyNumberOfTimes()

    MockAppConfig
      .isSunsetEnabled(apiVersion)
      .returns(false)
      .anyNumberOfTimes()

    MockAppConfig
      .apiDocumentationUrl()
      .returns("")
      .anyNumberOfTimes()

  }

  "handleRequest" should {
    "return ACCEPTED with a calculationId" when {

      def happyPath(rawData: TriggerCalculationRawData, requestData: TriggerCalculationRequest): Unit = new Test {

        override val finalDeclaration: Option[String] = rawData.finalDeclaration

        MockTriggerCalculationParser
          .parseRequest(rawData)
          .returns(Right(requestData))

        MockTriggerCalculationService
          .triggerCalculation(requestData)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, response))))

        MockHateoasFactory
          .wrap(
            response,
            TriggerCalculationHateoasData(nino, TaxYear.fromMtd(rawTaxYear), finalDeclaration = requestData.finalDeclaration, calculationId))
          .returns(HateoasWrapper(response, hateoaslinks))

        runOkTestWithAudit(
          expectedStatus = ACCEPTED,
          maybeExpectedResponseBody = Some(mtdResponseJson),
          maybeAuditResponseBody = Some(responseJsonNoHateoas)
        )
      }

      "happy path with final declaration" in {
        happyPath(rawDataWithFinalDeclaration, requestDataWithFinalDeclaration)
      }

      "happy path with final declaration as false" in {
        happyPath(rawDataWithFinalDeclarationFalse, requestDataWithFinalDeclarationFalse)
      }

      "happy path with final declaration undefined" in {
        happyPath(rawDataWithFinalDeclarationUndefined, requestDataWithFinalDeclarationFalse)
      }
    }

    "return the error as per spec" when {
      "the parser validation fails" in new Test {
        MockTriggerCalculationParser
          .parseRequest(rawDataWithFinalDeclarationUndefined)
          .returns(Left(ErrorWrapper(correlationId, NinoFormatError, None)))

        runErrorTestWithAudit(NinoFormatError)
      }

      "the service returns an error" in new Test {
        MockTriggerCalculationParser
          .parseRequest(rawDataWithFinalDeclarationUndefined)
          .returns(Right(requestDataWithFinalDeclarationFalse))

        MockTriggerCalculationService
          .triggerCalculation(requestDataWithFinalDeclarationFalse)
          .returns(Future.successful(Left(ErrorWrapper(correlationId, RuleTaxYearNotSupportedError))))

        runErrorTestWithAudit(RuleTaxYearNotSupportedError)
      }
    }

  }

}
