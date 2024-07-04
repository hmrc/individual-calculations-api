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

package shared.connectors

import play.api.http.{HeaderNames, MimeTypes}
import play.api.libs.json.Writes
import shared.config.{AppConfig, DownstreamConfig}
import shared.connectors.DownstreamUri.{DesUri, IfsUri, TaxYearSpecificIfsUri}
import shared.utils.Logging
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, HttpReads}

import scala.concurrent.{ExecutionContext, Future}

trait BaseDownstreamConnector extends Logging {
  val http: HttpClient
  val appConfig: AppConfig

  private val jsonContentTypeHeader = HeaderNames.CONTENT_TYPE -> MimeTypes.JSON

  def post[Body: Writes, Resp](body: Body, uri: DownstreamUri[Resp])(implicit
      ec: ExecutionContext,
      hc: HeaderCarrier,
      httpReads: HttpReads[DownstreamOutcome[Resp]],
      correlationId: String): Future[DownstreamOutcome[Resp]] = {

    def doPost(implicit hc: HeaderCarrier): Future[DownstreamOutcome[Resp]] = {
      http.POST(getBackendUri(uri), body)
    }

    doPost(getBackendHeaders(uri, jsonContentTypeHeader))
  }

  def get[Resp](uri: DownstreamUri[Resp])(implicit
      ec: ExecutionContext,
      hc: HeaderCarrier,
      httpReads: HttpReads[DownstreamOutcome[Resp]],
      correlationId: String): Future[DownstreamOutcome[Resp]] = {

    def doGet(implicit hc: HeaderCarrier): Future[DownstreamOutcome[Resp]] =
      http.GET(getBackendUri(uri))

    doGet(getBackendHeaders(uri))
  }

  def get[Resp](uri: DownstreamUri[Resp], queryParams: Seq[(String, String)])(implicit
      ec: ExecutionContext,
      hc: HeaderCarrier,
      httpReads: HttpReads[DownstreamOutcome[Resp]],
      correlationId: String): Future[DownstreamOutcome[Resp]] = {

    def doGet(implicit hc: HeaderCarrier): Future[DownstreamOutcome[Resp]] =
      http.GET(getBackendUri(uri), queryParams)

    doGet(getBackendHeaders(uri))
  }

  def delete[Resp](uri: DownstreamUri[Resp])(implicit
      ec: ExecutionContext,
      hc: HeaderCarrier,
      httpReads: HttpReads[DownstreamOutcome[Resp]],
      correlationId: String): Future[DownstreamOutcome[Resp]] = {

    def doDelete(implicit hc: HeaderCarrier): Future[DownstreamOutcome[Resp]] = {
      http.DELETE(getBackendUri(uri))
    }

    doDelete(getBackendHeaders(uri))
  }

  def put[Body: Writes, Resp](body: Body, uri: DownstreamUri[Resp], maybeIntent: Option[String] = None)(implicit
      ec: ExecutionContext,
      hc: HeaderCarrier,
      httpReads: HttpReads[DownstreamOutcome[Resp]],
      correlationId: String): Future[DownstreamOutcome[Resp]] = {

    def doPut(implicit hc: HeaderCarrier): Future[DownstreamOutcome[Resp]] = {
      http.PUT(getBackendUri(uri), body)
    }

    maybeIntent match {
      case Some(intent) => doPut(getBackendHeaders(uri, jsonContentTypeHeader, intentHeader(intent)))
      case None         => doPut(getBackendHeaders(uri, jsonContentTypeHeader))
    }
  }

  private def getBackendUri[Resp](uri: DownstreamUri[Resp]): String =
    s"${configFor(uri).baseUrl}/${uri.value}"

  private def getBackendHeaders[Resp](
      uri: DownstreamUri[Resp],
      additionalHeaders: (String, String)*
  )(implicit hc: HeaderCarrier, correlationId: String): HeaderCarrier = {

    val downstreamConfig = configFor(uri)

    HeaderCarrier(
      extraHeaders = hc.extraHeaders ++
        // Contract headers
        List(
          "Authorization" -> s"Bearer ${downstreamConfig.token}",
          "Environment"   -> downstreamConfig.env,
          "CorrelationId" -> correlationId
        ) ++
        additionalHeaders ++
        passThroughHeaders(downstreamConfig, additionalHeaders)
    )
  }

  /** Only allows certain headers to be passed through to downstream.
    * @param additionalHeaders
    *   contains headers that we're sending, so should be removed from client passthrough headers
    * @param hc
    *   contains the allowed headers
    * @return
    *   filtered allowed passThroughHeaders
    */
  private[connectors] def passThroughHeaders(
      downstreamConfig: DownstreamConfig,
      additionalHeaders: Seq[(String, String)]
  )(implicit hc: HeaderCarrier): Seq[(String, String)] = {
    hc
      .headers(downstreamConfig.environmentHeaders.getOrElse(Nil))
      .filterNot(hdr => additionalHeaders.exists(_._1.equalsIgnoreCase(hdr._1)))
  }

  private def configFor[Resp](uri: DownstreamUri[Resp]) =
    uri match {
      case DesUri(_)                => appConfig.desDownstreamConfig
      case IfsUri(_)                => appConfig.ifsDownstreamConfig
      case TaxYearSpecificIfsUri(_) => appConfig.tysIfsDownstreamConfig
    }

  private def intentHeader(maybeIntent: String): (String, String) = "intent" -> maybeIntent
}
