/*
 * Copyright 2026 HM Revenue & Customs
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

package v9.deleteProvisionalFiguresInformation

import api.connectors.DownstreamOutcome
import org.scalamock.handlers.CallHandler
import org.scalamock.scalatest.MockFactory
import org.scalatest.TestSuite
import uk.gov.hmrc.http.HeaderCarrier
import v9.deleteProvisionalFiguresInformation.model.request.DeleteProvisionalFiguresInformationRequestData

import scala.concurrent.{ExecutionContext, Future}

trait MockDeleteProvisionalFiguresInformationConnector extends TestSuite with MockFactory {

  val mockConnector: DeleteProvisionalFiguresInformationConnector = mock[DeleteProvisionalFiguresInformationConnector]

  object MockDeleteProvisionalFiguresInformationConnector {

    def deleteProvisionalFiguresInformation(request: DeleteProvisionalFiguresInformationRequestData): CallHandler[Future[DownstreamOutcome[Unit]]] =
      (mockConnector
        .deleteProvisionalFiguresInformation(_: DeleteProvisionalFiguresInformationRequestData)(_: HeaderCarrier, _: ExecutionContext, _: String))
        .expects(request, *, *, *)

  }

}
