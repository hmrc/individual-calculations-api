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

package v6.triggerCalculation

import config.CalculationsFeatureSwitches
import shared.connectors.DownstreamUri.{DesUri, IfsUri}
import shared.connectors.httpparsers.StandardDownstreamHttpParser._
import shared.connectors.{BaseDownstreamConnector, DownstreamOutcome}
import shared.config.AppConfig
import play.api.http.Status
import play.api.libs.json.JsObject
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.HeaderCarrier
import v6.triggerCalculation.model.request.TriggerCalculationRequestData
import v6.triggerCalculation.model.response.TriggerCalculationResponse

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class TriggerCalculationConnector @Inject()(val http: HttpClientV2, val appConfig: AppConfig)(featureSwitches: CalculationsFeatureSwitches)
    extends BaseDownstreamConnector {

  def triggerCalculation(request: TriggerCalculationRequestData)(implicit
      hc: HeaderCarrier,
      ec: ExecutionContext,
      correlationId: String): Future[DownstreamOutcome[TriggerCalculationResponse]] = {

    import request._
    import schema._

    val path = s"income-tax/nino/$nino/taxYear/${taxYear.asDownstream}/tax-calculation?crystallise=$finalDeclaration"

    if (taxYear.useTaxYearSpecificApi) {
      implicit val successCode: SuccessCode = SuccessCode(Status.ACCEPTED)
      val downstreamUri =
        IfsUri[DownstreamResp](s"income-tax/calculation/${taxYear.asTysDownstream}/$nino?crystallise=$finalDeclaration")
      post(JsObject.empty, downstreamUri)
    } else if (featureSwitches.isDesIf_MigrationEnabled) {
      val downstreamUri = IfsUri[DownstreamResp](path)
      post(JsObject.empty, downstreamUri)
    } else {
      val downstreamUri = DesUri[DownstreamResp](path)
      post(JsObject.empty, downstreamUri)
    }

  }

}
