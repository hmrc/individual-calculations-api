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

package v3.connectors

import config.AppConfig
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient}
import v3.connectors.DownstreamUri.{IfsUri, TaxYearSpecificIfsUri}
import v3.models.request.RetrieveCalculationRequest
import v3.models.response.retrieveCalculation.RetrieveCalculationResponse

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
import v3.connectors.httpparsers.StandardDownstreamHttpParser._

@Singleton
class RetrieveCalculationConnector @Inject() (val http: HttpClient, val appConfig: AppConfig) extends BaseDownstreamConnector {

  def retrieveCalculation(request: RetrieveCalculationRequest)(implicit
      hc: HeaderCarrier,
      ec: ExecutionContext,
      correlationId: String): Future[DownstreamOutcome[RetrieveCalculationResponse]] = {

    import request._

    val downstreamUri = if (taxYear.useTaxYearSpecificApi) {
      TaxYearSpecificIfsUri[RetrieveCalculationResponse](
        s"income-tax/view/calculations/liability/${taxYear.asTysDownstream}/${nino.nino}/$calculationId")
    } else {
      IfsUri[RetrieveCalculationResponse](s"income-tax/view/calculations/liability/${nino.nino}/$calculationId")
    }

    get(uri = downstreamUri)
  }

}
