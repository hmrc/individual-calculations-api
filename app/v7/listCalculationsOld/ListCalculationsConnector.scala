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

package v7.listCalculationsOld

import shared.connectors.DownstreamUri.{DesUri, HipUri, IfsUri}
import shared.connectors.httpparsers.StandardDownstreamHttpParser._
import shared.connectors.{BaseDownstreamConnector, DownstreamOutcome, DownstreamUri}
import shared.config.{AppConfig, ConfigFeatureSwitches}
import shared.models.domain.TaxYear
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient}
import v7.listCalculationsOld.def1.model.response.Calculation
import v7.listCalculationsOld.model.request.ListCalculationsRequestData
import v7.listCalculationsOld.model.response.ListCalculationsResponse

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

    //API1404
     lazy val downstreamUri1404: DownstreamUri[DownstreamResp] =
      if (ConfigFeatureSwitches().isEnabled("des_hip_migration_1404")) {
        HipUri[DownstreamResp](s"itsd/calculations/liability/$nino?taxYear=${taxYear.asDownstream}")
      } else {
        DesUri(s"income-tax/list-of-calculation-results/$nino?taxYear=${taxYear.asDownstream}")
      }
    //API2150
    lazy val downstreamUri2150: DownstreamUri[DownstreamResp] =
      IfsUri(s"income-tax/${taxYear.asTysDownstream}/view/calculations-summary/$nino")
    //API2083
    lazy val downstreamUri2083: DownstreamUri[DownstreamResp] =
      IfsUri(s"income-tax/${taxYear.asTysDownstream}/view/$nino/calculations-summary")

    val downstreamUri: DownstreamUri[DownstreamResp] =
      if (taxYear.year >= 2026) {
        downstreamUri2083
      } else if (taxYear.year == 2024 || taxYear.year == 2025) {
        downstreamUri2150
      } else {
        downstreamUri1404
      }

    get(downstreamUri)
  }

}
