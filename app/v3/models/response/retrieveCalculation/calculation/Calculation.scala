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

package v3.models.response.retrieveCalculation.calculation

import play.api.libs.json._
import v3.models.response.retrieveCalculation.calculation.allowancesAndDeductions.AllowancesAndDeductions
import v3.models.response.retrieveCalculation.calculation.businessProfitAndLoss.BusinessProfitAndLoss
import v3.models.response.retrieveCalculation.calculation.chargeableEventGainsIncome.ChargeableEventGainsIncome
import v3.models.response.retrieveCalculation.calculation.codedOutUnderpayments.CodedOutUnderpayments
import v3.models.response.retrieveCalculation.calculation.dividendsIncome.DividendsIncome
import v3.models.response.retrieveCalculation.calculation.employmentAndPensionsIncome.EmploymentAndPensionsIncome
import v3.models.response.retrieveCalculation.calculation.employmentExpenses.EmploymentExpenses
import v3.models.response.retrieveCalculation.calculation.endOfYearEstimate.EndOfYearEstimate
import v3.models.response.retrieveCalculation.calculation.foreignIncome.ForeignIncome
import v3.models.response.retrieveCalculation.calculation.foreignPropertyIncome.ForeignPropertyIncome
import v3.models.response.retrieveCalculation.calculation.foreignTaxForFtcrNotClaimed.ForeignTaxForFtcrNotClaimed
import v3.models.response.retrieveCalculation.calculation.giftAid.GiftAid
import v3.models.response.retrieveCalculation.calculation.incomeSummaryTotals.IncomeSummaryTotals
import v3.models.response.retrieveCalculation.calculation.lossesAndClaims.LossesAndClaims
import v3.models.response.retrieveCalculation.calculation.marriageAllowanceTransferredIn.MarriageAllowanceTransferredIn
import v3.models.response.retrieveCalculation.calculation.notionalTax.NotionalTax
import v3.models.response.retrieveCalculation.calculation.pensionContributionReliefs.PensionContributionReliefs
import v3.models.response.retrieveCalculation.calculation.pensionSavingsTaxCharges.PensionSavingsTaxCharges
import v3.models.response.retrieveCalculation.calculation.previousCalculation.PreviousCalculation
import v3.models.response.retrieveCalculation.calculation.reliefs.Reliefs
import v3.models.response.retrieveCalculation.calculation.royaltyPayments.RoyaltyPayments
import v3.models.response.retrieveCalculation.calculation.savingsAndGainsIncome.SavingsAndGainsIncome
import v3.models.response.retrieveCalculation.calculation.seafarersDeductions.SeafarersDeductions
import v3.models.response.retrieveCalculation.calculation.shareSchemesIncome.ShareSchemesIncome
import v3.models.response.retrieveCalculation.calculation.stateBenefitsIncome.StateBenefitsIncome
import v3.models.response.retrieveCalculation.calculation.studentLoans.StudentLoans
import v3.models.response.retrieveCalculation.calculation.taxCalculation.TaxCalculation
import v3.models.response.retrieveCalculation.calculation.taxDeductedAtSource.TaxDeductedAtSource

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
    dividendsIncome: Option[DividendsIncome],
    incomeSummaryTotals: Option[IncomeSummaryTotals],
    taxCalculation: Option[TaxCalculation],
    previousCalculation: Option[PreviousCalculation],
    endOfYearEstimate: Option[EndOfYearEstimate],
    lossesAndClaims: Option[LossesAndClaims]
) {

  def isEmpty: Boolean = allowancesAndDeductions.isEmpty &
    reliefs.isEmpty & taxDeductedAtSource.isEmpty &
    giftAid.isEmpty &
    royaltyPayments.isEmpty &
    notionalTax.isEmpty &
    marriageAllowanceTransferredIn.isEmpty &
    pensionContributionReliefs.isEmpty &
    pensionSavingsTaxCharges.isEmpty &
    studentLoans.isEmpty &
    codedOutUnderpayments.isEmpty &
    foreignPropertyIncome.isEmpty &
    businessProfitAndLoss.isEmpty &
    employmentAndPensionsIncome.isEmpty &
    employmentExpenses.isEmpty &
    seafarersDeductions.isEmpty &
    foreignTaxForFtcrNotClaimed.isEmpty &
    stateBenefitsIncome.isEmpty &
    shareSchemesIncome.isEmpty &
    foreignIncome.isEmpty &
    chargeableEventGainsIncome.isEmpty &
    savingsAndGainsIncome.isEmpty &
    dividendsIncome.isEmpty &
    incomeSummaryTotals.isEmpty &
    taxCalculation.isEmpty &
    previousCalculation.isEmpty &
    endOfYearEstimate.isEmpty &
    lossesAndClaims.isEmpty

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
