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
import play.api.Logger
import play.api.libs.json.Writes
import uk.gov.hmrc.http.logging.Authorization
import uk.gov.hmrc.http.{HeaderCarrier, HttpReads}
import uk.gov.hmrc.play.bootstrap.http.HttpClient

import scala.concurrent.{ExecutionContext, Future}

trait BaseConnector {
  val http: HttpClient
  val appConfig: AppConfig

  val logger = Logger(this.getClass)

  private[connectors] def headerCarrier(implicit hc: HeaderCarrier): HeaderCarrier =
    hc.copy(authorization = Some(Authorization(s"Bearer ${appConfig.backendToken}")))

  def post[Body: Writes, T](body: Body, uri: String)(implicit ec: ExecutionContext,
                                                           hc: HeaderCarrier,
                                                           httpReads: HttpReads[BackendOutcome[T]]): Future[BackendOutcome[T]] = {

    def doPost(implicit hc: HeaderCarrier): Future[BackendOutcome[T]] = {
      http.POST(s"${appConfig.backendBaseUrl}/$uri", body)
    }

    doPost(headerCarrier(hc))
  }

  def get[T](uri: String)(implicit ec: ExecutionContext,
                                hc: HeaderCarrier,
                                httpReads: HttpReads[BackendOutcome[T]]): Future[BackendOutcome[T]] = {

    def doGet(implicit hc: HeaderCarrier): Future[BackendOutcome[T]] =
      http.GET(s"${appConfig.backendBaseUrl}/$uri")

    doGet(headerCarrier(hc))
  }

  def get[T](uri: String, queryParameters: Seq[(String, String)])(implicit ec: ExecutionContext,
                                hc: HeaderCarrier,
                                httpReads: HttpReads[BackendOutcome[T]]): Future[BackendOutcome[T]] = {

    def doGet(implicit hc: HeaderCarrier): Future[BackendOutcome[T]] =
      http.GET(s"${appConfig.backendBaseUrl}/$uri", queryParameters)

    doGet(headerCarrier(hc))
  }

}
