/*
 * Copyright 2020 HM Revenue & Customs
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

package v1.models.errors

import play.api.libs.json.{Json, Writes}

case class MtdError(code: String, message: String)

object MtdError {
  implicit val writes: Writes[MtdError] = Json.writes[MtdError]
}

// Format Errors
object NinoFormatError extends MtdError(
  code = "FORMAT_NINO",
  message = "The provided NINO is invalid"
)

object TaxYearFormatError extends MtdError(
  code = "FORMAT_TAX_YEAR",
  message = "The provided tax year is invalid"
)

object CalculationIdFormatError extends MtdError(
  code = "FORMAT_CALC_ID",
  message = "The provided Calculation ID is invalid"
)

object TypeFormatError extends MtdError(
  code = "FORMAT_TYPE",
  message = "The provided type value is invalid"
)

// Specific Not Found Errors
object NoMessagesExistError extends MtdError(
  code = "NO_MESSAGES_PRESENT",
  message = "No messages present"
)

object EndOfYearEstimateNotPresentError extends MtdError(
  code = "END_OF_YEAR_ESTIMATE_NOT_PRESENT",
  message = "End of year estimate data will only be returned for an in-year calculation"
)

// Rule Errors
object RuleTaxYearNotSupportedError extends MtdError(
  code = "RULE_TAX_YEAR_NOT_SUPPORTED",
  message = "Tax year not supported, because it precedes the earliest allowable tax year"
)

object RuleIncorrectOrEmptyBodyError extends MtdError(
  code = "RULE_INCORRECT_OR_EMPTY_BODY_SUBMITTED",
  message = "An empty or non-matching body was submitted"
)

object RuleTaxYearRangeInvalidError
  extends MtdError(
    code = "RULE_TAX_YEAR_RANGE_INVALID",
    message = "Tax year range invalid. A tax year range of one year is required"
  )

object RuleNoIncomeSubmissionsExistError extends MtdError(
  code = "RULE_NO_INCOME_SUBMISSIONS_EXIST",
  message = "No income submissions exist for the tax year"
)

object RuleCalculationErrorMessagesExist extends MtdError(
  code = "RULE_CALCULATION_ERROR_MESSAGES_EXIST",
  message = "Calculation error messages exist for the supplied calculation ID"
)

object NoAllowancesDeductionsAndReliefsExist extends MtdError(
  code = "NO_ALLOWANCES_DEDUCTIONS_RELIEFS_EXIST",
  message = "No allowances, deductions and reliefs data exists"
)

// Standard Errors
object NotFoundError extends MtdError(
  code = "MATCHING_RESOURCE_NOT_FOUND",
  message = "Matching resource not found"
)

object DownstreamError extends MtdError(
  code = "INTERNAL_SERVER_ERROR",
  message = "An internal server error occurred"
)

object BadRequestError extends MtdError(
  code = "INVALID_REQUEST",
  message = "Invalid request"
)

object BVRError extends MtdError(
  code = "BUSINESS_ERROR",
  message = "Business validation error"
)

object ServiceUnavailableError extends MtdError(
  code = "SERVICE_UNAVAILABLE",
  message = "Internal server error"
)

object InvalidBodyTypeError extends MtdError(
  code = "INVALID_BODY_TYPE",
  message = "Expecting text/json or application/json body"
)

// Authorisation Errors
object UnauthorisedError extends MtdError(
  code = "CLIENT_OR_AGENT_NOT_AUTHORISED",
  message = "The client and/or agent is not authorised"
)

object InvalidBearerTokenError extends MtdError(
  code = "UNAUTHORIZED",
  message = "Bearer token is missing or not authorized"
)

// Accept header Errors
object InvalidAcceptHeaderError extends MtdError(
  code = "ACCEPT_HEADER_INVALID",
  message = "The accept header is missing or invalid"
)

object UnsupportedVersionError extends MtdError(
  code = "NOT_FOUND",
  message = "The requested resource could not be found"
)
