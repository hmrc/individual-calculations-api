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
import play.api.libs.json.{JsObject, Json, Reads}
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient}
import v3.connectors.DownstreamUri.DesUri
import v3.connectors.httpparsers.StandardHttpParser._
import v3.models.domain.{Nino, TaxYear}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

// FIXME REMOVE WHEN MODELS AVAILABLE
case class TriggerResponse(id: String)

object TriggerResponse {
  implicit val reads: Reads[TriggerResponse] = Json.reads
}

@Singleton
class TriggerCalculationConnector @Inject() (val http: HttpClient, val appConfig: AppConfig) extends BaseDownstreamConnector {

  def triggerCalculation(nino: Nino, taxYear: TaxYear, finalDeclaration: Boolean)(implicit
      hc: HeaderCarrier,
      ec: ExecutionContext,
      correlationId: String): Future[DownstreamOutcome[TriggerResponse]] = {

    val ninoString        = nino.value
    val downstreamTaxYear = taxYear.toDownstream
    val crystallize       = BooleanUtils.toStringTrueFalse(finalDeclaration)

    // Note using empty JSON object ({}) which works for v2 tax calc...
    post(
      body = JsObject.empty,
      uri = DesUri[TriggerResponse](s"income-tax/nino/$ninoString/taxYear/$downstreamTaxYear/tax-calculation?crystallise=$crystallize")
    )

  }

}
