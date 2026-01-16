/*
 * Copyright 2026 HM Revenue & Customs
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

package v8.triggerCalculation

import play.api.http.Status
import play.api.libs.json.JsObject
import shared.config.AppConfig
import shared.connectors.DownstreamUri.IfsUri
import shared.connectors.httpparsers.StandardDownstreamHttpParser._
import shared.connectors.{BaseDownstreamConnector, DownstreamOutcome}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.http.client.HttpClientV2
import v8.common.model.domain.{Either24or25Downstream, Post26Downstream, Pre24Downstream, `intent-to-finalise`}
import v8.triggerCalculation.model.request.TriggerCalculationRequestData
import v8.triggerCalculation.model.response.TriggerCalculationResponse
import v8.triggerCalculation.schema.TriggerCalculationSchema.Def1.DownstreamResp

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class TriggerCalculationConnector @Inject() (val http: HttpClientV2, val appConfig: AppConfig) extends BaseDownstreamConnector {

  def triggerCalculation(request: TriggerCalculationRequestData)(implicit
      hc: HeaderCarrier,
      ec: ExecutionContext,
      correlationId: String): Future[DownstreamOutcome[TriggerCalculationResponse]] = {

    import request._

    val crystallisationFlag = request.calculationType == `intent-to-finalise`

    tysDownstream match {
      case Pre24Downstream =>
        implicit val successCode: SuccessCode = SuccessCode(Status.OK)
        val path = s"income-tax/nino/$nino/taxYear/${taxYear.asDownstream}/tax-calculation?crystallise=$crystallisationFlag"
        val downstreamUrl =
          IfsUri[DownstreamResp](path)
        post(JsObject.empty, downstreamUrl)
      case Either24or25Downstream =>
        implicit val successCode: SuccessCode = SuccessCode(Status.ACCEPTED)
        val downstreamUrl =
          IfsUri[DownstreamResp](s"income-tax/calculation/${taxYear.asTysDownstream}/$nino?crystallise=$crystallisationFlag")
        post(JsObject.empty, downstreamUrl)
      case Post26Downstream =>
        implicit val successCode: SuccessCode = SuccessCode(Status.ACCEPTED)
        val downstreamUrl =
          IfsUri[DownstreamResp](s"income-tax/${taxYear.asTysDownstream}/calculation/$nino/${calculationType.toDownstream}")
        post(JsObject.empty, downstreamUrl)
    }
  }

}
