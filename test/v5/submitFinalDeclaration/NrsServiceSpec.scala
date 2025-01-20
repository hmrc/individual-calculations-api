/*
 * Copyright 2025 HM Revenue & Customs
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

package v5.submitFinalDeclaration

import api.nrs.{NrsFixture, NrsProxyConnector}
import com.github.pjfanning.pekko.scheduler.mock.{MockScheduler, VirtualTime}
import mocks.MockCalculationsConfig
import org.scalamock.matchers.ArgCapture.CaptureOne
import play.api.libs.json.{JsValue, Json}
import shared.models.domain.{CalculationId, Nino, TaxYear}
import shared.models.errors.{ErrorWrapper, InternalError}
import shared.models.outcomes.ResponseWrapper
import shared.services.ServiceSpec
import uk.gov.hmrc.http.HeaderCarrier
import v5.retrieveCalculation.MockRetrieveCalculationService
import v5.retrieveCalculation.def1.model.Def1_CalculationFixture
import v5.retrieveCalculation.models.request.Def1_RetrieveCalculationRequestData
import v5.retrieveCalculation.models.response.Def1_RetrieveCalculationResponse
import v5.submitFinalDeclaration.model.request.{Def1_SubmitFinalDeclarationRequestData, SubmitFinalDeclarationRequestData}

import scala.concurrent.Future
import scala.concurrent.duration.DurationInt

class NrsServiceSpec extends ServiceSpec with NrsFixture with Def1_CalculationFixture {

  val virtualTime                           = new VirtualTime
  implicit val mockScheduler: MockScheduler = virtualTime.scheduler
  private val taxYear                       = TaxYear.fromMtd("2020-21")
  private val calculationId                 = CalculationId("4557ecb5-fd32-48cc-81f5-e6acd1099f3c")
  private val retrieveDetailsRequestData    = Def1_RetrieveCalculationRequestData(Nino(nino), taxYear, calculationId)
  private val retrieveDetailsResponseData   = minimalCalculationR8bResponse
  val mockNrsProxyConnector                 = mock[NrsProxyConnector]

  val request: SubmitFinalDeclarationRequestData = Def1_SubmitFinalDeclarationRequestData(
    Nino(nino),
    taxYear,
    calculationId
  )

  trait Test extends MockRetrieveCalculationService with MockCalculationsConfig {

    val service: NrsService = new NrsService(mockNrsProxyConnector, mockRetrieveCalculationService, mockCalculationsConfig)
  }

  "NrsService" when {

    "updateNrs is called" must {
      "submit full details to NRS when retrieveCalculationDetails succeeds" in new Test {
        val nrsBodyCapture: CaptureOne[JsValue] = CaptureOne[JsValue]()

        MockCalculationsConfig.retrieveCalcRetries
          .returns(List(500.millis, 500.millis, 1000.millis))

        MockRetrieveCalculationService
          .retrieveCalculation(retrieveDetailsRequestData)
          .returns(Future.successful(Right(ResponseWrapper[Def1_RetrieveCalculationResponse]("correlationId", retrieveDetailsResponseData))))

        (mockNrsProxyConnector
          .submitAsync(_: String, _: String, _: JsValue)(_: HeaderCarrier))
          .expects(*, *, capture(nrsBodyCapture), *)
          .returns(Future.successful(Right(())))
//        MockNrsProxyConnector
//          .submit(nino, event, nrsBodyCapture)
//          .returns(Future.successful(Right(())))

        service.updateNrs(nino, request)
        nrsBodyCapture.value shouldBe Json.toJson(retrieveDetailsResponseData)
      }

      "submit fallback details to NRS when retrieveCalculationDetails fails" in new Test {

        MockCalculationsConfig.retrieveCalcRetries
          .returns(List(500.millis, 500.millis, 1000.millis))

        MockRetrieveCalculationService
          .retrieveCalculation(retrieveDetailsRequestData)
          .returns(Future.successful(Left(ErrorWrapper(requestContext.correlationId, InternalError))))

        (mockNrsProxyConnector
          .submitAsync(_: String, _: String, _: JsValue)(_: HeaderCarrier))
          .expects(*, *, *, *)
          .returns(Future.successful(Right(())))

        service.updateNrs(nino, request)
//        mockNrsProxyConnector.submitAsync(nino, event, request.toNrsJson).
      }

    }
  }

}
