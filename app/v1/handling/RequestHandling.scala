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
import v1.connectors.httpparsers.StandardHttpParser.SuccessCode
import v1.handling.RequestDefn.{ Get, Post }
import v1.handling.RequestHandling.{ ErrorMapping, SuccessMapping }
import v1.models.errors.{ ErrorWrapper, MtdError }
import v1.models.outcomes.ResponseWrapper

trait RequestHandling[BackendResp, APIResp] {

  def requestDefn: RequestDefn

  def passThroughErrors: Seq[MtdError]

  def customErrorMapping: ErrorMapping

  implicit val reads: Reads[BackendResp]

  implicit val successCode: SuccessCode

  def successMapping: SuccessMapping[BackendResp, APIResp]
}

object RequestHandling {
  type ErrorMapping                         = PartialFunction[String, (Int, MtdError)]
  type SuccessMapping[BackendResp, APIResp] = ResponseWrapper[BackendResp] => Either[ErrorWrapper, ResponseWrapper[APIResp]]

  def noMapping[Resp]: SuccessMapping[Resp, Resp] = (r: ResponseWrapper[Resp]) => Right(r)

  case class Impl[BackendResp, APIResp] protected[handling] (
      requestDefn: RequestDefn,
      successCode: SuccessCode,
      passThroughErrors: Seq[MtdError] = Seq.empty,
      customErrorMapping: ErrorMapping = PartialFunction.empty,
      successMapping: SuccessMapping[BackendResp, APIResp])(implicit val reads: Reads[BackendResp])
      extends RequestHandling[BackendResp, APIResp] {

    def withPassThroughErrors(errors: MtdError*): Impl[BackendResp, APIResp] =
      copy(passThroughErrors = errors)

    def withRequestSuccessCode(status: Int): Impl[BackendResp, APIResp] =
      copy(successCode = SuccessCode(status))

    def mapErrors(mappings: ErrorMapping): Impl[BackendResp, APIResp] =
      copy(customErrorMapping = mappings)

    def mapSuccess[APIResp2](mapping: SuccessMapping[BackendResp, APIResp2]): Impl[BackendResp, APIResp2] =
      copy(successMapping = mapping)
  }

  def apply[Resp: Reads](requestDefn: RequestDefn): Impl[Resp, Resp] = {
    val successCode: SuccessCode = requestDefn match {
      case _: Get  => SuccessCode(OK)
      case _: Post => SuccessCode(NO_CONTENT)
    }

    Impl(requestDefn, successCode, successMapping = noMapping)
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
