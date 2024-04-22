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

package v5.triggerCalculation

import api.connectors.DownstreamUri.{DesUri, TaxYearSpecificIfsUri}
import api.connectors.httpparsers.StandardDownstreamHttpParser._
import api.connectors.{BaseDownstreamConnector, DownstreamOutcome}
import config.AppConfig
import play.api.http.Status
import play.api.libs.json.JsObject
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient}
import v5.triggerCalculation.model.request.TriggerCalculationRequestData
import v5.triggerCalculation.model.response.TriggerCalculationResponse


import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class TriggerCalculationConnector @Inject() (val http: HttpClient, val appConfig: AppConfig) extends BaseDownstreamConnector {

  def triggerCalculation(request: TriggerCalculationRequestData)(implicit
      hc: HeaderCarrier,
      ec: ExecutionContext,
      correlationId: String): Future[DownstreamOutcome[TriggerCalculationResponse]] = {

    import request._
    import schema._

    if (taxYear.useTaxYearSpecificApi) {
      implicit val successCode: SuccessCode = SuccessCode(Status.ACCEPTED)

      val downstreamUri = TaxYearSpecificIfsUri[DownstreamResp](
        s"income-tax/calculation/${taxYear.asTysDownstream}/$nino?crystallise=$finalDeclaration"
      )

      post(JsObject.empty, downstreamUri)
    } else {
      val downstreamUri = DesUri[DownstreamResp](
        s"income-tax/nino/$nino/taxYear/${taxYear.asDownstream}/tax-calculation?crystallise=$finalDeclaration"
      )

      post(JsObject.empty, downstreamUri)
    }

  }

}
