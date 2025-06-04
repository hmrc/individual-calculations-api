/*
 * Copyright 2025 HM Revenue & Customs
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

package v5.listCalculations

import shared.connectors.DownstreamUri.{HipUri, IfsUri}
import shared.connectors.httpparsers.StandardDownstreamHttpParser._
import shared.connectors.{BaseDownstreamConnector, DownstreamOutcome, DownstreamUri}
import shared.config.{AppConfig, ConfigFeatureSwitches}
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient}
import v5.listCalculations.def1.model.response.Calculation
import v5.listCalculations.model.request.ListCalculationsRequestData
import v5.listCalculations.model.response.ListCalculationsResponse

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

    lazy val downstreamUri1404: DownstreamUri[DownstreamResp] =
      HipUri(s"itsd/calculations/liability/$nino?taxYear=${taxYear.asDownstream}")

    lazy val downstreamUri1896: DownstreamUri[DownstreamResp] =
      if (ConfigFeatureSwitches().isEnabled("ifs_hip_migration_1896")) {
        HipUri(s"itsa/income-tax/v1/${taxYear.asTysDownstream}/view/calculations/liability/$nino")
      } else {
        IfsUri(s"income-tax/view/calculations/liability/${taxYear.asTysDownstream}/$nino")
      }

    val downstreamUri: DownstreamUri[DownstreamResp] =
      if (taxYear.useTaxYearSpecificApi) downstreamUri1896 else downstreamUri1404

    get(downstreamUri)
  }

}
