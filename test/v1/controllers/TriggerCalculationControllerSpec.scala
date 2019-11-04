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

package v1.controllers

import play.api.libs.json.{Format, JsValue, Json}
import play.api.mvc.{AnyContentAsJson, Result}
import uk.gov.hmrc.domain.Nino
import uk.gov.hmrc.http.HeaderCarrier
import utils.Logging
import v1.handler.{RequestDefn, RequestHandler}
import v1.mocks.hateoas.MockHateoasFactory
import v1.mocks.requestParsers.MockTriggerCalculationParser
import v1.mocks.services.{MockAuditService, MockEnrolmentsAuthService, MockMtdIdLookupService, MockStandardService}
import v1.models.audit.{AuditError, AuditEvent, AuditResponse, GenericAuditDetail}
import v1.models.domain.TriggerCalculation
import v1.models.errors._
import v1.models.hateoas.HateoasWrapper
import v1.models.outcomes.ResponseWrapper
import v1.models.request.{TriggerCalculationRawData, TriggerCalculationRequest}
import v1.models.response.triggerCalculation.{TriggerCalculationHateaosData, TriggerCalculationResponse}
import v1.support.BackendResponseMappingSupport
import v1.models.hateoas.Link
import v1.models.hateoas.Method.GET

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class TriggerCalculationControllerSpec extends ControllerBaseSpec
  with MockEnrolmentsAuthService
  with MockMtdIdLookupService
  with MockTriggerCalculationParser
  with MockHateoasFactory
  with MockStandardService
  with MockAuditService{

  trait Test {
    val hc = HeaderCarrier()

    val controller = new TriggerCalculationController(
      authService = mockEnrolmentsAuthService,
      lookupService = mockMtdIdLookupService,
      triggerCalculationParser = mockTriggerCalculationParser,
      service = mockStandardService,
      cc = cc,
      auditService = mockAuditService,
      hateoasFactory = mockHateoasFactory
    )

    MockedMtdIdLookupService.lookup(nino).returns(Future.successful(Right("test-mtd-id")))
    MockedEnrolmentsAuthService.authoriseUser()
  }

  private val nino          = "AA123456A"
  private val taxYear       = "2017-18"
  private val correlationId = "X-123"

  private case class TaxYearWrapper(taxYear: String)
   private object TaxYearWrapper {
    implicit val formats: Format[TaxYearWrapper] = Json.format[TaxYearWrapper]
  }

  val response = TriggerCalculationResponse("f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c")

  val json: JsValue = Json.parse("""{
      |  "id" : "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c",
      |  "links" : [
      |      {
      |      "href":"/foo/bar",
      |      "method":"GET",
      |      "rel":"test-relationship"
      |      }
      |   ]
      |}""".stripMargin)

  val triggerCalculation = TriggerCalculation(taxYear)

  val rawData = TriggerCalculationRawData(nino, AnyContentAsJson(Json.toJson(triggerCalculation)))
  val requestData = TriggerCalculationRequest(Nino(nino), taxYear)
  val error = ErrorWrapper(Some(correlationId), MtdErrors(FORBIDDEN, RuleNoIncomeSubmissionsExistError))

  val testHateoasLink = Link(href = "/foo/bar", method = GET, rel = "test-relationship")

  private def uri = "/"

  "handleRequest" should {
    "return ACCEPTED with list of calculations" when {
      "happy path" in new Test {
        MockTriggerCalculationParser
          .parse(rawData)
          .returns(Right(requestData))

        MockStandardService
          .doService(RequestDefn.Post(uri, Json.toJson(triggerCalculation)), ACCEPTED)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, response))))

        MockHateoasFactory
          .wrap(response, TriggerCalculationHateaosData(nino, "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c"))
          .returns(HateoasWrapper(response, Seq(testHateoasLink)))

        val result: Future[Result] = controller.triggerCalculation(nino)(fakePostRequest(Json.toJson(triggerCalculation)))

        status(result) shouldBe ACCEPTED
        contentAsJson(result) shouldBe json
        header("X-CorrelationId", result) shouldBe Some(correlationId)


        val detail = GenericAuditDetail(
          "Individual", None, Map("nino" -> nino), Some(Json.toJson(TaxYearWrapper("2017-18"))), correlationId,
          AuditResponse(ACCEPTED, None, Some(json)))
        val event = AuditEvent("triggerASelfAssessmentTaxCalculation", "trigger-a-self-assessment-tax-calculation", detail)
        MockedAuditService.verifyAuditEvent(event).once
      }
    }

    "return FORBIDDEN with the correct error message" when {
      "no income submissions exist" in new Test {
        MockTriggerCalculationParser
          .parse(rawData)
          .returns(Right(requestData))

        MockStandardService
          .doService(RequestDefn.Post(uri, Json.toJson(triggerCalculation)), ACCEPTED)
          .returns(Future.successful(Left(error)))

        val result: Future[Result] = controller.triggerCalculation(nino)(fakePostRequest(Json.toJson(triggerCalculation)))

        status(result) shouldBe FORBIDDEN
        contentAsJson(result) shouldBe Json.toJson(RuleNoIncomeSubmissionsExistError)
        header("X-CorrelationId", result) shouldBe Some(correlationId)

        val detail = GenericAuditDetail(
          "Individual", None, Map("nino" -> nino), Some(Json.toJson(TaxYearWrapper("2017-18"))), correlationId,
          AuditResponse(FORBIDDEN, Some(List(AuditError(RuleNoIncomeSubmissionsExistError.code))), None))
        val event = AuditEvent("triggerASelfAssessmentTaxCalculation", "trigger-a-self-assessment-tax-calculation", detail)
        MockedAuditService.verifyAuditEvent(event).once
      }
    }

    "map service error mapping according to spec" in new Test with BackendResponseMappingSupport with Logging {
      MockTriggerCalculationParser
        .parse(rawData)
        .returns(Right(requestData))

      import controller.endpointLogContext

      val mappingChecks: RequestHandler[TriggerCalculationResponse, TriggerCalculationResponse] => Unit = allChecks[TriggerCalculationResponse, TriggerCalculationResponse](
        ("FORMAT_NINO", BAD_REQUEST, NinoFormatError, BAD_REQUEST),
        ("FORMAT_TAX_YEAR", BAD_REQUEST, TaxYearFormatError, BAD_REQUEST),
        ("RULE_TAX_YEAR_NOT_SUPPORTED", BAD_REQUEST, RuleTaxYearNotSupportedError, BAD_REQUEST),
        ("RULE_TAX_YEAR_RANGE_EXCEEDED", BAD_REQUEST, RuleTaxYearRangeExceededError, BAD_REQUEST),
        ("RULE_INCORRECT_OR_EMPTY_BODY_SUBMITTED", BAD_REQUEST, RuleIncorrectOrEmptyBodyError, BAD_REQUEST),
        ("RULE_NO_INCOME_SUBMISSIONS_EXIST", FORBIDDEN, RuleNoIncomeSubmissionsExistError, FORBIDDEN),
        ("INTERNAL_SERVER_ERROR", INTERNAL_SERVER_ERROR, DownstreamError, INTERNAL_SERVER_ERROR)
      )

      MockStandardService
        .doServiceWithMappings(mappingChecks)
        .returns(Future.successful(Right(ResponseWrapper(correlationId, TriggerCalculationResponse("f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c")))))

      MockHateoasFactory
        .wrap(response, TriggerCalculationHateaosData(nino, "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c"))
        .returns(HateoasWrapper(response, Seq(testHateoasLink)))

      val result: Future[Result] = controller.triggerCalculation(nino)(fakePostRequest(Json.toJson(triggerCalculation)))

      header("X-CorrelationId", result) shouldBe Some(correlationId)
    }
  }
}
