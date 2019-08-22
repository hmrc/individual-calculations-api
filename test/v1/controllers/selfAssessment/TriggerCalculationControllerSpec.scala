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
import v1.controllers.ControllerBaseSpec
import v1.handling.RequestDefn
import v1.mocks.requestParsers.MockTriggerCalculationParser
import v1.mocks.services.{MockEnrolmentsAuthService, MockMtdIdLookupService, MockStandardService}
import v1.models.backend.selfAssessment.{ListCalculationsResponse, TriggerCalculationResponse}
import v1.models.domain.TriggerCalculation
import v1.models.errors.NotFoundError
import v1.models.outcomes.ResponseWrapper
import v1.models.requestData.selfAssessment.{TriggerCalculationRawData, TriggerCalculationRequest}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class TriggerCalculationControllerSpec extends ControllerBaseSpec with GuiceOneAppPerSuite
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
    "return OK with list of calculations" when {
      "happy path" in new Test {
        MockTriggerCalculationParser
          .parse(rawData)
          .returns(Right(requestData))

        MockStandardService
          .doService(RequestDefn.Post(uri, Json.toJson(triggerCalculation)), OK)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, response))))

        val result: Future[Result] = controller.triggerCalculation(nino)(fakePostRequest(Json.toJson(triggerCalculation)))

        status(result) shouldBe OK
        contentAsJson(result) shouldBe json
        header("X-CorrelationId", result) shouldBe Some(correlationId)
      }
    }
  }
}
