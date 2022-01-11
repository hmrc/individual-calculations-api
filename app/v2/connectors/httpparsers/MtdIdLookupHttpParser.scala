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

package v2.connectors.httpparsers

import play.api.http.Status.{FORBIDDEN, OK, UNAUTHORIZED}
import play.api.libs.json._
import uk.gov.hmrc.http.{HttpReads, HttpResponse}
import v2.connectors.MtdIdLookupOutcome
import v2.models.errors.{DownstreamError, InvalidBearerTokenError, NinoFormatError}

object MtdIdLookupHttpParser extends HttpParser {

  private val mtdIdJsonReads: Reads[String] = (__ \ "mtdbsa").read[String]

  implicit val mtdIdLookupHttpReads: HttpReads[MtdIdLookupOutcome] = new HttpReads[MtdIdLookupOutcome] {
    override def read(method: String, url: String, response: HttpResponse): MtdIdLookupOutcome = {
      response.status match {
        case OK => response.validateJson[String](mtdIdJsonReads) match {
          case Some(mtdId) => Right(mtdId)
          case None => Left(DownstreamError)
        }
        case FORBIDDEN => Left(NinoFormatError)
        case UNAUTHORIZED => Left(InvalidBearerTokenError)
        case _ => Left(DownstreamError)
      }
    }
  }
}