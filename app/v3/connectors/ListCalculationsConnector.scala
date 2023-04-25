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

package v3.connectors

import api.connectors.{BaseDownstreamConnector, DownstreamOutcome}
import config.AppConfig
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient}
import api.connectors.DownstreamUri.{DesUri, TaxYearSpecificIfsUri}
import v3.models.request.ListCalculationsRequest
import v3.models.response.listCalculations.ListCalculationsResponse.ListCalculations

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ListCalculationsConnector @Inject() (val http: HttpClient, val appConfig: AppConfig) extends BaseDownstreamConnector {

  def list(request: ListCalculationsRequest)(implicit
      hc: HeaderCarrier,
      ec: ExecutionContext,
      correlationId: String
  ): Future[DownstreamOutcome[ListCalculations]] = {
    import api.connectors.httpparsers.StandardDownstreamHttpParser._
    import request._

    val downstreamUri =
      if (taxYear.useTaxYearSpecificApi) {
        TaxYearSpecificIfsUri[ListCalculations](s"income-tax/view/calculations/liability/${taxYear.asTysDownstream}/${nino.nino}")
      } else {
        DesUri[ListCalculations](s"income-tax/list-of-calculation-results/${nino.nino}?taxYear=${taxYear.asDownstream}")
      }

    get(downstreamUri)
  }

}
