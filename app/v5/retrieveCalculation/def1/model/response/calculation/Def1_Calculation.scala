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

package v5.retrieveCalculation.def1.model.response.calculation

import play.api.libs.json._
import v5.retrieveCalculation.def1.model.response.calculation.allowancesAndDeductions.Def1_AllowancesAndDeductions
import v5.retrieveCalculation.def1.model.response.calculation.businessProfitAndLoss.Def1_BusinessProfitAndLoss
import v5.retrieveCalculation.def1.model.response.calculation.chargeableEventGainsIncome.Def1_ChargeableEventGainsIncome
import v5.retrieveCalculation.def1.model.response.calculation.codedOutUnderpayments.Def1_CodedOutUnderpayments
import v5.retrieveCalculation.def1.model.response.calculation.dividendsIncome.Def1_DividendsIncome
import v5.retrieveCalculation.def1.model.response.calculation.employmentAndPensionsIncome.Def1_EmploymentAndPensionsIncome
import v5.retrieveCalculation.def1.model.response.calculation.employmentExpenses.Def1_EmploymentExpenses
import v5.retrieveCalculation.def1.model.response.calculation.endOfYearEstimate.Def1_EndOfYearEstimate
import v5.retrieveCalculation.def1.model.response.calculation.foreignIncome.Def1_ForeignIncome
import v5.retrieveCalculation.def1.model.response.calculation.foreignPropertyIncome.Def1_ForeignPropertyIncome
import v5.retrieveCalculation.def1.model.response.calculation.foreignTaxForFtcrNotClaimed.Def1_ForeignTaxForFtcrNotClaimed
import v5.retrieveCalculation.def1.model.response.calculation.giftAid.Def1_GiftAid
import v5.retrieveCalculation.def1.model.response.calculation.incomeSummaryTotals.Def1_IncomeSummaryTotals
import v5.retrieveCalculation.def1.model.response.calculation.lossesAndClaims.Def1_LossesAndClaims
import v5.retrieveCalculation.def1.model.response.calculation.marriageAllowanceTransferredIn.Def1_MarriageAllowanceTransferredIn
import v5.retrieveCalculation.def1.model.response.calculation.notionalTax.Def1_NotionalTax
import v5.retrieveCalculation.def1.model.response.calculation.otherIncome.Def1_OtherIncome
import v5.retrieveCalculation.def1.model.response.calculation.pensionContributionReliefs.Def1_PensionContributionReliefs
import v5.retrieveCalculation.def1.model.response.calculation.pensionSavingsTaxCharges.Def1_PensionSavingsTaxCharges
import v5.retrieveCalculation.def1.model.response.calculation.previousCalculation.Def1_PreviousCalculation
import v5.retrieveCalculation.def1.model.response.calculation.reliefs.Def1_Reliefs
import v5.retrieveCalculation.def1.model.response.calculation.royaltyPayments.Def1_RoyaltyPayments
import v5.retrieveCalculation.def1.model.response.calculation.savingsAndGainsIncome.Def1_SavingsAndGainsIncome
import v5.retrieveCalculation.def1.model.response.calculation.seafarersDeductions.Def1_SeafarersDeductions
import v5.retrieveCalculation.def1.model.response.calculation.shareSchemesIncome.Def1_ShareSchemesIncome
import v5.retrieveCalculation.def1.model.response.calculation.stateBenefitsIncome.Def1_StateBenefitsIncome
import v5.retrieveCalculation.def1.model.response.calculation.studentLoans.Def1_StudentLoans
import v5.retrieveCalculation.def1.model.response.calculation.taxCalculation.Def1_TaxCalculation
import v5.retrieveCalculation.def1.model.response.calculation.taxDeductedAtSource.Def1_TaxDeductedAtSource

