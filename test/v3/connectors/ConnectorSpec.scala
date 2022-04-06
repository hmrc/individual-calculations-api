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

import play.api.http.{HeaderNames, MimeTypes, Status}
import support.UnitSpec
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.ExecutionContext

trait ConnectorSpec extends UnitSpec with Status with MimeTypes with HeaderNames {
  lazy val baseUrl = "http://test-BaseUrl"

  val otherHeaders: Seq[(String, String)] = Seq(
    "Gov-Test-Scenario" -> "DEFAULT",
    "AnotherHeader" -> "HeaderValue"
  )

  implicit val hc: HeaderCarrier = HeaderCarrier(otherHeaders = otherHeaders)
  implicit val ec: ExecutionContext = scala.concurrent.ExecutionContext.global
  implicit val correlationId: String = "a1e8057e-fbbc-47a8-a8b4-78d9f015c253"

  val dummyHeaderCarrierConfig: HeaderCarrier.Config =
    HeaderCarrier.Config(
      Seq("^not-test-BaseUrl?$".r),
      Seq.empty[String],
      Some("individual-calculations-api")
    )

  val requiredDownstreamHeaders: Seq[(String, String)] = Seq(
    "Authorization" -> "Bearer downstream-token",
    "Environment" -> "downstream-environment",
    "User-Agent" -> "individual-calculations-api",
    "CorrelationId" -> correlationId,
    "Gov-Test-Scenario" -> "DEFAULT"
  )

  val requiredBackendHeaders: Seq[(String, String)] = Seq(
    "Authorization" -> "Bearer user-token",
    "CorrelationId" -> correlationId
  )

  val allowedDownstreamHeaders: Seq[String] = Seq(
    "Accept",
    "Gov-Test-Scenario",
    "Content-Type",
    "Location",
    "X-Request-Timestamp",
    "X-Session-Id"
  )
}