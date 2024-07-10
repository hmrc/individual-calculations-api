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
import shared.models.errors.{MtdError, ValueFormatError}

case class ResolveInteger(min: Int, max: Int) extends ResolverSupport {

  def resolver(error: => MtdError): Resolver[Int, Int] =
    resolveValid[Int] thenValidate validator(error)

  def validator(error: => MtdError): Validator[Int] = { value: Int =>
    val valid = min <= value && value <= max

    Option.when(!valid)(List(error))
  }

  def apply(value: Option[Int], path: String): Validated[Seq[MtdError], Option[Int]] =
    resolver(errorFor(path)).resolveOptionally(value)

  def apply(value: Int, path: String): Validated[Seq[MtdError], Int] =
    resolver(errorFor(path))(value)

  private def errorFor(path: String) = ValueFormatError.forPathAndRange(path, min.toString, max.toString)

}
