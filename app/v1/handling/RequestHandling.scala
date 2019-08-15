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

import play.api.libs.json.{ JsValue, Reads }
import play.api.mvc.Request
import v1.models.errors.MtdError

object RequestHandling {
  type Factory[Req, Resp] = (Request[_], Req) => RequestHandling[Req, Resp]
}

trait RequestHandling[Req, Resp] {

  def requestDefn: RequestDefn

  def passThroughErrors: List[MtdError]

  def customErrorMapping: PartialFunction[String, (Int, MtdError)]

  implicit val reads: Reads[Resp]
}

sealed trait RequestDefn

object RequestDefn {
  case class Get(uri: String, params: Seq[(String, String)]) extends RequestDefn
  case class Post(uri: String, body: JsValue)                extends RequestDefn
}
