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

package v7.submitFinalDeclaration.model.request.domain

import api.errors.formatCalculationTypeError
import cats.data.Validated
import cats.data.Validated.{Invalid, Valid}
import shared.controllers.validators.resolvers.ResolverSupport
import shared.models.errors.MtdError


object ResolveCalculationType extends ResolverSupport {

  def apply(value: String): Validated[Seq[MtdError], CalculationType] = (value) match {
    case ("final-declaration") => Valid(`final-declaration`)
    case ("confirm-amendment") => Valid(`confirm-amendment`)
    case _ => Invalid(Seq(formatCalculationTypeError))
  }

}
