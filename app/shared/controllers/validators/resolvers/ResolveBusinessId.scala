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
import shared.models.domain.BusinessId
import shared.models.errors.{BusinessIdFormatError, MtdError}

object ResolveBusinessId extends ResolverSupport {

  private val businessIdRegex = "^X[A-Z0-9]{1}IS[0-9]{11}$".r

  val resolver: Resolver[String, BusinessId] =
    ResolveStringPattern(businessIdRegex, BusinessIdFormatError).resolver.map(BusinessId.apply)

  def apply(value: String): Validated[Seq[MtdError], BusinessId] = resolver(value)

}
