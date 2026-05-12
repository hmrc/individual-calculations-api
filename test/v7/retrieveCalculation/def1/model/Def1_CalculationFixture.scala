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

package v7.retrieveCalculation.def1.model

import play.api.libs.json.{JsObject, JsValue, Json}
import shared.models.domain.TaxYear
import v7.common.model.response.RetrieveCalculationType.`in-year`
import v7.common.model.response.IncomeSourceType
import v7.retrieveCalculation.def1.model.response.calculation.Calculation
import v7.retrieveCalculation.def1.model.response.calculation.employmentAndPensionsIncome.{
  EmploymentAndPensionsIncome,
  EmploymentAndPensionsIncomeDetail
}
import v7.retrieveCalculation.def1.model.response.calculation.endOfYearEstimate.EndOfYearEstimate
import v7.retrieveCalculation.def1.model.response.calculation.otherIncome.{OtherIncome, PostCessationIncome, PostCessationReceipt}
import v7.retrieveCalculation.def1.model.response.calculation.reliefs.{BasicRateExtension, Reliefs}
import v7.retrieveCalculation.def1.model.response.calculation.taxCalculation.{Class2Nics, IncomeTax, Nics, TaxCalculation}
import v7.retrieveCalculation.def1.model.response.calculation.taxDeductedAtSource.TaxDeductedAtSource
import v7.retrieveCalculation.def1.model.response.inputs._
import v7.retrieveCalculation.def1.model.response.metadata.CalculationReason.`customer-request`
import v7.retrieveCalculation.def1.model.response.metadata.Metadata
import v7.retrieveCalculation.models.response.Def1_RetrieveCalculationResponse

trait Def1_CalculationFixture {

  val totalBasicRateExtension      = 2000
  val totalAllowancesAndDeductions = 100
  val incomeTaxValue               = 50

  val calculationMtdJson: JsValue =
    Json.parse(getClass.getResourceAsStream("/v7/retrieveCalculation/def1/model/response/calculation_mtd.json"))

  val calculationDownstreamJson: JsObject =
    Json.parse(getClass.getResourceAsStream("/v7/retrieveCalculation/def1/model/response/calculation_downstream.json")).as[JsObject]

  val reliefs: Reliefs = Reliefs(
    basicRateExtension =
      Some(BasicRateExtension(totalBasicRateExtension = Some(totalBasicRateExtension), giftAidRelief = None, pensionContributionReliefs = None)),
    residentialFinanceCosts = None,
    foreignTaxCreditRelief = None,
    topSlicingRelief = None,
    reliefsClaimed = None
  )

  val reliefsWithBasicRateDivergenceData: Reliefs =
    reliefs.copy(basicRateExtension = None)

  val class2Nics: Class2Nics = Class2Nics(
    amount = None,
    weeklyRate = None,
    weeks = None,
    limit = None,
    apportionedLimit = None,
    underSmallProfitThreshold = true,
    underLowerProfitThreshold = Some(true),
    actualClass2Nic = None
  )

  val nics: Nics = Nics(class2Nics = Some(class2Nics), class4Nics = None, nic2NetOfDeductions = None, nic4NetOfDeductions = None, totalNic = None)

  val incomeTax: IncomeTax = IncomeTax(
    totalIncomeReceivedFromAllSources = incomeTaxValue,
    totalAllowancesAndDeductions = incomeTaxValue,
    totalTaxableIncome = incomeTaxValue,
    payPensionsProfit = None,
    savingsAndGains = None,
    dividends = None,
    lumpSums = None,
    gainsOnLifePolicies = None,
    incomeTaxCharged = incomeTaxValue,
    totalReliefs = None,
    incomeTaxDueAfterReliefs = None,
    totalNotionalTax = None,
    marriageAllowanceRelief = None,
    incomeTaxDueAfterTaxReductions = None,
    incomeTaxDueAfterGiftAid = None,
    totalPensionSavingsTaxCharges = None,
    statePensionLumpSumCharges = None,
    payeUnderpaymentsCodedOut = None,
    totalIncomeTaxDue = None
  )

  val taxCalculation: TaxCalculation = TaxCalculation(
    incomeTax = Some(incomeTax),
    nics = Some(nics),
    totalTaxDeductedBeforeCodingOut = None,
    saUnderpaymentsCodedOut = None,
    totalIncomeTaxNicsCharged = None,
    totalStudentLoansRepaymentAmount = None,
    totalAnnuityPaymentsTaxCharged = None,
    totalRoyaltyPaymentsTaxCharged = None,
    totalTaxDeducted = None,
    totalIncomeTaxAndNicsDue = Some(incomeTaxValue),
    capitalGainsTax = None,
    totalIncomeTaxAndNicsAndCgt = None
  )

