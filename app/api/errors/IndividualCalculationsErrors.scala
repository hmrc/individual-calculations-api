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

package api.errors

import play.api.http.Status.BAD_REQUEST
import shared.models.errors.MtdError

object RuleFinalDeclarationReceivedError
    extends MtdError(
      code = "RULE_FINAL_DECLARATION_RECEIVED",
      message = "Final declaration has already been received",
      BAD_REQUEST
    )

object RuleIncomeSourcesChangedError
    extends MtdError(
      code = "RULE_INCOME_SOURCES_CHANGED",
      message = "Income sources data has changed. Trigger a new calculation",
      BAD_REQUEST
    )

object RuleOutsideAmendmentWindowError
    extends MtdError(
      code = "RULE_OUTSIDE_AMENDMENT_WINDOW",
      message = "The request cannot be completed as you are outside the amendment window",
      BAD_REQUEST
    )

object formatCalculationTypeError
    extends MtdError(
      code = "FORMAT_CALCULATION_TYPE",
      message = "The provided calculation type is not valid.",
      BAD_REQUEST
    )

object RuleIncomeSourcesInvalidError
    extends MtdError(
      code = "RULE_INCOME_SOURCES_INVALID",
      message = "No valid income sources could be found",
      BAD_REQUEST
    )

object RuleNoIncomeSubmissionsExistError
    extends MtdError(
      code = "RULE_NO_INCOME_SUBMISSIONS_EXIST",
      message = "No income submissions exist for the tax year",
      BAD_REQUEST
    )

object RuleSubmissionFailedError
    extends MtdError(
      code = "RULE_SUBMISSION_FAILED",
      message = "The submission cannot be completed due to validation failures",
      BAD_REQUEST
    )

object RuleRecentSubmissionsExistError
    extends MtdError(
      code = "RULE_RECENT_SUBMISSIONS_EXIST",
      message = "More recent submissions exist. Trigger a new calculation",
      BAD_REQUEST
    )

object RuleResidencyChangedError
    extends MtdError(
      code = "RULE_RESIDENCY_CHANGED",
      message = "Residency has changed. Trigger a new calculation",
      BAD_REQUEST
    )

object FinalDeclarationFormatError
    extends MtdError(
      code = "FORMAT_FINAL_DECLARATION",
      message = "The provided finalDeclaration is invalid",
      BAD_REQUEST
    )

object RuleCalculationInProgressError
    extends MtdError(
      code = "RULE_CALCULATION_IN_PROGRESS",
      message = "A calculation is in progress. Please wait before triggering a new calculation",
      BAD_REQUEST
    )

object RuleBusinessValidationFailureError
    extends MtdError(
      code = "RULE_BUSINESS_VALIDATION_FAILURE",
      message = "Business validation rule failures",
      BAD_REQUEST
    )

object RuleFinalDeclarationTaxYearError
    extends MtdError(
      code = "RULE_FINAL_DECLARATION_TAX_YEAR",
      message = "Final declaration cannot be submitted until after the end of the tax year",
      BAD_REQUEST
    )

object RuleFinalDeclarationInProgressError
    extends MtdError(
      code = "RULE_FINAL_DECLARATION_IN_PROGRESS",
      message = "There is a calculation in progress for the tax year",
      BAD_REQUEST
    )
