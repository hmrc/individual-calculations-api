/*
 * Copyright 2021 HM Revenue & Customs
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

package v1r2.mocks.connectors

import org.scalamock.handlers.CallHandler
import org.scalamock.scalatest.MockFactory
import uk.gov.hmrc.http.HeaderCarrier
import v1r2.connectors.{BackendOutcome, IntentToCrystalliseConnector}
import v1r2.models.request.intentToCrystallise.IntentToCrystalliseRequest
import v1r2.models.response.intentToCrystallise.IntentToCrystalliseResponse

import scala.concurrent.{ExecutionContext, Future}

trait MockIntentToCrystalliseConnector extends MockFactory {

  val mockIntentToCrystalliseConnector: IntentToCrystalliseConnector = mock[IntentToCrystalliseConnector]

  object MockIntentToCrystalliseConnector {

    def submitIntent(request: IntentToCrystalliseRequest): CallHandler[Future[BackendOutcome[IntentToCrystalliseResponse]]] = {
      (mockIntentToCrystalliseConnector
        .submitIntentToCrystallise(_: IntentToCrystalliseRequest)(_: HeaderCarrier, _: ExecutionContext,  _: String))
        .expects(request, *, *, *)
    }
  }
}