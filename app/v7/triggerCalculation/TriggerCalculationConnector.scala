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

package v7.triggerCalculation

import config.CalculationsFeatureSwitches
import play.api.http.Status
import shared.config.AppConfig
import shared.connectors.DownstreamUri.{DesUri, IfsUri}
import shared.connectors.httpparsers.StandardDownstreamHttpParser._
import shared.connectors.{BaseDownstreamConnector, DownstreamOutcome}
import shared.models.domain.EmptyJsonBody
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient}
import v7.triggerCalculation.model.request.TriggerCalculationRequestData
import v7.triggerCalculation.model.response.TriggerCalculationResponse
import v7.triggerCalculation.schema.TriggerCalculationSchema.Def1.DownstreamResp

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class TriggerCalculationConnector @Inject() (val http: HttpClient, val appConfig: AppConfig)(featureSwitches: CalculationsFeatureSwitches)
    extends BaseDownstreamConnector {

  def triggerCalculation(request: TriggerCalculationRequestData)(implicit
      hc: HeaderCarrier,
      ec: ExecutionContext,
      correlationId: String): Future[DownstreamOutcome[TriggerCalculationResponse]] = {

    import request._
    implicit val successCode: SuccessCode = SuccessCode(Status.ACCEPTED)

    val path = s"income-tax/nino/$nino/taxYear/${taxYear.asDownstream}/tax-calculation?crystallise=true"

    val downstreamUrl = if (taxYear.year <= 2023) {
      if (featureSwitches.isDesIf_MigrationEnabled) {
        IfsUri[DownstreamResp](path)
      } else {
        DesUri[DownstreamResp](path)
      }
    } else if (taxYear.year == 2024 || taxYear.year == 2025) {
      IfsUri[DownstreamResp](s"income-tax/calculation/${taxYear.asTysDownstream}/$nino?crystallise=true")
    } else {
      IfsUri[DownstreamResp](s"income-tax/${taxYear.asTysDownstream}/calculation/$nino/$calculationType")
    }

    post(EmptyJsonBody, downstreamUrl)

  }


}
