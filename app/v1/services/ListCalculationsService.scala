/*
 * Copyright 2019 HM Revenue & Customs
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

package v1.services

import javax.inject.Inject
import uk.gov.hmrc.http.HeaderCarrier
import utils.Logging
import v1.connectors.IndividualCalculationsConnector
import v1.controllers.EndpointLogContext
import v1.models.domain.selfAssessment.ListCalculationsResponse
import v1.models.errors._
import v1.models.outcomes.ResponseWrapper
import v1.models.requestData.selfAssessment.ListCalculationsRequest
import v1.support.BackendResponseMappingSupport

import scala.concurrent.{ExecutionContext, Future}

class ListCalculationsService @Inject()(connector: IndividualCalculationsConnector) extends BackendResponseMappingSupport with Logging {

  def listCalculations(request: ListCalculationsRequest)(
      implicit logContext: EndpointLogContext,
      ec: ExecutionContext,
      hc: HeaderCarrier): Future[Either[ErrorWrapper, ResponseWrapper[ListCalculationsResponse]]] = {
    connector.listTaxCalculations(request).map(directMap[ListCalculationsResponse](passthroughErrors, customErrorMapping))
  }

  private def passthroughErrors: List[MtdError] = List(
    NinoFormatError,
    TaxYearFormatError,
    RuleTaxYearNotSupportedError,
    RuleTaxYearRangeExceededError,
    NotFoundError,
    DownstreamError
  )

  private def customErrorMapping: PartialFunction[String, (Int, MtdError)] = PartialFunction.empty

}
