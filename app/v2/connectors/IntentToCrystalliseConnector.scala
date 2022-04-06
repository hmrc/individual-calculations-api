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

package v2.connectors

import config.AppConfig
import javax.inject.{Inject, Singleton}
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient}
import v2.models.domain.EmptyJsonBody
import v2.models.request.intentToCrystallise.IntentToCrystalliseRequest
import v2.models.response.intentToCrystallise.IntentToCrystalliseResponse

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class IntentToCrystalliseConnector @Inject()(val http: HttpClient,
                                             val appConfig: AppConfig) extends BaseConnector {

  def submitIntentToCrystallise(request: IntentToCrystalliseRequest)(
    implicit hc: HeaderCarrier,
    ec: ExecutionContext,
    correlationId: String): Future[BackendOutcome[IntentToCrystalliseResponse]] = {

    import v2.connectors.httpparsers.StandardHttpParser._

    val nino: String = request.nino.nino
    val taxYear: String = request.taxYear.value

    downstreamPost(
      uri = Uri[IntentToCrystalliseResponse](s"income-tax/nino/$nino/taxYear/$taxYear/tax-calculation?crystallise=true"),
      body = EmptyJsonBody
    )
  }
}