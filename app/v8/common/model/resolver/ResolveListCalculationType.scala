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

package v8.common.model.resolver

import api.errors.FormatCalculationTypeError
import cats.data.Validated
import cats.data.Validated.{Invalid, Valid}
import shared.controllers.validators.resolvers.ResolverSupport
import shared.models.errors.MtdError
import v8.common.model.domain._

object ResolveListCalculationType extends ResolverSupport {

  def apply(value: Option[String]): Validated[Seq[MtdError], Option[CalculationType]] = value match {
    case None                       => Valid(None)
    case Some("in-year")            => Valid(Some(`in-year`))
    case Some("intent-to-finalise") => Valid(Some(`intent-to-finalise`))
    case Some("final-declaration")  => Valid(Some(`final-declaration`))
    case Some("intent-to-amend")    => Valid(Some(`intent-to-amend`))
    case Some("confirm-amendment")  => Valid(Some(`confirm-amendment`))
    case Some(_)                    => Invalid(Seq(FormatCalculationTypeError))
  }

}
