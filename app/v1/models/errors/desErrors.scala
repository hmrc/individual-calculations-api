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

package v1.models.errors

import play.api.libs.json.{ Json, Reads }

case class BackendErrorCode(code: String) {
  def toMtd: MtdError = MtdError(code = code, message = "")
}

object BackendErrorCode {
  implicit val reads: Reads[BackendErrorCode] = Json.reads[BackendErrorCode]
}

sealed trait BackendError

case class BackendErrors(errors: List[BackendErrorCode]) extends BackendError

object BackendErrors {
  def single(error: BackendErrorCode): BackendErrors = BackendErrors(List(error))
}

case class OutboundError(error: MtdError, errors: Option[Seq[MtdError]] = None) extends BackendError
