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
import play.api.Logger
import play.api.http.{HeaderNames, MimeTypes}
import play.api.libs.json.Writes
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, HttpReads}
import v3.connectors.DownstreamUri.{DesUri, IfsUri}

import scala.concurrent.{ExecutionContext, Future}

trait BaseDownstreamConnector {
  val http: HttpClient
  val appConfig: AppConfig

  val logger: Logger = Logger(this.getClass)

  private val jsonContentTypeHeader = HeaderNames.CONTENT_TYPE -> MimeTypes.JSON

  private def desHeaderCarrier(hc: HeaderCarrier, correlationId: String, additionalHeaders: (String, String)*): HeaderCarrier = {
    val passThroughHeaders = hc
      .headers(appConfig.desEnvironmentHeaders.getOrElse(Seq.empty))
      .filterNot(hdr => additionalHeaders.exists(_._1.equalsIgnoreCase(hdr._1)))

    HeaderCarrier(
      extraHeaders = hc.extraHeaders ++
        // Contract headers
        Seq(
          "Authorization" -> s"Bearer ${appConfig.desToken}",
          "Environment"   -> appConfig.desEnv,
          "CorrelationId" -> correlationId
        ) ++
        additionalHeaders ++
        passThroughHeaders
    )
  }

  private def ifsHeaderCarrier(hc: HeaderCarrier, correlationId: String, additionalHeaders: (String, String)*): HeaderCarrier = {
    val passThroughHeaders = hc
      .headers(appConfig.ifsEnvironmentHeaders.getOrElse(Seq.empty))
      .filterNot(hdr => additionalHeaders.exists(_._1.equalsIgnoreCase(hdr._1)))

    HeaderCarrier(
      extraHeaders = hc.extraHeaders ++
        // Contract headers
        Seq(
          "Authorization" -> s"Bearer ${appConfig.ifsToken}",
          "Environment"   -> appConfig.ifsEnv,
          "consumerId"    -> "MDTP",
          "CorrelationId" -> correlationId
        ) ++
        additionalHeaders ++
        passThroughHeaders
    )
  }

  def post[Body: Writes, Resp](body: Body, uri: DownstreamUri[Resp])(implicit
      ec: ExecutionContext,
      hc: HeaderCarrier,
      correlationId: String,
      httpReads: HttpReads[DownstreamOutcome[Resp]]): Future[DownstreamOutcome[Resp]] = {

    def doPost(implicit hc: HeaderCarrier): Future[DownstreamOutcome[Resp]] = {
      http.POST(getBackendUri(uri), body)
    }

    doPost(getBackendHeaders(uri, hc, correlationId, jsonContentTypeHeader))
  }

  def get[Resp](uri: DownstreamUri[Resp], queryParams: Seq[(String, String)] = Seq.empty)(implicit
      ec: ExecutionContext,
      hc: HeaderCarrier,
      correlationId: String,
      httpReads: HttpReads[DownstreamOutcome[Resp]]): Future[DownstreamOutcome[Resp]] = {

    def doGet(implicit hc: HeaderCarrier): Future[DownstreamOutcome[Resp]] =
      http.GET(getBackendUri(uri), queryParams = queryParams)

    doGet(getBackendHeaders(uri, hc, correlationId))
  }

  def delete[Resp](uri: DownstreamUri[Resp])(implicit
      ec: ExecutionContext,
      hc: HeaderCarrier,
      correlationId: String,
      httpReads: HttpReads[DownstreamOutcome[Resp]]): Future[DownstreamOutcome[Resp]] = {

    def doDelete(implicit hc: HeaderCarrier): Future[DownstreamOutcome[Resp]] = {
      http.DELETE(getBackendUri(uri))
    }

    doDelete(getBackendHeaders(uri, hc, correlationId))
  }

  def put[Body: Writes, Resp](body: Body, uri: DownstreamUri[Resp])(implicit
      ec: ExecutionContext,
      hc: HeaderCarrier,
      correlationId: String,
      httpReads: HttpReads[DownstreamOutcome[Resp]]): Future[DownstreamOutcome[Resp]] = {

    def doPut(implicit hc: HeaderCarrier): Future[DownstreamOutcome[Resp]] = {
      http.PUT(getBackendUri(uri), body)
    }

    doPut(getBackendHeaders(uri, hc, correlationId, jsonContentTypeHeader))
  }

  private def getBackendUri[Resp](uri: DownstreamUri[Resp]): String = uri match {
    case DesUri(value) => s"${appConfig.desBaseUrl}/$value"
    case IfsUri(value) => s"${appConfig.ifsBaseUrl}/$value"
  }

  private def getBackendHeaders[Resp](uri: DownstreamUri[Resp],
                                      hc: HeaderCarrier,
                                      correlationId: String,
                                      additionalHeaders: (String, String)*): HeaderCarrier =
    uri match {
      case DesUri(_) => desHeaderCarrier(hc, correlationId, additionalHeaders: _*)
      case IfsUri(_) => ifsHeaderCarrier(hc, correlationId, additionalHeaders: _*)
    }

}
