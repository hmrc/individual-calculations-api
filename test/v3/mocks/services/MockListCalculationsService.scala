/*
 * Copyright 2022 HM Revenue & Customs
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
import uk.gov.hmrc.http.HeaderCarrier
import v3.controllers.EndpointLogContext
import v3.models.errors.ErrorWrapper
import v3.models.outcomes.ResponseWrapper
import v3.models.request.ListCalculationsRequest
import v3.models.response.listCalculations.ListCalculationsResponse.ListCalculations
import v3.services.ListCalculationsService

import scala.concurrent.{ExecutionContext, Future}

trait MockListCalculationsService extends MockFactory {
  val mockListCalculationsService: ListCalculationsService = mock[ListCalculationsService]

  object MockListCalculationsService {

    def list[I](requestData: ListCalculationsRequest): CallHandler[Future[Either[ErrorWrapper, ResponseWrapper[ListCalculations]]]] = {
      (mockListCalculationsService
        .list(_: ListCalculationsRequest)(_: HeaderCarrier, _: ExecutionContext, _: EndpointLogContext, _: String))
        .expects(requestData, *, *, *, *)
    }

  }

}
