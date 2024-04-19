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

package v5.retrieveCalculation.def2.model.response.calculation

import play.api.libs.json._
import v5.retrieveCalculation.def2.model.response.calculation.allowancesAndDeductions.AllowancesAndDeductions
import v5.retrieveCalculation.def2.model.response.calculation.businessProfitAndLoss.BusinessProfitAndLoss
import v5.retrieveCalculation.def2.model.response.calculation.chargeableEventGainsIncome.ChargeableEventGainsIncome
import v5.retrieveCalculation.def2.model.response.calculation.codedOutUnderpayments.CodedOutUnderpayments
import v5.retrieveCalculation.def2.model.response.calculation.dividendsIncome.DividendsIncome
import v5.retrieveCalculation.def2.model.response.calculation.employmentAndPensionsIncome.EmploymentAndPensionsIncome
import v5.retrieveCalculation.def2.model.response.calculation.employmentExpenses.EmploymentExpenses
import v5.retrieveCalculation.def2.model.response.calculation.endOfYearEstimate.EndOfYearEstimate
import v5.retrieveCalculation.def2.model.response.calculation.foreignIncome.ForeignIncome
import v5.retrieveCalculation.def2.model.response.calculation.foreignPropertyIncome.ForeignPropertyIncome
import v5.retrieveCalculation.def2.model.response.calculation.foreignTaxForFtcrNotClaimed.ForeignTaxForFtcrNotClaimed
import v5.retrieveCalculation.def2.model.response.calculation.giftAid.GiftAid
import v5.retrieveCalculation.def2.model.response.calculation.incomeSummaryTotals.IncomeSummaryTotals
import v5.retrieveCalculation.def2.model.response.calculation.lossesAndClaims.LossesAndClaims
import v5.retrieveCalculation.def2.model.response.calculation.marriageAllowanceTransferredIn.MarriageAllowanceTransferredIn
import v5.retrieveCalculation.def2.model.response.calculation.notionalTax.NotionalTax
import v5.retrieveCalculation.def2.model.response.calculation.otherIncome.OtherIncome
import v5.retrieveCalculation.def2.model.response.calculation.pensionContributionReliefs.PensionContributionReliefs
import v5.retrieveCalculation.def2.model.response.calculation.pensionSavingsTaxCharges.PensionSavingsTaxCharges
import v5.retrieveCalculation.def2.model.response.calculation.previousCalculation.PreviousCalculation
import v5.retrieveCalculation.def2.model.response.calculation.reliefs.Reliefs
import v5.retrieveCalculation.def2.model.response.calculation.royaltyPayments.RoyaltyPayments
import v5.retrieveCalculation.def2.model.response.calculation.savingsAndGainsIncome.SavingsAndGainsIncome
import v5.retrieveCalculation.def2.model.response.calculation.seafarersDeductions.SeafarersDeductions
import v5.retrieveCalculation.def2.model.response.calculation.shareSchemesIncome.ShareSchemesIncome
import v5.retrieveCalculation.def2.model.response.calculation.stateBenefitsIncome.StateBenefitsIncome
import v5.retrieveCalculation.def2.model.response.calculation.studentLoans.StudentLoans
import v5.retrieveCalculation.def2.model.response.calculation.taxCalculation.TaxCalculation
import v5.retrieveCalculation.def2.model.response.calculation.taxDeductedAtSource.TaxDeductedAtSource
import v5.retrieveCalculation.def2.model.response.calculation.transitionProfit.TransitionProfit

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
    lossesAndClaims: Option[LossesAndClaims],
    transitionProfit: Option[TransitionProfit]
)

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
    transitionProfit               <- (JsPath \ "transitionProfit").readNullable[TransitionProfit]
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
      lossesAndClaims = lossesAndClaims,
      transitionProfit = transitionProfit
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
        "lossesAndClaims"                -> Json.toJson(o.lossesAndClaims),
        "transitionProfit"               -> Json.toJson(o.transitionProfit)
      ).filterNot { case (_, value) =>
        value == JsNull
      }
    )
  }

}
