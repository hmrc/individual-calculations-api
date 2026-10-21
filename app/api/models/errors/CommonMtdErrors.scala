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

package api.models.errors

import play.api.http.Status.*

// MtdError types that are common across MTD APIs.

// Format Errors
object NinoFormatError extends MtdError("FORMAT_NINO", "The NINO format is invalid", BAD_REQUEST)

object TaxYearFormatError extends MtdError("FORMAT_TAX_YEAR", "The provided tax year format is invalid", BAD_REQUEST)

object CalculationIdFormatError extends MtdError("FORMAT_CALCULATION_ID", "The provided calculation ID is invalid", BAD_REQUEST)

object FormatCalculationTypeError extends MtdError("FORMAT_CALCULATION_TYPE", "The provided calculation type is not valid", BAD_REQUEST)

object FinalDeclarationFormatError extends MtdError("FORMAT_FINAL_DECLARATION", "The provided finalDeclaration is invalid", BAD_REQUEST)

//Standard Errors
object NotFoundError extends MtdError("MATCHING_RESOURCE_NOT_FOUND", "Matching resource not found", NOT_FOUND)

object NotFoundFinalDeclarationError
    extends MtdError(
      "MATCHING_RESOURCE_NOT_FOUND",
      "The calculation ID was not found or does not correspond to the most recent calculation",
      NOT_FOUND)

object InternalError extends MtdError("INTERNAL_SERVER_ERROR", "An internal server error occurred", INTERNAL_SERVER_ERROR)

object BadRequestError extends MtdError("INVALID_REQUEST", "Invalid request", BAD_REQUEST)

object BVRError extends MtdError("BUSINESS_ERROR", "Business validation error", BAD_REQUEST)

object GatewayTimeoutError extends MtdError("GATEWAY_TIMEOUT", "The request has timed out", GATEWAY_TIMEOUT)

object InvalidHttpMethodError extends MtdError("INVALID_HTTP_METHOD", "Invalid HTTP method", METHOD_NOT_ALLOWED)

object InvalidBodyTypeError extends MtdError("INVALID_BODY_TYPE", "Expecting text/json or application/json body", UNSUPPORTED_MEDIA_TYPE)

//Authentication/Authorisation errors

/** Authentication OK but not allowed access to the requested resource
  */
object ClientOrAgentNotAuthorisedError extends MtdError("CLIENT_OR_AGENT_NOT_AUTHORISED", "The client or agent is not authorised", FORBIDDEN) {
  def withStatus401: MtdError = copy(httpStatus = UNAUTHORIZED)
}

object InvalidBearerTokenError extends MtdError("UNAUTHORIZED", "Bearer token is missing or not authorized", UNAUTHORIZED)

// Accept header Errors
object InvalidAcceptHeaderError extends MtdError("ACCEPT_HEADER_INVALID", "The accept header is missing or invalid", NOT_ACCEPTABLE)

object UnsupportedVersionError extends MtdError("NOT_FOUND", "The requested resource could not be found", NOT_FOUND)

// Common rule errors
object RuleTaxYearNotSupportedError
    extends MtdError("RULE_TAX_YEAR_NOT_SUPPORTED", "The tax year specified does not lie within the supported range", BAD_REQUEST)

object RuleTaxYearRangeInvalidError extends MtdError("RULE_TAX_YEAR_RANGE_INVALID", "A tax year range of one year is required", BAD_REQUEST)

object RuleTaxYearNotEndedError extends MtdError("RULE_TAX_YEAR_NOT_ENDED", "The specified tax year has not yet ended", BAD_REQUEST)

object RuleFinalDeclarationReceivedError
    extends MtdError("RULE_FINAL_DECLARATION_RECEIVED", "Final declaration has already been received", BAD_REQUEST)

object RuleIncomeSourcesChangedError
    extends MtdError("RULE_INCOME_SOURCES_CHANGED", "Income sources data has changed. Trigger a new calculation", BAD_REQUEST)

object RulePrematureFinalisationError
    extends MtdError("RULE_PREMATURE_FINALISATION", "Finalisation cannot occur until after the end of the tax year", BAD_REQUEST)

object RuleDeclarationNotReceivedError extends MtdError("RULE_DECLARATION_NOT_RECEIVED", "The final declaration has not been received", BAD_REQUEST)

object RuleCalculationTypeNotAllowed
    extends MtdError("RULE_CALCULATION_TYPE_NOT_ALLOWED", "The provided calculation type is not applicable for this tax year", BAD_REQUEST)

object RuleIncomeSourcesInvalidError extends MtdError("RULE_INCOME_SOURCES_INVALID", "No valid income sources could be found", BAD_REQUEST) {
  def withStatus422: MtdError = copy(httpStatus = UNPROCESSABLE_ENTITY)
}

object RuleNoIncomeSubmissionsExistError
    extends MtdError("RULE_NO_INCOME_SUBMISSIONS_EXIST", "No income submissions exist for the tax year", BAD_REQUEST)

object RuleSubmissionFailedError
    extends MtdError("RULE_SUBMISSION_FAILED", "The submission cannot be completed due to validation failures", BAD_REQUEST)

object RuleRecentSubmissionsExistError
    extends MtdError("RULE_RECENT_SUBMISSIONS_EXIST", "More recent submissions exist. Trigger a new calculation", BAD_REQUEST)

object RuleResidencyChangedError extends MtdError("RULE_RESIDENCY_CHANGED", "Residency has changed. Trigger a new calculation", BAD_REQUEST)

object RuleCalculationInProgressError
    extends MtdError("RULE_CALCULATION_IN_PROGRESS", "A calculation is in progress. Please wait before triggering a new calculation", BAD_REQUEST)

object RuleBusinessValidationFailureError extends MtdError("RULE_BUSINESS_VALIDATION_FAILURE", "Business validation rule failures", BAD_REQUEST)

object RuleFinalDeclarationTaxYearError
    extends MtdError("RULE_FINAL_DECLARATION_TAX_YEAR", "Final declaration cannot be submitted until after the end of the tax year", BAD_REQUEST)

object RuleFinalDeclarationInProgressError
    extends MtdError("RULE_FINAL_DECLARATION_IN_PROGRESS", "There is a calculation in progress for the tax year", BAD_REQUEST)

object RuleOutsideAmendmentWindowError extends MtdError("RULE_OUTSIDE_AMENDMENT_WINDOW", "You are outside the amendment window", BAD_REQUEST)

//Stub Errors
object RuleIncorrectGovTestScenarioError
    extends MtdError("RULE_INCORRECT_GOV_TEST_SCENARIO", "The supplied Gov-Test-Scenario is not valid", BAD_REQUEST)
