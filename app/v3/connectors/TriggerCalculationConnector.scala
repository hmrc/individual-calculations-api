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

import config.AppConfig
import play.api.libs.json.JsObject
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient}
import v3.connectors.DownstreamUri.{DesUri, TaxYearSpecificIfsUri}
import v3.connectors.httpparsers.StandardDownstreamHttpParser._
import v3.models.request.TriggerCalculationRequest
import v3.models.response.triggerCalculation.TriggerCalculationResponse

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class TriggerCalculationConnector @Inject() (val http: HttpClient, val appConfig: AppConfig) extends BaseDownstreamConnector {

  def triggerCalculation(request: TriggerCalculationRequest)(implicit
      hc: HeaderCarrier,
      ec: ExecutionContext,
      correlationId: String): Future[DownstreamOutcome[TriggerCalculationResponse]] = {

    import request._

    val downstreamUri = if (taxYear.useTaxYearSpecificApi) {
      TaxYearSpecificIfsUri[TriggerCalculationResponse](
        s"income-tax/calculation/${taxYear.asTysDownstream}/${nino.value}?crystallise=$finalDeclaration")
    } else {
      DesUri[TriggerCalculationResponse](
        s"income-tax/nino/${nino.value}/taxYear/${taxYear.asDownstream}/tax-calculation?crystallise=$finalDeclaration")
    }

    // Note using empty JSON object ({}) which works for v2 tax calc...
    post(body = JsObject.empty, uri = downstreamUri)

  }

}
