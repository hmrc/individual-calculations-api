/*
 * Copyright 2019 HM Revenue & Customs
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
import javax.inject.Inject
import play.api.libs.json.Reads
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.http.HttpClient
import v1.connectors.httpparsers.StandardHttpParser._
import v1.handling.RequestDefn
import v1.handling.RequestDefn.{ Get, Post }

import scala.concurrent.{ ExecutionContext, Future }

class IndividualCalculationsConnector @Inject()(val appConfig: AppConfig, val http: HttpClient) extends BaseConnector {

  def doRequest[Resp: Reads](request: RequestDefn)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[BackendOutcome[Resp]] = {
    request match {
      case Get(uri, params) => get(uri, params)
      case Post(uri, body)  => post(body, uri)
    }
  }
}
