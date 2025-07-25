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
import play.api.libs.json.JsValue
import shared.models.errors.MtdError

class ResolveExclusiveJsonProperty(error: => MtdError, fieldNames: String*) extends ResolverSupport {

  val validator: Validator[JsValue] =
    satisfies(error)(body => numExclusiveFieldsPresentIn(body) <= 1)

  private def numExclusiveFieldsPresentIn(body: JsValue) =
    fieldNames.count(field => (body \ field).isDefined)

  def resolver: Resolver[JsValue, JsValue] = resolveValid[JsValue].thenValidate(validator)

  def apply(body: JsValue): Validated[Seq[MtdError], JsValue] = resolver(body)

}
