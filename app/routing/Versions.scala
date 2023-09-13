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

package routing

import play.api.http.HeaderNames.ACCEPT
import play.api.libs.json._
import play.api.mvc.RequestHeader

object Version {

  implicit object VersionWrites extends Writes[Version] {

    def writes(version: Version): JsValue = version match {
      case Version3 => Json.toJson(Version3.name)
      case Version4 => Json.toJson(Version4.name)
      case Version5 => Json.toJson(Version5.name)
    }

  }

  implicit object VersionReads extends Reads[Version] {

    override def reads(version: JsValue): JsResult[Version] =
      version.validate[String].flatMap {
        case Version3.name => JsSuccess(Version3)
        case Version4.name => JsSuccess(Version4)
        case Version5.name => JsSuccess(Version5)
        case _             => JsError("Unrecognised version")
      }

  }

  implicit val versionFormat: Format[Version] = Format(VersionReads, VersionWrites)
}

sealed trait Version {
  val name: String
  val configName: String
  val maybePrevious: Option[Version] = None
  override def toString: String      = name
}

case object Version3 extends Version {
  val name                                    = "3.0"
  val configName                              = "3"
}

case object Version4 extends Version {
  val name                                    = "4.0"
  val configName                              = "4"
  override val maybePrevious: Option[Version] = Some(Version3)
}

case object Version5 extends Version {
  val name                                    = "5.0"
  val configName                              = "5"
  override val maybePrevious: Option[Version] = Some(Version4)
}

object Versions {

  private val versionsByName: Map[String, Version] = Map(
    Version3.name -> Version3,
    Version4.name -> Version4,
    Version5.name -> Version5

  )

  private val versionRegex = """application/vnd.hmrc.(\d.\d)\+json""".r

  def getFromRequest(request: RequestHeader): Either[GetFromRequestError, Version] =
    for {
      str <- getFrom(request.headers.headers)
      ver <- getFrom(str)
    } yield ver

  private def getFrom(headers: Seq[(String, String)]): Either[GetFromRequestError, String] =
    headers.collectFirst { case (ACCEPT, versionRegex(ver)) => ver }.toRight(left = InvalidHeader)

  private def getFrom(name: String): Either[GetFromRequestError, Version] =
    versionsByName.get(name).toRight(left = VersionNotFound)

}

sealed trait GetFromRequestError
case object InvalidHeader   extends GetFromRequestError
case object VersionNotFound extends GetFromRequestError
