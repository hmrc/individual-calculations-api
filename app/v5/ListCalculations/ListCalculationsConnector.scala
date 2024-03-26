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

package v5.ListCalculations

import api.connectors.DownstreamUri.{DesUri, TaxYearSpecificIfsUri}
import api.connectors.httpparsers.StandardDownstreamHttpParser.reads
import api.connectors.{BaseDownstreamConnector, DownstreamOutcome}
import config.AppConfig
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient}
import v5.ListCalculations.model.request.{Def1_ListCalculationsRequestData, ListCalculationsRequestData}
import v5.ListCalculations.model.response.Def1_ListCalculationsResponse.ListCalculations

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ListCalculationsConnector @Inject()(val http: HttpClient, val appConfig: AppConfig) extends BaseDownstreamConnector {

  def list(request: ListCalculationsRequestData)(implicit
                                                 hc: HeaderCarrier,
                                                 ec: ExecutionContext,
                                                 correlationId: String
  ): Future[DownstreamOutcome[ListCalculations]] = {

    request match {
      case tysDef1: Def1_ListCalculationsRequestData if tysDef1.taxYear.useTaxYearSpecificApi =>
        import tysDef1._

        val downstreamUri = TaxYearSpecificIfsUri[ListCalculations](s"income-tax/view/calculations/liability/${taxYear.asTysDownstream}/$nino")
        val result = get(downstreamUri)
        result

      case nonTysDef1: Def1_ListCalculationsRequestData =>
        import nonTysDef1._

        val downstreamUri = DesUri[ListCalculations](s"income-tax/list-of-calculation-results/$nino?taxYear=${taxYear.asDownstream}")
        val result = get(downstreamUri)
        result
    }
  }
}
