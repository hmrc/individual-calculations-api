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

package shared.controllers.validators.resolvers

import cats.data.Validated
import play.api.libs.json.{JsValue, Reads}
import shared.controllers.validators.resolvers.UnexpectedJsonFieldsValidator.SchemaStructureSource
import shared.models.errors.{MtdError, RuleIncorrectOrEmptyBodyError}
import shared.utils.EmptyPathsResult._
import shared.utils.{EmptinessChecker, Logging}

class ResolveNonEmptyJsonObject[A: Reads: EmptinessChecker] extends ResolverSupport {

  val resolver: Resolver[JsValue, A] = ResolveNonEmptyJsonObject.resolver

  def apply(data: JsValue): Validated[Seq[MtdError], A] = resolver(data)

}

object ResolveNonEmptyJsonObject extends ResolverSupport with Logging {

  private def nonEmptyValidator[A: EmptinessChecker]: Validator[A] = { data =>
    EmptinessChecker.findEmptyPaths(data) match {
      case CompletelyEmpty => Some(List(RuleIncorrectOrEmptyBodyError))
      case EmptyPaths(paths) =>
        logger.warn(s"Request body failed validation with errors - Empty object or array: $paths")
        Some(List(RuleIncorrectOrEmptyBodyError.withPaths(paths)))
      case NoEmptyPaths => None
    }
  }

  def resolver[A: Reads: EmptinessChecker]: Resolver[JsValue, A] = ResolveJsonObject.resolver.thenValidate(nonEmptyValidator)

  /** Gets a resolver that also validates for unexpected JSON fields
    */
  def strictResolver[A: Reads: EmptinessChecker: SchemaStructureSource]: Resolver[JsValue, A] =
    ResolveJsonObject.strictResolver.thenValidate(nonEmptyValidator)

}
