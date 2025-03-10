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

package v8.retrieveCalculation.def1.model.response.calculation

import play.api.libs.json._
import v8.retrieveCalculation.def1.model.response.calculation.allowancesAndDeductions.AllowancesAndDeductions
import v8.retrieveCalculation.def1.model.response.calculation.businessProfitAndLoss.BusinessProfitAndLoss
import v8.retrieveCalculation.def1.model.response.calculation.chargeableEventGainsIncome.ChargeableEventGainsIncome
import v8.retrieveCalculation.def1.model.response.calculation.codedOutUnderpayments.CodedOutUnderpayments
import v8.retrieveCalculation.def1.model.response.calculation.dividendsIncome.DividendsIncome
import v8.retrieveCalculation.def1.model.response.calculation.employmentAndPensionsIncome.EmploymentAndPensionsIncome
import v8.retrieveCalculation.def1.model.response.calculation.employmentExpenses.EmploymentExpenses
import v8.retrieveCalculation.def1.model.response.calculation.endOfYearEstimate.EndOfYearEstimate
import v8.retrieveCalculation.def1.model.response.calculation.foreignIncome.ForeignIncome
import v8.retrieveCalculation.def1.model.response.calculation.foreignPropertyIncome.ForeignPropertyIncome
import v8.retrieveCalculation.def1.model.response.calculation.foreignTaxForFtcrNotClaimed.ForeignTaxForFtcrNotClaimed
import v8.retrieveCalculation.def1.model.response.calculation.giftAid.GiftAid
import v8.retrieveCalculation.def1.model.response.calculation.incomeSummaryTotals.IncomeSummaryTotals
import v8.retrieveCalculation.def1.model.response.calculation.lossesAndClaims.LossesAndClaims
import v8.retrieveCalculation.def1.model.response.calculation.marriageAllowanceTransferredIn.MarriageAllowanceTransferredIn
import v8.retrieveCalculation.def1.model.response.calculation.notionalTax.NotionalTax
import v8.retrieveCalculation.def1.model.response.calculation.otherIncome.OtherIncome
import v8.retrieveCalculation.def1.model.response.calculation.pensionContributionReliefs.PensionContributionReliefs
import v8.retrieveCalculation.def1.model.response.calculation.pensionSavingsTaxCharges.PensionSavingsTaxCharges
import v8.retrieveCalculation.def1.model.response.calculation.previousCalculation.PreviousCalculation
import v8.retrieveCalculation.def1.model.response.calculation.reliefs.Reliefs
import v8.retrieveCalculation.def1.model.response.calculation.royaltyPayments.RoyaltyPayments
import v8.retrieveCalculation.def1.model.response.calculation.savingsAndGainsIncome.SavingsAndGainsIncome
import v8.retrieveCalculation.def1.model.response.calculation.seafarersDeductions.SeafarersDeductions
import v8.retrieveCalculation.def1.model.response.calculation.shareSchemesIncome.ShareSchemesIncome
import v8.retrieveCalculation.def1.model.response.calculation.stateBenefitsIncome.StateBenefitsIncome
import v8.retrieveCalculation.def1.model.response.calculation.studentLoans.StudentLoans
import v8.retrieveCalculation.def1.model.response.calculation.taxCalculation.TaxCalculation
import v8.retrieveCalculation.def1.model.response.calculation.taxDeductedAtSource.TaxDeductedAtSource

