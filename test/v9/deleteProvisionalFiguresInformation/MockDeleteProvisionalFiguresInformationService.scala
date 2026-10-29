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

import api.controllers.RequestContext
import api.services.ServiceOutcome
import org.scalamock.handlers.CallHandler
import org.scalamock.scalatest.MockFactory
import org.scalatest.TestSuite
import v9.deleteProvisionalFiguresInformation.model.request.DeleteProvisionalFiguresInformationRequestData

import scala.concurrent.{ExecutionContext, Future}

trait MockDeleteProvisionalFiguresInformationService extends TestSuite with MockFactory {

  val mockDeleteProvisionalFiguresInformationService: DeleteProvisionalFiguresInformationService = mock[DeleteProvisionalFiguresInformationService]

  object MockDeleteProvisionalFiguresInformationService {

    def deleteProvisionalFiguresInformation(requestData: DeleteProvisionalFiguresInformationRequestData): CallHandler[Future[ServiceOutcome[Unit]]] =
      (
        mockDeleteProvisionalFiguresInformationService
          .deleteProvisionalFiguresInformation(_: DeleteProvisionalFiguresInformationRequestData)(
            _: RequestContext,
            _: ExecutionContext
          )
        )
        .expects(requestData, *, *)

  }

}
