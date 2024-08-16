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
import cats.data.Validated.{Invalid, Valid}
import com.neovisionaries.i18n.CountryCode
import shared.controllers.validators.resolvers.ResolveParsedCountryCode.permittedCustomCodes
import shared.models.errors.{CountryCodeFormatError, MtdError, RuleCountryCodeError}

case class ResolveParsedCountryCode(path: String) {

  def apply(value: String): Validated[List[MtdError], String] = {
    if (value.length != 3) {
      Invalid(List(CountryCodeFormatError.withPath(path)))
    } else if (permittedCustomCodes.contains(value)) {
      Valid(value)
    } else {
      Option(CountryCode.getByAlpha3Code(value)) match {
        case Some(_) => Valid(value)
        case None    => Invalid(List(RuleCountryCodeError.withPath(path)))
      }
    }
  }

  def apply(maybeValue: Option[String]): Validated[List[MtdError], Option[String]] = {
    maybeValue match {
      case Some(value) => apply(value).map(Option(_))
      case None        => Valid(None)
    }
  }

}

object ResolveParsedCountryCode {

  private val permittedCustomCodes = Set("ZZZ")

  def apply(value: String, path: String): Validated[Seq[MtdError], String] = {
    val resolver = ResolveParsedCountryCode(path)

    resolver(value)
  }

  def apply(maybeValue: Option[String], path: String): Validated[Seq[MtdError], Option[String]] = {
    val resolver = ResolveParsedCountryCode(path)
    resolver(maybeValue)
  }

}
