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

package v3.models.errors

import play.api.http.Status._
import scala.collection.immutable.Seq

// MtdError types that are common across MTD APIs.

// Format Errors
object NinoFormatError          extends MtdError("FORMAT_NINO", "The provided NINO is invalid", BAD_REQUEST)
object TaxYearFormatError       extends MtdError("FORMAT_TAX_YEAR", "The provided tax year is invalid", BAD_REQUEST)
object ToDateFormatError        extends MtdError(code = "FORMAT_TO_DATE", message = "The toDate format is invalid", BAD_REQUEST)
object FromDateFormatError      extends MtdError(code = "FORMAT_FROM_DATE", message = "The fromDate format is invalid", BAD_REQUEST)
object BusinessIdFormatError    extends MtdError("FORMAT_BUSINESS_ID", "The Business ID format is invalid", BAD_REQUEST)
object PaymentIdFormatError     extends MtdError(code = "FORMAT_PAYMENT_ID", message = "The payment ID format is invalid", BAD_REQUEST)
object TransactionIdFormatError extends MtdError(code = "FORMAT_TRANSACTION_ID", message = "The transaction ID format is invalid", BAD_REQUEST)

object IdFormatError extends MtdError(code = "FORMAT_ID", message = "The ID format is invalid", BAD_REQUEST)

object ValueFormatError extends MtdError("FORMAT_VALUE", "The value must be between 0 and 99999999999.99", BAD_REQUEST) {

  def forPathAndRange(path: String, min: String, max: String): MtdError =
    ValueFormatError.copy(paths = Some(Seq(path)), message = s"The value must be between $min and $max")

}

//Standard Errors
object NotFoundError extends MtdError("MATCHING_RESOURCE_NOT_FOUND", "Matching resource not found", NOT_FOUND)

object InternalError extends MtdError("INTERNAL_SERVER_ERROR", "An internal server error occurred", INTERNAL_SERVER_ERROR)

object BadRequestError extends MtdError("INVALID_REQUEST", "Invalid request", BAD_REQUEST)

object BVRError extends MtdError("BUSINESS_ERROR", "Business validation error", BAD_REQUEST)

object ServiceUnavailableError extends MtdError("SERVICE_UNAVAILABLE", "Internal server error", INTERNAL_SERVER_ERROR)

object InvalidHttpMethodError extends MtdError("INVALID_HTTP_METHOD", "Invalid HTTP method", METHOD_NOT_ALLOWED)

object InvalidBodyTypeError extends MtdError("INVALID_BODY_TYPE", "Expecting text/json or application/json body", UNSUPPORTED_MEDIA_TYPE)

//Authentication/Authorisation errors
object ClientNotAuthenticatedError extends MtdError("CLIENT_OR_AGENT_NOT_AUTHORISED", "The client and/or agent is not authorised", UNAUTHORIZED)

/** Authentication OK but not allowed access to the requested resource
  */
object ClientNotAuthorisedError extends MtdError("CLIENT_OR_AGENT_NOT_AUTHORISED", "The client and/or agent is not authorised", FORBIDDEN)

object InvalidBearerTokenError extends MtdError("UNAUTHORIZED", "Bearer token is missing or not authorized", UNAUTHORIZED)

// Accept header Errors
object InvalidAcceptHeaderError extends MtdError("ACCEPT_HEADER_INVALID", "The accept header is missing or invalid", NOT_ACCEPTABLE)
object UnsupportedVersionError  extends MtdError("NOT_FOUND", "The requested resource could not be found", NOT_FOUND)

// Common rule errors
object RuleTaxYearNotSupportedError
    extends MtdError("RULE_TAX_YEAR_NOT_SUPPORTED", "The tax year specified does not lie within the supported range", BAD_REQUEST)


object RuleIncorrectOrEmptyBodyError
    extends MtdError("RULE_INCORRECT_OR_EMPTY_BODY_SUBMITTED", "An empty or non-matching body was submitted", BAD_REQUEST)

object RuleTaxYearRangeInvalidError
  extends MtdError("RULE_TAX_YEAR_RANGE_INVALID", "Tax year range invalid. A tax year range of one year is required", BAD_REQUEST)
object RuleTaxYearNotEndedError     extends MtdError("RULE_TAX_YEAR_NOT_ENDED", "Tax year not ended", BAD_REQUEST)
object RuleDuplicateIdError         extends MtdError(code = "RULE_DUPLICATE_ID_NOT_ALLOWED", message = "Duplicate IDs are not allowed", BAD_REQUEST)

object RuleFromDateNotSupportedError
    extends MtdError(code = "RULE_FROM_DATE_NOT_SUPPORTED", message = "The specified from date is too early", BAD_REQUEST)

object RuleMissingToDateError
    extends MtdError(code = "MISSING_TO_DATE", message = "The fromDate has been provided, but toDate is missing", BAD_REQUEST)

object MissingFromDateError
    extends MtdError(code = "MISSING_FROM_DATE", message = "The toDate has been provided, but fromDate is missing", BAD_REQUEST)

object RangeToDateBeforeFromDateError
    extends MtdError(code = "RANGE_TO_DATE_BEFORE_FROM_DATE", message = "The toDate cannot be earlier than the fromDate", BAD_REQUEST)

object RuleDateRangeInvalidError extends MtdError(code = "RULE_DATE_RANGE_INVALID", message = "The specified date range is invalid", BAD_REQUEST)
object RuleInvalidDateRangeError extends MtdError(code = "RULE_INVALID_DATE_RANGE", message = "The provided date range is invalid", BAD_REQUEST)

object RuleBusinessId
    extends MtdError("RULE_BUSINESS_ID", "A Business ID must be supplied for a self-employment or a foreign property business type.", BAD_REQUEST)

object NoTransactionDetailsFoundError extends MtdError(code = "NO_DETAILS_FOUND", message = "No transaction details found", BAD_REQUEST)

object RuleIncorrectGovTestScenarioError
    extends MtdError(
      code = "RULE_INCORRECT_GOV_TEST_SCENARIO",
      message = "The Gov-Test-Scenario was not found",
      BAD_REQUEST
    )