case class Def1_Calculation(
    allowancesAndDeductions: Option[Def1_AllowancesAndDeductions],
    reliefs: Option[Def1_Reliefs],
    taxDeductedAtSource: Option[Def1_TaxDeductedAtSource],
    giftAid: Option[Def1_GiftAid],
    royaltyPayments: Option[Def1_RoyaltyPayments],
    notionalTax: Option[Def1_NotionalTax],
    marriageAllowanceTransferredIn: Option[Def1_MarriageAllowanceTransferredIn],
    pensionContributionReliefs: Option[Def1_PensionContributionReliefs],
    pensionSavingsTaxCharges: Option[Def1_PensionSavingsTaxCharges],
    studentLoans: Option[Seq[Def1_StudentLoans]],
    codedOutUnderpayments: Option[Def1_CodedOutUnderpayments],
    foreignPropertyIncome: Option[Seq[Def1_ForeignPropertyIncome]],
    businessProfitAndLoss: Option[Seq[Def1_BusinessProfitAndLoss]],
    employmentAndPensionsIncome: Option[Def1_EmploymentAndPensionsIncome],
    employmentExpenses: Option[Def1_EmploymentExpenses],
    seafarersDeductions: Option[Def1_SeafarersDeductions],
    foreignTaxForFtcrNotClaimed: Option[Def1_ForeignTaxForFtcrNotClaimed],
    stateBenefitsIncome: Option[Def1_StateBenefitsIncome],
    shareSchemesIncome: Option[Def1_ShareSchemesIncome],
    foreignIncome: Option[Def1_ForeignIncome],
    chargeableEventGainsIncome: Option[Def1_ChargeableEventGainsIncome],
    savingsAndGainsIncome: Option[Def1_SavingsAndGainsIncome],
    otherIncome: Option[Def1_OtherIncome],
    dividendsIncome: Option[Def1_DividendsIncome],
    incomeSummaryTotals: Option[Def1_IncomeSummaryTotals],
    taxCalculation: Option[Def1_TaxCalculation],
    previousCalculation: Option[Def1_PreviousCalculation],
    endOfYearEstimate: Option[Def1_EndOfYearEstimate],
    lossesAndClaims: Option[Def1_LossesAndClaims]
) {

  def withoutBasicExtension: Def1_Calculation = copy(reliefs = reliefs.map(_.withoutBasicExtension).filter(_.isDefined))

  def withoutGiftAidTaxReductionWhereBasicRateDiffers: Def1_Calculation =
    copy(reliefs = reliefs.map(_.withoutGiftAidTaxReductionWhereBasicRateDiffers).filter(_.isDefined))

  def withoutGiftAidTaxChargeWhereBasicRateDiffers: Def1_Calculation =
    copy(taxCalculation = taxCalculation.map(_.withoutGiftAidTaxChargeWhereBasicRateDiffers))

  def withoutUnderLowerProfitThreshold: Def1_Calculation =
    copy(taxCalculation = taxCalculation.map(_.withoutUnderLowerProfitThreshold))

  def withoutOffPayrollWorker: Def1_Calculation =
    copy(employmentAndPensionsIncome = employmentAndPensionsIncome.map(_.withoutOffPayrollWorker).filter(_.isDefined))

  def withoutTotalAllowanceAndDeductions: Def1_Calculation =
    copy(endOfYearEstimate = endOfYearEstimate.map(_.withoutTotalAllowanceAndDeductions).filter(_.isDefined))

  def withoutTaxTakenOffTradingIncome: Def1_Calculation =
    copy(taxDeductedAtSource = taxDeductedAtSource.map(_.withoutTaxTakenOffTradingIncome))

  def withoutOtherIncome: Def1_Calculation =
    copy(otherIncome = None)

  val isDefined: Boolean =
    !(allowancesAndDeductions.isEmpty &&
      reliefs.isEmpty && taxDeductedAtSource.isEmpty &&
      giftAid.isEmpty &&
      royaltyPayments.isEmpty &&
      notionalTax.isEmpty &&
      marriageAllowanceTransferredIn.isEmpty &&
      pensionContributionReliefs.isEmpty &&
      pensionSavingsTaxCharges.isEmpty &&
      studentLoans.isEmpty &&
      codedOutUnderpayments.isEmpty &&
      foreignPropertyIncome.isEmpty &&
      businessProfitAndLoss.isEmpty &&
      employmentAndPensionsIncome.isEmpty &&
      employmentExpenses.isEmpty &&
      seafarersDeductions.isEmpty &&
      foreignTaxForFtcrNotClaimed.isEmpty &&
      stateBenefitsIncome.isEmpty &&
      shareSchemesIncome.isEmpty &&
      foreignIncome.isEmpty &&
      chargeableEventGainsIncome.isEmpty &&
      savingsAndGainsIncome.isEmpty &&
      otherIncome.isEmpty &&
      dividendsIncome.isEmpty &&
      incomeSummaryTotals.isEmpty &&
      taxCalculation.isEmpty &&
      previousCalculation.isEmpty &&
      endOfYearEstimate.isEmpty &&
      lossesAndClaims.isEmpty)

}

