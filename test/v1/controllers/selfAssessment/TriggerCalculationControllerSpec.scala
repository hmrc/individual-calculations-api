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

package v1.controllers.selfAssessment

import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{AnyContentAsJson, ControllerComponents, Result}
import uk.gov.hmrc.domain.Nino
import uk.gov.hmrc.http.HeaderCarrier
import utils.Logging
import v1.controllers.ControllerBaseSpec
import v1.handling.RequestDefn
import v1.mocks.requestParsers.MockTriggerCalculationParser
import v1.mocks.services.{MockEnrolmentsAuthService, MockMtdIdLookupService, MockStandardService}
import v1.models.response.selfAssessment.{ListCalculationsResponse, TriggerCalculationResponse}
import v1.models.domain.TriggerCalculation
import v1.models.errors._
import v1.models.outcomes.ResponseWrapper
import v1.models.request.selfAssessment.{TriggerCalculationRawData, TriggerCalculationRequest}
import v1.support.BackendResponseMappingSupport

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class TriggerCalculationControllerSpec extends ControllerBaseSpec
  with MockEnrolmentsAuthService
  with MockMtdIdLookupService
  with MockTriggerCalculationParser
  with MockStandardService {

  trait Test {
    val hc = HeaderCarrier()

    val controller = new TriggerCalculationController(
      authService = mockEnrolmentsAuthService,
      lookupService = mockMtdIdLookupService,
      triggerCalculationParser = mockTriggerCalculationParser,
      service = mockStandardService,
      cc = cc
    )

    MockedMtdIdLookupService.lookup(nino).returns(Future.successful(Right("test-mtd-id")))
    MockedEnrolmentsAuthService.authoriseUser()
  }

  private val nino          = "AA123456A"
  private val taxYear       = "2017-18"
  private val correlationId = "X-123"

  val response = TriggerCalculationResponse("f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c")

  val json: JsValue = Json.parse("""{"id" : "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c"}""")

  val triggerCalculation = TriggerCalculation(taxYear)

  val rawData = TriggerCalculationRawData(nino, AnyContentAsJson(Json.toJson(triggerCalculation)))
  val requestData = TriggerCalculationRequest(Nino(nino), taxYear)

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

        val result: Future[Result] = controller.triggerCalculation(nino)(fakePostRequest(Json.toJson(triggerCalculation)))

        status(result) shouldBe ACCEPTED
        contentAsJson(result) shouldBe json
        header("X-CorrelationId", result) shouldBe Some(correlationId)
      }
    }

    "map service error mapping according to spec" in new Test with BackendResponseMappingSupport with Logging {
      MockTriggerCalculationParser
        .parse(rawData)
        .returns(Right(requestData))

      import controller.endpointLogContext

      val mappingChecks = allChecks[TriggerCalculationResponse, TriggerCalculationResponse](
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
        .returns(Future.successful(Right(ResponseWrapper(correlationId, TriggerCalculationResponse("fakeId")))))

      val result: Future[Result] = controller.triggerCalculation(nino)(fakePostRequest(Json.toJson(triggerCalculation)))

      header("X-CorrelationId", result) shouldBe Some(correlationId)
    }
  }
}