case class Calculation(
    allowancesAndDeductions: Option[AllowancesAndDeductions],
    reliefs: Option[Reliefs],
    taxDeductedAtSource: Option[TaxDeductedAtSource],
    giftAid: Option[GiftAid],
    royaltyPayments: Option[RoyaltyPayments],
    notionalTax: Option[NotionalTax],
    marriageAllowanceTransferredIn: Option[MarriageAllowanceTransferredIn],
    pensionContributionReliefs: Option[PensionContributionReliefs],
    pensionSavingsTaxCharges: Option[PensionSavingsTaxCharges],
    studentLoans: Option[Seq[StudentLoans]],
    codedOutUnderpayments: Option[CodedOutUnderpayments],
    foreignPropertyIncome: Option[Seq[ForeignPropertyIncome]],
    businessProfitAndLoss: Option[Seq[BusinessProfitAndLoss]],
    employmentAndPensionsIncome: Option[EmploymentAndPensionsIncome],
    employmentExpenses: Option[EmploymentExpenses],
    seafarersDeductions: Option[SeafarersDeductions],
    foreignTaxForFtcrNotClaimed: Option[ForeignTaxForFtcrNotClaimed],
    stateBenefitsIncome: Option[StateBenefitsIncome],
    shareSchemesIncome: Option[ShareSchemesIncome],
    foreignIncome: Option[ForeignIncome],
    chargeableEventGainsIncome: Option[ChargeableEventGainsIncome],
    savingsAndGainsIncome: Option[SavingsAndGainsIncome],
    otherIncome: Option[OtherIncome],
    dividendsIncome: Option[DividendsIncome],
    incomeSummaryTotals: Option[IncomeSummaryTotals],
    taxCalculation: Option[TaxCalculation],
    previousCalculation: Option[PreviousCalculation],
    endOfYearEstimate: Option[EndOfYearEstimate],
    lossesAndClaims: Option[LossesAndClaims]
) {

  def withoutBasicExtension: Calculation = copy(reliefs = reliefs.map(_.withoutBasicExtension).filter(_.isDefined))

  def withoutGiftAidTaxReductionWhereBasicRateDiffers: Calculation =
    copy(reliefs = reliefs.map(_.withoutGiftAidTaxReductionWhereBasicRateDiffers).filter(_.isDefined))

  def withoutGiftAidTaxChargeWhereBasicRateDiffers: Calculation =
    copy(taxCalculation = taxCalculation.map(_.withoutGiftAidTaxChargeWhereBasicRateDiffers))

  def withoutUnderLowerProfitThreshold: Calculation =
    copy(taxCalculation = taxCalculation.map(_.withoutUnderLowerProfitThreshold))

  def withoutOffPayrollWorker: Calculation =
    copy(employmentAndPensionsIncome = employmentAndPensionsIncome.map(_.withoutOffPayrollWorker).filter(_.isDefined))

  def withoutTotalAllowanceAndDeductions: Calculation =
    copy(endOfYearEstimate = endOfYearEstimate.map(_.withoutTotalAllowanceAndDeductions).filter(_.isDefined))

  def withoutTaxTakenOffTradingIncome: Calculation =
    copy(taxDeductedAtSource = taxDeductedAtSource.map(_.withoutTaxTakenOffTradingIncome))

  def withoutOtherIncome: Calculation =
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

object Calculation {

  implicit val reads: Reads[Calculation] = for {
    allowancesAndDeductions        <- (JsPath \ "allowancesAndDeductions").readNullable[AllowancesAndDeductions]
    reliefs                        <- (JsPath \ "reliefs").readNullable[Reliefs]
    taxDeductedAtSource            <- (JsPath \ "taxDeductedAtSource").readNullable[TaxDeductedAtSource]
    giftAid                        <- (JsPath \ "giftAid").readNullable[GiftAid]
    royaltyPayments                <- (JsPath \ "royaltyPayments").readNullable[RoyaltyPayments]
    notionalTax                    <- (JsPath \ "notionalTax").readNullable[NotionalTax]
    marriageAllowanceTransferredIn <- (JsPath \ "marriageAllowanceTransferredIn").readNullable[MarriageAllowanceTransferredIn]
    pensionContributionReliefs     <- (JsPath \ "pensionContributionReliefs").readNullable[PensionContributionReliefs]
    pensionSavingsTaxCharges       <- (JsPath \ "pensionSavingsTaxCharges").readNullable[PensionSavingsTaxCharges]
    studentLoans                   <- (JsPath \ "studentLoans").readNullable[Seq[StudentLoans]]
    codedOutUnderpayments          <- (JsPath \ "codedOutUnderpayments").readNullable[CodedOutUnderpayments]
    foreignPropertyIncome          <- (JsPath \ "foreignPropertyIncome").readNullable[Seq[ForeignPropertyIncome]]
    businessProfitAndLoss          <- (JsPath \ "businessProfitAndLoss").readNullable[Seq[BusinessProfitAndLoss]]
    employmentAndPensionsIncome    <- (JsPath \ "employmentAndPensionsIncome").readNullable[EmploymentAndPensionsIncome]
    employmentExpenses             <- (JsPath \ "employmentExpenses").readNullable[EmploymentExpenses]
    seafarersDeductions            <- (JsPath \ "seafarersDeductions").readNullable[SeafarersDeductions]
    foreignTaxForFtcrNotClaimed    <- (JsPath \ "foreignTaxForFtcrNotClaimed").readNullable[ForeignTaxForFtcrNotClaimed]
    stateBenefitsIncome            <- (JsPath \ "stateBenefitsIncome").readNullable[StateBenefitsIncome]
    shareSchemesIncome             <- (JsPath \ "shareSchemesIncome").readNullable[ShareSchemesIncome]
    foreignIncome                  <- (JsPath \ "foreignIncome").readNullable[ForeignIncome]
    chargeableEventGainsIncome     <- (JsPath \ "chargeableEventGainsIncome").readNullable[ChargeableEventGainsIncome]
    savingsAndGainsIncome          <- (JsPath \ "savingsAndGainsIncome").readNullable[SavingsAndGainsIncome]
    otherIncome                    <- (JsPath \ "otherIncome").readNullable[OtherIncome]
    dividendsIncome                <- (JsPath \ "dividendsIncome").readNullable[DividendsIncome]
    incomeSummaryTotals            <- (JsPath \ "incomeSummaryTotals").readNullable[IncomeSummaryTotals]
    taxCalculation                 <- (JsPath \ "taxCalculation").readNullable[TaxCalculation]
    previousCalculation            <- (JsPath \ "previousCalculation").readNullable[PreviousCalculation]
    endOfYearEstimate              <- (JsPath \ "endOfYearEstimate").readNullable[EndOfYearEstimate]
    lossesAndClaims                <- (JsPath \ "lossesAndClaims").readNullable[LossesAndClaims]
  } yield {
    Calculation(
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

  implicit val writes: OWrites[Calculation] = (o: Calculation) => {
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
