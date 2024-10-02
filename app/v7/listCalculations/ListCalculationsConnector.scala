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

import shared.connectors.DownstreamUri.{DesUri, TaxYearSpecificIfsUri}
import shared.connectors.httpparsers.StandardDownstreamHttpParser._
import shared.connectors.{BaseDownstreamConnector, DownstreamOutcome, DownstreamUri}
import shared.config.AppConfig
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient}
import v7.listCalculations.def1.model.response.Calculation
import v7.listCalculations.model.request.ListCalculationsRequestData
import v7.listCalculations.model.response.ListCalculationsResponse

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ListCalculationsConnector @Inject() (val http: HttpClient, val appConfig: AppConfig) extends BaseDownstreamConnector {

  def list(request: ListCalculationsRequestData)(implicit
      hc: HeaderCarrier,
      ec: ExecutionContext,
      correlationId: String
  ): Future[DownstreamOutcome[ListCalculationsResponse[Calculation]]] = {

    import request._
    import schema._

    val downstreamUri: DownstreamUri[DownstreamResp] =
      if (taxYear.useTaxYearSpecificApi) {
        TaxYearSpecificIfsUri(s"income-tax/view/calculations/liability/${taxYear.asTysDownstream}/$nino")
      } else {
        DesUri(s"income-tax/list-of-calculation-results/$nino?taxYear=${taxYear.asDownstream}")
      }

    get(downstreamUri)
  }

}
