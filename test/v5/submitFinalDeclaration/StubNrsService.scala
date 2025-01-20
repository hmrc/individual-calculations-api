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

package v5.submitFinalDeclaration

import api.nrs.MockNrsProxyConnector
import com.github.pjfanning.pekko.scheduler.mock.{MockScheduler, VirtualTime}
import mocks.MockCalculationsConfig
import shared.controllers.RequestContext
import shared.services.ServiceSpec
import v5.retrieveCalculation.MockRetrieveCalculationService
import v5.submitFinalDeclaration.model.request.SubmitFinalDeclarationRequestData

import java.util.concurrent.ConcurrentLinkedQueue
import scala.concurrent.{ExecutionContext, Future}

trait StubNrsService extends ServiceSpec with MockRetrieveCalculationService with MockNrsProxyConnector with MockCalculationsConfig {
  private val called = new ConcurrentLinkedQueue[NrsProxyCall]()

  val virtualTime                           = new VirtualTime
  implicit val mockScheduler: MockScheduler = virtualTime.scheduler

  val stubNrsService: NrsService = new NrsService(mockNrsProxyConnector, mockRetrieveCalculationService, mockCalculationsConfig) {

    override def updateNrs(nino: String, body: SubmitFinalDeclarationRequestData)(implicit ctx: RequestContext, ec: ExecutionContext): Future[Unit] =
      Future.successful(called.add(NrsProxyCall(nino, "itsa-crystallisation", body)))

  }

  def verifyNrsProxyService(expectations: NrsProxyCall*): Unit = {
    if (called.isEmpty) fail(s"No calls were made to this service, expected ${expectations.length}")
    called should contain theSameElementsAs expectations
  }

  def resetNrsProxyService(): Unit = called.clear()

  protected case class NrsProxyCall(nino: String, notableEvent: String, body: SubmitFinalDeclarationRequestData)
}