  val employmentAndPensionsIncomeDetails: EmploymentAndPensionsIncomeDetail = EmploymentAndPensionsIncomeDetail(
    incomeSourceId = None,
    source = None,
    occupationalPension = None,
    employerRef = None,
    employerName = None,
    offPayrollWorker = Some(true),
    payrollId = None,
    startDate = None,
    dateEmploymentEnded = None,
    taxablePayToDate = None,
    totalTaxToDate = None,
    disguisedRemuneration = None,
    lumpSums = None,
    studentLoans = None,
    benefitsInKind = None
  )

  val employmentAndPensionsIncome: EmploymentAndPensionsIncome = EmploymentAndPensionsIncome(
    totalPayeEmploymentAndLumpSumIncome = None,
    totalOccupationalPensionIncome = None,
    totalBenefitsInKind = None,
    tipsIncome = None,
    employmentAndPensionsIncomeDetail = Some(Seq(employmentAndPensionsIncomeDetails))
  )

  val eoyEstimates: EndOfYearEstimate = EndOfYearEstimate(
    incomeSource = None,
    totalEstimatedIncome = None,
    totalAllowancesAndDeductions = Some(totalAllowancesAndDeductions),
    totalTaxableIncome = None,
    incomeTaxAmount = None,
    nic2 = None,
    nic4 = None,
    totalTaxDeductedBeforeCodingOut = None,
    saUnderpaymentsCodedOut = None,
    totalNicAmount = None,
    totalStudentLoansRepaymentAmount = None,
    totalAnnuityPaymentsTaxCharged = None,
    totalRoyaltyPaymentsTaxCharged = None,
    totalTaxDeducted = None,
    incomeTaxNicAmount = None,
    cgtAmount = None,
    incomeTaxNicAndCgtAmount = None
  )
  // @formatter:on

  val calculationWithR8BData: Calculation = Calculation(
    reliefs = Some(reliefs),
    allowancesAndDeductions = None,
    taxDeductedAtSource = None,
    giftAid = None,
    royaltyPayments = None,
    notionalTax = None,
    marriageAllowanceTransferredIn = None,
    pensionContributionReliefs = None,
    pensionSavingsTaxCharges = None,
    studentLoans = None,
    codedOutUnderpayments = None,
    foreignPropertyIncome = None,
    businessProfitAndLoss = None,
    employmentAndPensionsIncome = Some(employmentAndPensionsIncome),
    employmentExpenses = None,
    seafarersDeductions = None,
    foreignTaxForFtcrNotClaimed = None,
    stateBenefitsIncome = None,
    shareSchemesIncome = None,
    foreignIncome = None,
    chargeableEventGainsIncome = None,
    savingsAndGainsIncome = None,
    otherIncome = None,
    dividendsIncome = None,
    incomeSummaryTotals = None,
    taxCalculation = Some(taxCalculation),
    previousCalculation = None,
    endOfYearEstimate = Some(eoyEstimates),
    lossesAndClaims = None
  )

  val taxDeductedAtSource: TaxDeductedAtSource =
    TaxDeductedAtSource(None, None, None, None, None, None, None, None, None, None, taxTakenOffTradingIncome = Some(2000.00))

  val otherIncome: OtherIncome =
    OtherIncome(
      totalOtherIncome = 2000.00,
      postCessationIncome = Some(
        PostCessationIncome(
          totalPostCessationReceipts = 2000.00,
          postCessationReceipts = Seq(PostCessationReceipt(amount = 100.00, taxYearIncomeToBeTaxed = "2019-20"))))
    )

  val calculationWithAdditionalFields: Calculation = Calculation(
    reliefs = None,
    allowancesAndDeductions = None,
    taxDeductedAtSource = None,
    giftAid = None,
    royaltyPayments = None,
    notionalTax = None,
    marriageAllowanceTransferredIn = None,
    pensionContributionReliefs = None,
    pensionSavingsTaxCharges = None,
    studentLoans = None,
    codedOutUnderpayments = None,
    foreignPropertyIncome = None,
    businessProfitAndLoss = None,
    employmentAndPensionsIncome = None,
    employmentExpenses = None,
    seafarersDeductions = None,
    foreignTaxForFtcrNotClaimed = None,
    stateBenefitsIncome = None,
    shareSchemesIncome = None,
    foreignIncome = None,
    chargeableEventGainsIncome = None,
    savingsAndGainsIncome = None,
    otherIncome = Some(otherIncome),
    dividendsIncome = None,
    incomeSummaryTotals = None,
    taxCalculation = None,
    previousCalculation = None,
    endOfYearEstimate = None,
    lossesAndClaims = None
  )

