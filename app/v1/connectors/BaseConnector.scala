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

package v1.connectors

import config.AppConfig
import play.api.libs.json.Writes
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, HttpReads}
import utils.Logging
import v1.models.response.common.DesResponse

import scala.concurrent.{ExecutionContext, Future}

trait BaseConnector extends Logging {
  val http: HttpClient
  val appConfig: AppConfig

  private[connectors] def headerCarrier(implicit hc: HeaderCarrier, correlationId: String): HeaderCarrier = hc
    .withExtraHeaders(headers = "CorrelationId" -> correlationId)

  private def desHeaderCarrier(additionalHeaders: Seq[String] = Seq("Content-Type"))(implicit hc: HeaderCarrier,
                                                                                     correlationId: String): HeaderCarrier =
    HeaderCarrier(
      extraHeaders = hc.extraHeaders ++
        // Contract headers
        Seq(
          "Authorization" -> s"Bearer ${appConfig.desToken}",
          "Environment" -> appConfig.desEnv,
          "CorrelationId" -> correlationId
        ) ++
        // Other headers (i.e Gov-Test-Scenario, Content-Type)
        hc.headers(additionalHeaders ++ appConfig.desEnvironmentHeaders.getOrElse(Seq.empty))
    )

  private def urlFrom(uri: String): String =
    if (uri.startsWith("/")) s"${appConfig.backendBaseUrl}$uri" else s"${appConfig.backendBaseUrl}/$uri"

  def post[Body: Writes, T](body: Body, uri: String)(implicit ec: ExecutionContext,
                                                     hc: HeaderCarrier,
                                                     httpReads: HttpReads[BackendOutcome[T]],
                                                     correlationId: String): Future[BackendOutcome[T]] = {

    def doPost(implicit hc: HeaderCarrier): Future[BackendOutcome[T]] = {
      http.POST(urlFrom(uri), body)
    }

    doPost(headerCarrier(hc, correlationId))
  }

  def desPost[Body: Writes, Resp <: DesResponse](body: Body, uri: Uri[Resp])(implicit ec: ExecutionContext,
                                                                             hc: HeaderCarrier,
                                                                             httpReads: HttpReads[BackendOutcome[Resp]],
                                                                             correlationId: String): Future[BackendOutcome[Resp]] = {

    def doPost(implicit hc: HeaderCarrier): Future[BackendOutcome[Resp]] = {
      http.POST(s"${appConfig.desBaseUrl}/${uri.value}", body)
    }

    doPost(desHeaderCarrier())
  }

  def get[T](uri: String, queryParameters: Seq[(String, String)] = Nil)(implicit ec: ExecutionContext,
                                                                        hc: HeaderCarrier,
                                                                        httpReads: HttpReads[BackendOutcome[T]],
                                                                        correlationId: String): Future[BackendOutcome[T]] = {

    def doGet(implicit hc: HeaderCarrier): Future[BackendOutcome[T]] =
      http.GET(urlFrom(uri), queryParameters)

    doGet(headerCarrier(hc, correlationId))
  }
}