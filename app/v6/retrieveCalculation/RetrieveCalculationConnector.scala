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

package v6.retrieveCalculation

import shared.connectors.DownstreamUri.IfsUri
import shared.connectors.httpparsers.StandardDownstreamHttpParser._
import shared.connectors.{BaseDownstreamConnector, DownstreamOutcome, DownstreamUri}
import shared.config.AppConfig
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient}
import v6.retrieveCalculation.models.request.RetrieveCalculationRequestData
import v6.retrieveCalculation.models.response.RetrieveCalculationResponse

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class RetrieveCalculationConnector @Inject() (val http: HttpClient, val appConfig: AppConfig) extends BaseDownstreamConnector {

  def retrieveCalculation(request: RetrieveCalculationRequestData)(implicit
      hc: HeaderCarrier,
      ec: ExecutionContext,
      correlationId: String): Future[DownstreamOutcome[RetrieveCalculationResponse]] = {

    import request._
    import schema._

    val downstreamUri: DownstreamUri[DownstreamResp] = if (taxYear.useTaxYearSpecificApi) {
      IfsUri(s"income-tax/view/calculations/liability/${taxYear.asTysDownstream}/$nino/$calculationId")
    } else {
      IfsUri(s"income-tax/view/calculations/liability/$nino/$calculationId")
    }

    get(downstreamUri)
  }

}
