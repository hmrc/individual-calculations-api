/*
 * Copyright 2021 HM Revenue & Customs
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

package v1r2.connectors

import config.AppConfig
import javax.inject.{Inject, Singleton}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.http.HttpClient
import v1r2.models.domain.EmptyJsonBody
import v1r2.models.request.crystallisation.CrystallisationRequest
import v1r2.models.response.common.DesUnit

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CrystallisationConnector @Inject()(val http: HttpClient,
                                         val appConfig: AppConfig) extends BaseConnector {

  def declareCrystallisation(request: CrystallisationRequest)(
    implicit hc: HeaderCarrier,
    ec: ExecutionContext,
    correlationId: String): Future[BackendOutcome[DesUnit]] = {

    import v1r2.connectors.httpparsers.StandardHttpParser._
    import request._

    desPost(
      uri = Uri[DesUnit](s"income-tax/calculation/nino/$nino/$taxYear/$calculationId/crystallise"),
      body = EmptyJsonBody
    )
  }
}