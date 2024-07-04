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

package shared.controllers.validators

import cats.data.Validated
import cats.data.Validated.Valid
import cats.implicits._
import shared.models.errors.MtdError

/** For complex additional validating that needs to take place after the initial validation and parsing of the JSON payload.
  *
  * If the additional validating is fairly minor, it could just go into a method in the Validator/ValidatorFactory; but if it's sizeable and is
  * primarily about validating business rules, then it'll make sense to separate it into a separate RulesValidator class.
  */
trait RulesValidator[PARSED] {

  /** A successful validation result with no errors. */
  protected val valid: Validated[Seq[MtdError], Unit] = Valid(())

  /** Validates the business rules for the given parsed data.
    *
    * @param parsed
    *   The parsed data to validate.
    * @return
    *   A validation result containing either the parsed data or a sequence of errors.
    */
  def validateBusinessRules(parsed: PARSED): Validated[Seq[MtdError], PARSED]

  /** Combines multiple validation results into a single result.
    *
    * @param results
    *   The validation results to combine.
    * @return
    *   A combined validation result.
    */
  protected def combine(results: Validated[Seq[MtdError], _]*): Validated[Seq[MtdError], Unit] =
    results.traverse_(identity)

  /** Provides utility operations for working with validation results. */
  implicit protected class ResultOps(result: Validated[Seq[MtdError], _]) {

    /** Converts the validation result to a result containing the given parsed data.
      *
      * @param parsed
      *   The parsed data to include in the result.
      * @return
      *   A validation result containing either the parsed data or a sequence of errors.
      */
    def onSuccess(parsed: PARSED): Validated[Seq[MtdError], PARSED] = result.map(_ => parsed)
    def toUnit: Validated[Seq[MtdError], Unit]                      = result.andThen(_ => valid)
  }

}
