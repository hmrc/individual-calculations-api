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

package v3.mocks.services

import org.scalamock.handlers.CallHandler
import org.scalamock.scalatest.MockFactory
import v3.controllers.RequestContext
import v3.models.errors.ErrorWrapper
import v3.models.outcomes.ResponseWrapper
import v3.models.request.RetrieveCalculationRequest
import v3.models.response.retrieveCalculation.RetrieveCalculationResponse
import v3.services.RetrieveCalculationService

import scala.concurrent.{ExecutionContext, Future}

trait MockRetrieveCalculationService extends MockFactory {

  val mockService: RetrieveCalculationService = mock[RetrieveCalculationService]

  object MockRetrieveCalculationService {

    def retrieveCalculation(
        request: RetrieveCalculationRequest): CallHandler[Future[Either[ErrorWrapper, ResponseWrapper[RetrieveCalculationResponse]]]] = {
      (mockService
        .retrieveCalculation(_: RetrieveCalculationRequest)(_: RequestContext, _: ExecutionContext))
        .expects(request, *, *)
    }

  }

}
