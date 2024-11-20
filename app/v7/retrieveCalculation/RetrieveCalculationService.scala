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

package v7.retrieveCalculation

import shared.controllers.RequestContext
import shared.services.{BaseService, ServiceOutcome}
import cats.implicits._
import v7.retrieveCalculation.downstreamErrorMapping.RetrieveCalculationDownstreamErrorMapping.errorMapFor
import v7.retrieveCalculation.models.request.RetrieveCalculationRequestData
import v7.retrieveCalculation.models.response.RetrieveCalculationResponse

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class RetrieveCalculationService @Inject() (connector: RetrieveCalculationConnector) extends BaseService {

  def retrieveCalculation(request: RetrieveCalculationRequestData)(implicit
      ctx: RequestContext,
      ec: ExecutionContext): Future[ServiceOutcome[RetrieveCalculationResponse]] = {

    connector.retrieveCalculation(request).map(_.leftMap(mapDownstreamErrors(errorMapFor(request.taxYear).errorMap)))

  }
}
