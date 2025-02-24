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

package v8.submitFinalDeclaration

import api.nrs.{MockNrsProxyConnector, NrsFixture}
import mocks.MockCalculationsConfig
import org.apache.pekko.actor.{ActorSystem, Scheduler}
import org.scalamock.matchers.ArgCapture.CaptureOne
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.{JsValue, Json}
import play.api.{Application, Environment, Mode}
import shared.models.domain.{CalculationId, Nino, TaxYear}
import shared.models.errors.{ErrorWrapper, InternalError}
import shared.models.outcomes.ResponseWrapper
import shared.services.ServiceSpec
import v8.common.model.domain.{CalculationType, `final-declaration`}
import v8.retrieveCalculation.MockRetrieveCalculationService
import v8.retrieveCalculation.def1.model.Def1_CalculationFixture
import v8.retrieveCalculation.models.request.Def1_RetrieveCalculationRequestData
import v8.retrieveCalculation.models.response.Def1_RetrieveCalculationResponse
import v8.submitFinalDeclaration.model.request.{Def1_SubmitFinalDeclarationRequestData, SubmitFinalDeclarationRequestData}

import scala.concurrent.Future
import scala.concurrent.duration.DurationInt

class NrsProxyServiceSpec extends ServiceSpec with NrsFixture with Def1_CalculationFixture with GuiceOneAppPerSuite {

  override lazy val app: Application = new GuiceApplicationBuilder()
    .in(Environment.simple(mode = Mode.Dev))
    .configure("metrics.enabled" -> "false")
    .build()

  val actorSystem                              = app.injector.instanceOf[ActorSystem]
  implicit val scheduler: Scheduler            = actorSystem.scheduler
  private val taxYear                          = TaxYear.fromMtd("2020-21")
  private val calculationId                    = CalculationId("4557ecb5-fd32-48cc-81f5-e6acd1099f3c")
  private val calculationType: CalculationType = `final-declaration`
  private val retrieveDetailsRequestData       = Def1_RetrieveCalculationRequestData(Nino(nino), taxYear, calculationId)
  private val retrieveDetailsResponseData      = minimalCalculationR8bResponse

  val request: SubmitFinalDeclarationRequestData = Def1_SubmitFinalDeclarationRequestData(
    Nino(nino),
    taxYear,
    calculationId,
    calculationType
  )

  trait Test extends MockRetrieveCalculationService with MockCalculationsConfig with MockNrsProxyConnector {

    val service: NrsProxyService = new NrsProxyService(mockNrsProxyConnector, mockRetrieveCalculationService, mockCalculationsConfig)
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

        MockNrsProxyConnector
          .submit(nino, "itsa-crystallisation", nrsBodyCapture)
          .returns(Future.successful(Right(())))

        val result = await(service.updateNrs(nino, request))
        result shouldBe (): Unit
        nrsBodyCapture.value shouldBe Json.toJson(retrieveDetailsResponseData)
      }

      "submit fallback details to NRS when retrieveCalculationDetails fails" in new Test {
        val nrsBodyCapture: CaptureOne[JsValue] = CaptureOne[JsValue]()
        MockCalculationsConfig.retrieveCalcRetries
          .returns(List(500.millis, 500.millis, 1000.millis))

        MockRetrieveCalculationService
          .retrieveCalculation(retrieveDetailsRequestData)
          .returns(Future.successful(Left(ErrorWrapper(requestContext.correlationId, InternalError))))
          .repeat(3)

        MockNrsProxyConnector
          .submit(nino, "itsa-crystallisation", nrsBodyCapture)
          .returns(Future.successful(Right(())))

        val result = await(service.updateNrs(nino, request))
        result shouldBe (): Unit
        nrsBodyCapture.value shouldBe request.toNrsJson
      }

    }
  }

}
