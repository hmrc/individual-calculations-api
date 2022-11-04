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
import org.apache.commons.lang3.BooleanUtils
import play.api.libs.json.JsObject
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient}
import v3.connectors.DownstreamUri.DesUri
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

    val ninoString        = request.nino.value
    val downstreamTaxYear = request.taxYear.asDownstream
    val crystallize       = BooleanUtils.toStringTrueFalse(request.finalDeclaration)

    // Note using empty JSON object ({}) which works for v2 tax calc...
    post(
      body = JsObject.empty,
      uri = DesUri[TriggerCalculationResponse](s"income-tax/nino/$ninoString/taxYear/$downstreamTaxYear/tax-calculation?crystallise=$crystallize")
    )

  }

}