object Def1_Calculation {

  implicit val reads: Reads[Def1_Calculation] = for {
    allowancesAndDeductions        <- (JsPath \ "allowancesAndDeductions").readNullable[Def1_AllowancesAndDeductions]
    reliefs                        <- (JsPath \ "reliefs").readNullable[Def1_Reliefs]
    taxDeductedAtSource            <- (JsPath \ "taxDeductedAtSource").readNullable[Def1_TaxDeductedAtSource]
    giftAid                        <- (JsPath \ "giftAid").readNullable[Def1_GiftAid]
    royaltyPayments                <- (JsPath \ "royaltyPayments").readNullable[Def1_RoyaltyPayments]
    notionalTax                    <- (JsPath \ "notionalTax").readNullable[Def1_NotionalTax]
    marriageAllowanceTransferredIn <- (JsPath \ "marriageAllowanceTransferredIn").readNullable[Def1_MarriageAllowanceTransferredIn]
    pensionContributionReliefs     <- (JsPath \ "pensionContributionReliefs").readNullable[Def1_PensionContributionReliefs]
    pensionSavingsTaxCharges       <- (JsPath \ "pensionSavingsTaxCharges").readNullable[Def1_PensionSavingsTaxCharges]
    studentLoans                   <- (JsPath \ "studentLoans").readNullable[Seq[Def1_StudentLoans]]
    codedOutUnderpayments          <- (JsPath \ "codedOutUnderpayments").readNullable[Def1_CodedOutUnderpayments]
    foreignPropertyIncome          <- (JsPath \ "foreignPropertyIncome").readNullable[Seq[Def1_ForeignPropertyIncome]]
    businessProfitAndLoss          <- (JsPath \ "businessProfitAndLoss").readNullable[Seq[Def1_BusinessProfitAndLoss]]
    employmentAndPensionsIncome    <- (JsPath \ "employmentAndPensionsIncome").readNullable[Def1_EmploymentAndPensionsIncome]
    employmentExpenses             <- (JsPath \ "employmentExpenses").readNullable[Def1_EmploymentExpenses]
    seafarersDeductions            <- (JsPath \ "seafarersDeductions").readNullable[Def1_SeafarersDeductions]
    foreignTaxForFtcrNotClaimed    <- (JsPath \ "foreignTaxForFtcrNotClaimed").readNullable[Def1_ForeignTaxForFtcrNotClaimed]
    stateBenefitsIncome            <- (JsPath \ "stateBenefitsIncome").readNullable[Def1_StateBenefitsIncome]
    shareSchemesIncome             <- (JsPath \ "shareSchemesIncome").readNullable[Def1_ShareSchemesIncome]
    foreignIncome                  <- (JsPath \ "foreignIncome").readNullable[Def1_ForeignIncome]
    chargeableEventGainsIncome     <- (JsPath \ "chargeableEventGainsIncome").readNullable[Def1_ChargeableEventGainsIncome]
    savingsAndGainsIncome          <- (JsPath \ "savingsAndGainsIncome").readNullable[Def1_SavingsAndGainsIncome]
    otherIncome                    <- (JsPath \ "otherIncome").readNullable[Def1_OtherIncome]
    dividendsIncome                <- (JsPath \ "dividendsIncome").readNullable[Def1_DividendsIncome]
    incomeSummaryTotals            <- (JsPath \ "incomeSummaryTotals").readNullable[Def1_IncomeSummaryTotals]
    taxCalculation                 <- (JsPath \ "taxCalculation").readNullable[Def1_TaxCalculation]
    previousCalculation            <- (JsPath \ "previousCalculation").readNullable[Def1_PreviousCalculation]
    endOfYearEstimate              <- (JsPath \ "endOfYearEstimate").readNullable[Def1_EndOfYearEstimate]
    lossesAndClaims                <- (JsPath \ "lossesAndClaims").readNullable[Def1_LossesAndClaims]
  } yield {
    Def1_Calculation(
      allowancesAndDeductions = allowancesAndDeductions,
      reliefs = reliefs,
      taxDeductedAtSource = taxDeductedAtSource,
      giftAid = giftAid,
      royaltyPayments = royaltyPayments,
      notionalTax = notionalTax,
      marriageAllowanceTransferredIn = marriageAllowanceTransferredIn,
      pensionContributionReliefs = pensionContributionReliefs,
      pensionSavingsTaxCharges = pensionSavingsTaxCharges,
      studentLoans = studentLoans,
      codedOutUnderpayments = codedOutUnderpayments,
      foreignPropertyIncome = foreignPropertyIncome,
      businessProfitAndLoss = businessProfitAndLoss,
      employmentAndPensionsIncome = employmentAndPensionsIncome,
      employmentExpenses = employmentExpenses,
      seafarersDeductions = seafarersDeductions,
      foreignTaxForFtcrNotClaimed = foreignTaxForFtcrNotClaimed,
      stateBenefitsIncome = stateBenefitsIncome,
      shareSchemesIncome = shareSchemesIncome,
      foreignIncome = foreignIncome,
      chargeableEventGainsIncome = chargeableEventGainsIncome,
      savingsAndGainsIncome = savingsAndGainsIncome,
      otherIncome = otherIncome,
      dividendsIncome = dividendsIncome,
      incomeSummaryTotals = incomeSummaryTotals,
      taxCalculation = taxCalculation,
      previousCalculation = previousCalculation,
      endOfYearEstimate = endOfYearEstimate,
      lossesAndClaims = lossesAndClaims
    )
  }

