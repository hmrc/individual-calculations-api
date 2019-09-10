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
import play.api.libs.json.JsValue
import v1.handling.RequestDefinition.{ErrorHandler, SuccessHandler}
import v1.models.errors.{ErrorWrapper, MtdError}
import v1.models.outcomes.ResponseWrapper

sealed trait RequestDefinition[BackendResp, APIResp] {
  val uri: String
  val expectedSuccessCode: Int
  val passThroughErrors: Seq[MtdError]
  val customErrorHandler: ErrorHandler
  val successHandler: SuccessHandler[BackendResp, APIResp]
}

object RequestDefinition {
  type ErrorHandler = PartialFunction[String, (Int, MtdError)]
  type SuccessHandler[BackendResp, A] = ResponseWrapper[BackendResp] => Either[ErrorWrapper, ResponseWrapper[A]]

  def noMapping[BackendResp, APIResp]: SuccessHandler[BackendResp, APIResp] = (r: ResponseWrapper[BackendResp]) =>
    Right(r.map(_.asInstanceOf[APIResp]))

  case class Get[BackendResp, APIResp](uri: String,
                                       params: Seq[(String, String)] = Nil,
                                       expectedSuccessCode: Int = OK,
                                       passThroughErrors: Seq[MtdError] = Seq.empty,
                                       customErrorHandler: ErrorHandler = PartialFunction.empty,
                                       successHandler: SuccessHandler[BackendResp, APIResp] = noMapping[BackendResp, APIResp]) extends RequestDefinition[BackendResp, APIResp] {

    def withOptionalParams(optionalParams: (String, Option[String])*): Get[BackendResp, APIResp] = {
      val params = optionalParams.collect { case (n, Some(v)) => n -> v }

      copy(params = params)
    }
  }

  case class Post[BackendResp, APIResp](uri: String,
                                        body: JsValue,
                                        expectedSuccessCode: Int = NO_CONTENT,
                                        passThroughErrors: Seq[MtdError] = Seq.empty,
                                        customErrorHandler: ErrorHandler = PartialFunction.empty,
                                        successHandler: SuccessHandler[BackendResp, APIResp] = noMapping[BackendResp, APIResp]) extends RequestDefinition[BackendResp, APIResp]

}