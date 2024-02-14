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

package api.controllers.validators.resolvers

import api.models.errors.{MtdError, RuleIncorrectOrEmptyBodyError}
import cats.data.Validated
import play.api.libs.json.{JsValue, OFormat, Reads}
import utils.EmptinessChecker
import utils.EmptyPathsResult._

class ResolveNonEmptyJsonObject[T: OFormat: EmptinessChecker]()(implicit val reads: Reads[T]) extends ResolverSupport {

  private val jsonResolver = new ResolveJsonObject[T].resolver

  private val checkNonEmpty: Validator[T] = { data =>
    EmptinessChecker.findEmptyPaths(data) match {
      case CompletelyEmpty   => Some(List(RuleIncorrectOrEmptyBodyError))
      case EmptyPaths(paths) => Some(List(RuleIncorrectOrEmptyBodyError.withPaths(paths)))
      case NoEmptyPaths      => None
    }
  }

  val resolver: Resolver[JsValue, T] = jsonResolver thenValidate checkNonEmpty

  def apply(data: JsValue): Validated[Seq[MtdError], T] = resolver(data)

}
