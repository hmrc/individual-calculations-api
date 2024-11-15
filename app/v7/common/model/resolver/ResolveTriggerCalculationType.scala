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

package v7.common.model.resolver

import api.errors.FormatCalculationTypeError
import cats.data.Validated
import cats.data.Validated.{Invalid, Valid}
import shared.controllers.validators.resolvers.ResolverSupport
import shared.models.errors.MtdError
import v7.common.model.domain._

object ResolveTriggerCalculationType extends ResolverSupport {

  def apply(value: String): Validated[Seq[MtdError], CalculationType] = value match {
    case "in-year" => Valid(`in-year`)
    case "intent-to-finalise" => Valid(`intent-to-finalise`)
    case "intent-to-amend" => Valid(`intent-to-amend`)
    case _                   => Invalid(Seq(FormatCalculationTypeError))
  }
}