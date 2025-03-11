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

import shared.connectors.DownstreamUri.{DesUri, HipUri, IfsUri}
import shared.connectors.httpparsers.StandardDownstreamHttpParser._
import shared.connectors.{BaseDownstreamConnector, DownstreamOutcome, DownstreamUri}
import shared.config.{AppConfig, ConfigFeatureSwitches}
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient}
import v7.listCalculations.model.request.{
  Def1_ListCalculationsRequestData,
  Def2_ListCalculationsRequestData,
  Def3_ListCalculationsRequestData,
  ListCalculationsRequestData
}
import v7.listCalculations.model.response.{Calculation, ListCalculationsResponse}

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

    // API1404
    lazy val downstreamUri1404: DownstreamUri[DownstreamResp] =
      if (ConfigFeatureSwitches().isEnabled("des_hip_migration_1404")) {
        HipUri[DownstreamResp](s"itsd/calculations/liability/$nino?taxYear=${taxYear.asDownstream}")
      } else {
        DesUri(s"income-tax/list-of-calculation-results/$nino?taxYear=${taxYear.asDownstream}")
      }
    // API2150
    lazy val downstreamUri2150: DownstreamUri[DownstreamResp] =
      IfsUri(s"income-tax/${taxYear.asTysDownstream}/view/calculations-summary/$nino")
    // API2083
    lazy val downstreamUri2083: DownstreamUri[DownstreamResp] =
      IfsUri(s"income-tax/${taxYear.asTysDownstream}/view/$nino/calculations-summary")

    request match {
      case Def1_ListCalculationsRequestData(_, _, _) =>
        get(downstreamUri1404)
      case Def2_ListCalculationsRequestData(_, _, calculationType) =>
        val calcType: Option[String] = calculationType.map(_.to2150Downstream)
        get(downstreamUri2150, params(calcType))
      case Def3_ListCalculationsRequestData(_, _, calculationType) =>
        val calcType: Option[String] = calculationType.map(_.toDownstream)
        get(downstreamUri2083, params(calcType))
    }
  }

  def params(calcType: Option[String]): Seq[(String, String)] =
    List("calculationType" -> calcType).collect { case (key, Some(value)) => key -> value }

}