  implicit val writes: OWrites[Def1_Calculation] = (o: Def1_Calculation) => {
    JsObject(
      Map(
        "allowancesAndDeductions"        -> Json.toJson(o.allowancesAndDeductions),
        "reliefs"                        -> Json.toJson(o.reliefs),
        "taxDeductedAtSource"            -> Json.toJson(o.taxDeductedAtSource),
        "giftAid"                        -> Json.toJson(o.giftAid),
        "royaltyPayments"                -> Json.toJson(o.royaltyPayments),
        "notionalTax"                    -> Json.toJson(o.notionalTax),
        "marriageAllowanceTransferredIn" -> Json.toJson(o.marriageAllowanceTransferredIn),
        "pensionContributionReliefs"     -> Json.toJson(o.pensionContributionReliefs),
        "pensionSavingsTaxCharges"       -> Json.toJson(o.pensionSavingsTaxCharges),
        "studentLoans"                   -> Json.toJson(o.studentLoans),
        "codedOutUnderpayments"          -> Json.toJson(o.codedOutUnderpayments),
        "foreignPropertyIncome"          -> Json.toJson(o.foreignPropertyIncome),
        "businessProfitAndLoss"          -> Json.toJson(o.businessProfitAndLoss),
        "employmentAndPensionsIncome"    -> Json.toJson(o.employmentAndPensionsIncome),
        "employmentExpenses"             -> Json.toJson(o.employmentExpenses),
        "seafarersDeductions"            -> Json.toJson(o.seafarersDeductions),
        "foreignTaxForFtcrNotClaimed"    -> Json.toJson(o.foreignTaxForFtcrNotClaimed),
        "stateBenefitsIncome"            -> Json.toJson(o.stateBenefitsIncome),
        "shareSchemesIncome"             -> Json.toJson(o.shareSchemesIncome),
        "foreignIncome"                  -> Json.toJson(o.foreignIncome),
        "chargeableEventGainsIncome"     -> Json.toJson(o.chargeableEventGainsIncome),
        "savingsAndGainsIncome"          -> Json.toJson(o.savingsAndGainsIncome),
        "otherIncome"                    -> Json.toJson(o.otherIncome),
        "dividendsIncome"                -> Json.toJson(o.dividendsIncome),
        "incomeSummaryTotals"            -> Json.toJson(o.incomeSummaryTotals),
        "taxCalculation"                 -> Json.toJson(o.taxCalculation),
        "previousCalculation"            -> Json.toJson(o.previousCalculation),
        "endOfYearEstimate"              -> Json.toJson(o.endOfYearEstimate),
        "lossesAndClaims"                -> Json.toJson(o.lossesAndClaims)
      ).filterNot { case (_, value) =>
        value == JsNull
      }
    )
  }

}
