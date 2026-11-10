/*
 * Copyright 2026 HM Revenue & Customs
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

package v9.createAmendProvisionalFiguresInformation

import api.controllers.validators.RulesValidator
import api.controllers.validators.resolvers.ResolveStringPattern
import api.models.errors.{
  ExpectedDateFormatError,
  MtdError,
  RuleExpectedDateNotInFutureError,
  RuleProvisionalFiguresIndicatorInvalidError,
  SupplementaryInformationFormatError
}
import cats.data.Validated
import cats.data.Validated.Invalid
import v9.createAmendProvisionalFiguresInformation.model.request.CreateAmendProvisionalFiguresInformationRequestData

import java.time.format.DateTimeParseException
import java.time.{Clock, LocalDate}

class CreateAmendProvisionalFiguresInformationRulesValidator(implicit clock: Clock = Clock.systemUTC)
    extends RulesValidator[CreateAmendProvisionalFiguresInformationRequestData] {

  private val expectedDatePath                    = "/expectedDate"
  private val supplementaryInformationPath        = "/supplementaryInformation"
  private val provisionalFiguresIndicatorPath     = "/provisionalFiguresIndicator"
  private val expectedDateFormat                  = """[0-9]{4}-[0-9]{2}-[0-9]{2}""".r
  private val supplementaryInformationRegexFormat = """[A-Za-z0-9 &'"()*,./@£-]{1,4000}""".r

  override def validateBusinessRules(
      parsed: CreateAmendProvisionalFiguresInformationRequestData
  ): Validated[Seq[MtdError], CreateAmendProvisionalFiguresInformationRequestData] = {
    val body = parsed.body

    combine(
      validateProvisionalFiguresIndicator(body.provisionalFiguresIndicator),
      validateExpectedDate(body.expectedDate),
      validateSupplementaryInformation(body.supplementaryInformation)
    ).onSuccess(parsed)
  }

  private def validateProvisionalFiguresIndicator(indicator: Boolean): Validated[Seq[MtdError], Unit] =
    if (indicator) valid
    else Invalid(Seq(RuleProvisionalFiguresIndicatorInvalidError.withPath(provisionalFiguresIndicatorPath)))

  private def validateExpectedDate(expectedDate: Option[String]): Validated[Seq[MtdError], Unit] =
    expectedDate match {
      case None => valid
      case Some(value) =>
        parseExpectedDate(value) match {
          case None => Invalid(Seq(ExpectedDateFormatError.withPath(expectedDatePath)))
          case Some(date) if !date.isAfter(LocalDate.now(clock)) =>
            Invalid(Seq(RuleExpectedDateNotInFutureError.withPath(expectedDatePath)))
          case Some(_) => valid
        }
    }

  private def parseExpectedDate(value: String): Option[LocalDate] =
    if (!expectedDateFormat.matches(value)) {
      None
    } else {
      try Some(LocalDate.parse(value))
      catch {
        case _: DateTimeParseException => None
      }
    }

  private def validateSupplementaryInformation(supplementaryInformation: Option[String]): Validated[Seq[MtdError], Unit] =
    ResolveStringPattern(
      supplementaryInformation,
      supplementaryInformationRegexFormat,
      SupplementaryInformationFormatError.withPath(supplementaryInformationPath)).map(_ => ())

}
