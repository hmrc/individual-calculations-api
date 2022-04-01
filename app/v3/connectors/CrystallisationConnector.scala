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
import javax.inject.{Inject, Singleton}
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient}
import v3.models.domain.EmptyJsonBody
import v3.models.request.crystallisation.CrystallisationRequest
import v3.models.response.common.DesUnit

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CrystallisationConnector @Inject()(val http: HttpClient,
                                         val appConfig: AppConfig) extends BaseConnector {

  def declareCrystallisation(request: CrystallisationRequest)(
    implicit hc: HeaderCarrier,
    ec: ExecutionContext,
    correlationId: String): Future[BackendOutcome[DesUnit]] = {

    import v3.connectors.httpparsers.StandardHttpParser._

    val nino: String = request.nino.nino
    val taxYear: String = request.taxYear.value
    val calculationId: String = request.calculationId

    desPost(
      uri = Uri[DesUnit](s"income-tax/calculation/nino/$nino/$taxYear/$calculationId/crystallise"),
      body = EmptyJsonBody
    )
  }
}