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

package v7.listCalculations

import shared.connectors.DownstreamOutcome
import org.scalamock.handlers.CallHandler
import org.scalamock.scalatest.MockFactory
import uk.gov.hmrc.http.HeaderCarrier
import v7.listCalculations.def1.model.response.Calculation
import v7.listCalculations.model.request.ListCalculationsRequestData
import v7.listCalculations.model.response.ListCalculationsResponse

import scala.concurrent.{ExecutionContext, Future}

trait MockListCalculationsConnector extends MockFactory {
  val mockListCalculationsConnector: ListCalculationsConnector = mock[ListCalculationsConnector]

  object MockListCalculationsConnector {

    def list[I](request: ListCalculationsRequestData): CallHandler[Future[DownstreamOutcome[ListCalculationsResponse[Calculation]]]] = {
      (mockListCalculationsConnector
        .list(_: ListCalculationsRequestData)(_: HeaderCarrier, _: ExecutionContext, _: String))
        .expects(request, *, *, *)
    }

  }

}
