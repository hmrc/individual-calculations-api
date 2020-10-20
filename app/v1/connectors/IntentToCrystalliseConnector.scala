/*
 * Copyright 2020 HM Revenue & Customs
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

package v1.connectors

import config.AppConfig
import javax.inject.{Inject, Singleton}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.http.HttpClient
import v1.models.domain.EmptyJsonBody
import v1.models.request.intentToCrystallise.IntentToCrystalliseRequest
import v1.models.response.intentToCrystallise.IntentToCrystalliseResponse

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class IntentToCrystalliseConnector @Inject()(val http: HttpClient,
                                             val appConfig: AppConfig) extends BaseConnector {

  def submitIntentToCrystallise(request: IntentToCrystalliseRequest)(
    implicit hc: HeaderCarrier,
    ec: ExecutionContext): Future[BackendOutcome[IntentToCrystalliseResponse]] = {

    import v1.connectors.httpparsers.StandardHttpParser._

    val nino: String = request.nino.nino
    val taxYear: String = request.taxYear.value

    desPost(
      uri = Uri[IntentToCrystalliseResponse](s"income-tax/nino/$nino/taxYear/$taxYear/tax-calculation?crystallise=true"),
      body = EmptyJsonBody
    )
  }
}