  val metadata: Metadata = Metadata(
    calculationId = "",
    taxYear = TaxYear.fromDownstream("2018"),
    requestedBy = "",
    requestedTimestamp = None,
    calculationReason = `customer-request`,
    calculationTimestamp = None,
    calculationType = `in-year`,
    intentToSubmitFinalDeclaration = false,
    finalDeclaration = false,
    finalDeclarationTimestamp = None,
    periodFrom = "",
    periodTo = ""
  )

  val inputs: Inputs = Inputs(
    PersonalInformation("", None, TaxRegime.`uk`, None, None, None, None, None, None),
    IncomeSources(None, None),
    // @formatter:off
    None, None, None, None,
    None, None, None
  )
  // @formatter:on

  val businessIncomeSourceWithAdditionalField: BusinessIncomeSource = BusinessIncomeSource(
    incomeSourceId = "000000000000210",
    incomeSourceType = IncomeSourceType.`self-employment`,
    incomeSourceName = Some("string"),
    accountingPeriodStartDate = "2018-04-06",
    accountingPeriodEndDate = "2019-04-05",
    source = "MTD-SA",
    commencementDate = Some("2018-04-06"),
    cessationDate = Some("2019-04-05"),
    latestPeriodEndDate = "2021-12-02",
    latestReceivedDateTime = "2021-12-02T15:25:48Z",
    submissionPeriods = Some(
      Seq(
        SubmissionPeriod(
          periodId = Some("001"),
          startDate = "2018-04-06",
          endDate = "2019-04-05",
          receivedDateTime = "2019-02-15T09:35:04.843Z"
        )))
  )

  val inputsWithAdditionalFields: Inputs = Inputs(
    PersonalInformation("", None, TaxRegime.`uk`, None, None, None, None, None, Some("No Status")),
    IncomeSources(Some(Seq(businessIncomeSourceWithAdditionalField)), None),
    // @formatter:off
    None, None, None, None,
    None, None, None
  )
  // @formatter:on

  val minimalCalculationR8bResponse: Def1_RetrieveCalculationResponse = Def1_RetrieveCalculationResponse(
    metadata = metadata,
    inputs = inputs,
    calculation = Some(calculationWithR8BData),
    messages = None
  )

  val minimalCalculationResponse: Def1_RetrieveCalculationResponse = Def1_RetrieveCalculationResponse(
    metadata = metadata,
    inputs = inputs,
    calculation = Some(calculationWithR8BData),
    messages = None
  )

  val minimumCalculationResponseR8BEnabledJson: JsObject = Json
    .parse(
      """
        |{
        |  "metadata": {
        |    "calculationId": "",
        |    "taxYear": "2017-18",
        |    "requestedBy": "",
        |    "calculationReason": "customer-request",
        |    "calculationType": "in-year",
        |    "intentToSubmitFinalDeclaration": false,
        |    "finalDeclaration": false,
        |    "periodFrom": "",
        |    "periodTo": ""
        |  },
        |  "inputs": {
        |    "personalInformation": {
        |      "identifier": "",
        |      "taxRegime": "uk"
        |    },
        |    "incomeSources": {}
        |  },
        |  "calculation": {
        |    "taxCalculation": {
        |      "incomeTax": {
        |        "totalIncomeReceivedFromAllSources": 50,
        |        "totalAllowancesAndDeductions": 50,
        |        "totalTaxableIncome": 50,
        |        "incomeTaxCharged": 50
        |      },
        |      "nics": {
        |        "class2Nics": {
        |          "underSmallProfitThreshold": true,
        |          "underLowerProfitThreshold": true
        |        }
        |      },
        |      "totalIncomeTaxAndNicsDue": 50
        |    },
        |    "employmentAndPensionsIncome": {
        |      "employmentAndPensionsIncomeDetail": [
        |        {
        |          "offPayrollWorker": true
        |        }
        |      ]
        |    },
        |    "reliefs": {
        |      "basicRateExtension": {
        |        "totalBasicRateExtension": 2000
        |      }
        |    },
        |    "endOfYearEstimate": {
        |      "totalAllowancesAndDeductions": 100
        |    }
        |  }
        |}
    """.stripMargin
    )
    .as[JsObject]

}
