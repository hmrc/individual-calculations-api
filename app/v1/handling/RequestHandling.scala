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
package v1.handling

import play.api.http.Status._
import play.api.libs.json.{ JsValue, Reads }
import play.api.mvc.Request
import v1.connectors.httpparsers.StandardHttpParser.SuccessCode
import v1.handling.RequestDefn.{ Get, Post }
import v1.models.errors.MtdError

trait RequestHandling[Resp] {

  def requestDefn: RequestDefn

  def passThroughErrors: Seq[MtdError]

  def customErrorMapping: PartialFunction[String, (Int, MtdError)]

  implicit val reads: Reads[Resp]

  implicit val successCode: SuccessCode
}

object RequestHandling {

  case class Impl[Resp] private (
      requestDefn: RequestDefn,
      successCode: SuccessCode,
      passThroughErrors: Seq[MtdError] = Seq.empty,
      customErrorMapping: PartialFunction[String, (Int, MtdError)] = PartialFunction.empty)(implicit val reads: Reads[Resp])
      extends RequestHandling[Resp] {

    def withPassThroughErrors(errors: MtdError*): Impl[Resp] =
      copy(passThroughErrors = errors)

    def withRequestSuccessCode(status: Int): Impl[Resp] =
      copy(successCode = SuccessCode(status))

    def withCustomErrorMappings(mappings: PartialFunction[String, (Int, MtdError)]): Impl[Resp] =
      copy(customErrorMapping = mappings)
  }

  type Factory[Req, Resp] = (Request[_], Req) => RequestHandling[Resp]

  def apply[Req: Reads](requestDefn: RequestDefn): Impl[Req] = {
    val successCode: SuccessCode = requestDefn match {
      case _: Get  => SuccessCode(OK)
      case _: Post => SuccessCode(NO_CONTENT)
    }

    Impl(requestDefn, successCode)
  }
}

sealed trait RequestDefn

object RequestDefn {
  case class Get(uri: String, params: Seq[(String, String)] = Nil) extends RequestDefn {

    def withParams(params: (String, String)*): Get =
      copy(params = params)

    def withOptionalParams(optionalParams: (String, Option[String])*): Get = {
      val params = optionalParams.collect { case (n, Some(v)) => n -> v }

      copy(params = params)
    }
  }

  case class Post(uri: String, body: JsValue) extends RequestDefn

}
