/*
 * Copyright 2019 HM Revenue & Customs
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

import play.api.libs.json.{JsValue, Json, Writes}

case class MtdErrors(statusCode: Int, error: MtdError, errors: Option[Seq[MtdError]] = None)

object MtdErrors {
  implicit val writes: Writes[MtdErrors] = new Writes[MtdErrors] {
    override def writes(errorResponse: MtdErrors): JsValue = {

      val json = Json.obj(
        "code"    -> errorResponse.error.code,
        "message" -> errorResponse.error.message
      )

      errorResponse.errors match {
        case Some(errors) if errors.nonEmpty => json + ("errors" -> Json.toJson(errors))
        case _                               => json
      }

    }
  }

}

case class MtdError(code: String, message: String)

object MtdError {
  implicit val writes: Writes[MtdError] = Json.writes[MtdError]
}

object NinoFormatError    extends MtdError("FORMAT_NINO", "The provided NINO is invalid")
object TaxYearFormatError extends MtdError("FORMAT_TAX_YEAR", "The provided tax year is invalid")
object CalculationIdFormatError extends MtdError("FORMAT_CALC_ID", "The provided Calculation ID is invalid")
object TypeFormatError extends MtdError("FORMAT_TYPE", "The provided type value is invalid")

object NoMessagesExistError extends MtdError("NO_MESSAGES_PRESENT", "No messages present")

// Rule Errors
object RuleTaxYearNotSupportedError
    extends MtdError("RULE_TAX_YEAR_NOT_SUPPORTED", "Tax year not supported, because it precedes the earliest allowable tax year")

object RuleIncorrectOrEmptyBodyError extends MtdError("RULE_INCORRECT_OR_EMPTY_BODY_SUBMITTED", "An empty or non-matching body was submitted")

object RuleTaxYearRangeExceededError
    extends MtdError("RULE_TAX_YEAR_RANGE_EXCEEDED", "Tax year range exceeded. A tax year range of one year is required")

object RuleNoIncomeSubmissionsExistError extends MtdError("RULE_NO_INCOME_SUBMISSIONS_EXIST", "No income submissions exist for the tax year")

object RuleCalculationErrorMessagesExist extends MtdError("RULE_CALCULATION_ERROR_MESSAGES_EXIST", "Calculation error messages exist for the supplied calculation ID")

//Standard Errors
object NotFoundError extends MtdError("MATCHING_RESOURCE_NOT_FOUND", "Matching resource not found")

object DownstreamError extends MtdError("INTERNAL_SERVER_ERROR", "An internal server error occurred")

object BadRequestError extends MtdError("INVALID_REQUEST", "Invalid request")

object BVRError extends MtdError("BUSINESS_ERROR", "Business validation error")

object ServiceUnavailableError extends MtdError("SERVICE_UNAVAILABLE", "Internal server error")

object InvalidBodyTypeError extends MtdError("INVALID_BODY_TYPE", "Expecting text/json or application/json body")

//Authorisation Errors
object UnauthorisedError       extends MtdError("CLIENT_OR_AGENT_NOT_AUTHORISED", "The client and/or agent is not authorised")
object InvalidBearerTokenError extends MtdError("UNAUTHORIZED", "Bearer token is missing or not authorized")

// Accept header Errors
object InvalidAcceptHeaderError extends MtdError("ACCEPT_HEADER_INVALID", "The accept header is missing or invalid")

object UnsupportedVersionError extends MtdError("NOT_FOUND", "The requested resource could not be found")
