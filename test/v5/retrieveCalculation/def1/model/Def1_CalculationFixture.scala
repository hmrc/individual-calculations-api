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

package v5.retrieveCalculation.def1.model

import api.models.domain.TaxYear
import play.api.libs.json.{JsObject, JsValue, Json}
import v5.retrieveCalculation.def1.model.response.calculation.Def1_Calculation
import v5.retrieveCalculation.def1.model.response.calculation.employmentAndPensionsIncome.{Def1_EmploymentAndPensionsIncome, Def1_EmploymentAndPensionsIncomeDetail}
import v5.retrieveCalculation.def1.model.response.calculation.endOfYearEstimate.Def1_EndOfYearEstimate
import v5.retrieveCalculation.def1.model.response.calculation.otherIncome.{Def1_OtherIncome, Def1_PostCessationIncome, Def1_PostCessationReceipt}
import v5.retrieveCalculation.def1.model.response.calculation.reliefs.{Def1_BasicRateExtension, Def1_GiftAidTaxReductionWhereBasicRateDiffers, Def1_Reliefs}
import v5.retrieveCalculation.def1.model.response.calculation.taxCalculation.{Def1_Class2Nics, Def1_IncomeTax, Def1_Nics, Def1_TaxCalculation}
import v5.retrieveCalculation.def1.model.response.calculation.taxDeductedAtSource.Def1_TaxDeductedAtSource
import v5.retrieveCalculation.def1.model.response.common.Def1_CalculationType.`inYear`
import v5.retrieveCalculation.def1.model.response.common.Def1_IncomeSourceType
import v5.retrieveCalculation.def1.model.response.inputs._
import v5.retrieveCalculation.def1.model.response.metadata.Def1_Metadata

trait Def1_CalculationFixture {

  val totalBasicRateExtension = 2000
  val totalAllowancesAndDeductions = 100
  val incomeTaxValue = 50

  val calculationMtdJson: JsValue =
    Json.parse(getClass.getResourceAsStream("/v5/models/response/retrieveCalculation/calculation_mtd.json"))

  val calculationDownstreamJson: JsValue =
    Json.parse(getClass.getResourceAsStream("/v5/models/response/retrieveCalculation/calculation_downstream.json"))

  val reliefs: Def1_Reliefs = Def1_Reliefs(
    basicRateExtension =
      Some(Def1_BasicRateExtension(totalBasicRateExtension = Some(totalBasicRateExtension), giftAidRelief = None, pensionContributionReliefs = None)),
    residentialFinanceCosts = None,
    foreignTaxCreditRelief = None,
    topSlicingRelief = None,
    reliefsClaimed = None,
    giftAidTaxReductionWhereBasicRateDiffers = None
  )

  val reliefsWithBasicRateDivergenceData: Def1_Reliefs =
    reliefs.copy(basicRateExtension = None, giftAidTaxReductionWhereBasicRateDiffers = Some(Def1_GiftAidTaxReductionWhereBasicRateDiffers(Some(2000.25))))

  val class2Nics: Def1_Class2Nics = Def1_Class2Nics(
    amount = None,
    weeklyRate = None,
    weeks = None,
    limit = None,
    apportionedLimit = None,
    underSmallProfitThreshold = true,
    underLowerProfitThreshold = Some(true),
    actualClass2Nic = None
  )

  val class2NicsWithoutUnderLowerProfitThreshold: Def1_Class2Nics = class2Nics.copy(underLowerProfitThreshold = None)

  val nics: Def1_Nics = Def1_Nics(class2Nics = Some(class2Nics), class4Nics = None, nic2NetOfDeductions = None, nic4NetOfDeductions = None, totalNic = None)
  val nicsWithoutUnderLowerProfitThreshold: Def1_Nics = nics.copy(class2Nics = Some(class2NicsWithoutUnderLowerProfitThreshold))

  val incomeTax: Def1_IncomeTax = Def1_IncomeTax(
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
    totalIncomeTaxDue = None,
    giftAidTaxChargeWhereBasicRateDiffers = None
  )

  val incomeTaxWithBasicRateDivergenceData: Def1_IncomeTax = incomeTax.copy(giftAidTaxChargeWhereBasicRateDiffers = Some(2000.25))

  val taxCalculation: Def1_TaxCalculation = Def1_TaxCalculation(
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

  val taxCalculationWithBasicRateDivergenceData: Def1_TaxCalculation = taxCalculation.copy(incomeTax = Some(incomeTaxWithBasicRateDivergenceData))

  val taxCalculationWithoutUnderLowerProfitThreshold: Def1_TaxCalculation = taxCalculation.copy(nics = Some(nicsWithoutUnderLowerProfitThreshold))

  val employmentAndPensionsIncomeDetails: Def1_EmploymentAndPensionsIncomeDetail = Def1_EmploymentAndPensionsIncomeDetail(
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

  val employmentAndPensionsIncome: Def1_EmploymentAndPensionsIncome = Def1_EmploymentAndPensionsIncome(
    totalPayeEmploymentAndLumpSumIncome = None,
    totalOccupationalPensionIncome = None,
    totalBenefitsInKind = None,
    tipsIncome = None,
    employmentAndPensionsIncomeDetail = Some(Seq(employmentAndPensionsIncomeDetails))
  )

  val eoyEstimates: Def1_EndOfYearEstimate = Def1_EndOfYearEstimate(
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

  val calculationWithR8BData: Def1_Calculation = Def1_Calculation(
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

  val taxDeductedAtSource: Def1_TaxDeductedAtSource =
    Def1_TaxDeductedAtSource(None, None, None, None, None, None, None, None, None, None, taxTakenOffTradingIncome = Some(2000.00))

  val otherIncome: Def1_OtherIncome =
    Def1_OtherIncome(
      totalOtherIncome = 2000.00,
      postCessationIncome = Some(
        Def1_PostCessationIncome(
          totalPostCessationReceipts = 2000.00,
          postCessationReceipts = Seq(Def1_PostCessationReceipt(amount = 100.00, taxYearIncomeToBeTaxed = "2019-20"))))
    )

  val calculationWithAdditionalFields: Def1_Calculation = Def1_Calculation(
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

  val calculationWithCl290Enabled: Def1_Calculation = Def1_Calculation(
    reliefs = None,
    allowancesAndDeductions = None,
    taxDeductedAtSource = Some(taxDeductedAtSource),
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
    otherIncome = None,
    dividendsIncome = None,
    incomeSummaryTotals = None,
    taxCalculation = None,
    previousCalculation = None,
    endOfYearEstimate = None,
    lossesAndClaims = None
  )

  val calculationWithBasicRateDivergenceEnabled: Def1_Calculation = Def1_Calculation(
    reliefs = Some(reliefsWithBasicRateDivergenceData),
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
    otherIncome = None,
    dividendsIncome = None,
    incomeSummaryTotals = None,
    taxCalculation = Some(taxCalculationWithBasicRateDivergenceData),
    previousCalculation = None,
    endOfYearEstimate = None,
    lossesAndClaims = None
  )

  val calculationWithR8BDisabled: Def1_Calculation =
    calculationWithR8BData.copy(employmentAndPensionsIncome = None, endOfYearEstimate = None, reliefs = None)

  val metadata: Def1_Metadata = Def1_Metadata(
    calculationId = "",
    taxYear = TaxYear.fromDownstream("2018"),
    requestedBy = "",
    requestedTimestamp = None,
    calculationReason = "",
    calculationTimestamp = None,
    calculationType = `inYear`,
    intentToSubmitFinalDeclaration = false,
    finalDeclaration = false,
    finalDeclarationTimestamp = None,
    periodFrom = "",
    periodTo = ""
  )
  val metadataWithBasicRateDivergenceData: Def1_Metadata = metadata.copy(taxYear = TaxYear.fromDownstream("2025"))

  val inputs: Def1_Inputs = Def1_Inputs(
    Def1_PersonalInformation("", None, "UK", None, None, None, None, None, None),
    Def1_IncomeSources(None, None),
    // @formatter:off
    None, None, None, None,
    None, None, None
  )
  // @formatter:on

  val businessIncomeSourceWithAdditionalField: Def1_BusinessIncomeSource = Def1_BusinessIncomeSource(
    incomeSourceId = "000000000000210",
    incomeSourceType = Def1_IncomeSourceType.`self-employment`,
    incomeSourceName = Some("string"),
    accountingPeriodStartDate = "2018-04-06",
    accountingPeriodEndDate = "2019-04-05",
    source = "MTD-SA",
    commencementDate = Some("2018-04-06"),
    cessationDate = Some("2019-04-05"),
    latestPeriodEndDate = "2021-12-02",
    latestReceivedDateTime = "2021-12-02T15:25:48Z",
    finalised = Some(true),
    finalisationTimestamp = Some("2019-02-15T09:35:15.094Z"),
    submissionPeriods = Some(
      Seq(
        Def1_SubmissionPeriod(
          periodId = Some("001"),
          startDate = "2018-04-06",
          endDate = "2019-04-05",
          receivedDateTime = "2019-02-15T09:35:04.843Z"
        )))
  )

  val inputsWithAdditionalFields: Def1_Inputs = Def1_Inputs(
    Def1_PersonalInformation("", None, "UK", None, None, None, None, None, Some("No status")),
    Def1_IncomeSources(Some(Seq(businessIncomeSourceWithAdditionalField)), None),
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

  val minimalCalculationAdditionalFieldsResponse: Def1_RetrieveCalculationResponse = Def1_RetrieveCalculationResponse(
    metadata = metadata,
    inputs = inputsWithAdditionalFields,
    calculation = Some(calculationWithAdditionalFields),
    messages = None
  )

  val minimalCalculationCl290EnabledResponse: Def1_RetrieveCalculationResponse = Def1_RetrieveCalculationResponse(
    metadata = metadata,
    inputs = inputs,
    calculation = Some(calculationWithCl290Enabled),
    messages = None
  )

  val minimalCalculationBasicRateDivergenceEnabledResponse: Def1_RetrieveCalculationResponse = Def1_RetrieveCalculationResponse(
    metadata = metadataWithBasicRateDivergenceData,
    inputs = inputs,
    calculation = Some(calculationWithBasicRateDivergenceEnabled),
    messages = None
  )

  // @formatter:off
  val emptyCalculation: Def1_Calculation = Def1_Calculation(
    None, None, None, None, None, None,
    None, None, None, None, None, None,
    None, None, None, None, None, None,
    None, None, None, None, None, None,
    None, None, None, None, None)

  val calcWithoutEndOfYearEstimate: Def1_Calculation = emptyCalculation.copy(reliefs= Some(reliefs),employmentAndPensionsIncome = Some(employmentAndPensionsIncome), taxCalculation = Some(taxCalculation))


  val calcWithoutBasicExtension: Def1_Calculation = emptyCalculation.copy(endOfYearEstimate =  Some(eoyEstimates), employmentAndPensionsIncome = Some(employmentAndPensionsIncome), taxCalculation = Some(taxCalculation))

 val calcWithoutOffPayrollWorker: Def1_Calculation = emptyCalculation.copy(reliefs= Some(reliefs),endOfYearEstimate =  Some(eoyEstimates), taxCalculation = Some(taxCalculation))
  val calcWithoutUnderLowerProfitThreshold: Def1_Calculation = emptyCalculation.copy(taxCalculation=Some(taxCalculationWithoutUnderLowerProfitThreshold),  reliefs= Some(reliefs),endOfYearEstimate =  Some(eoyEstimates), employmentAndPensionsIncome = Some(employmentAndPensionsIncome))
  // @formatter:on

  val minimalCalculationResponse: Def1_RetrieveCalculationResponse = Def1_RetrieveCalculationResponse(
    metadata = metadata,
    inputs = inputs,
    calculation = Some(calculationWithR8BData),
    messages = None
  )

  val minimalCalculationResponseWithoutR8BData: Def1_RetrieveCalculationResponse = Def1_RetrieveCalculationResponse(
    metadata = metadata,
    inputs = inputs,
    calculation = None,
    messages = None
  )

  val minimumCalculationResponseMtdJson: JsObject = Json
    .parse(
      """
        |{
        |  "metadata" : {
        |    "calculationId": "",
        |    "taxYear": "2017-18",
        |    "requestedBy": "",
        |    "calculationReason": "",
        |    "calculationType": "inYear",
        |    "intentToSubmitFinalDeclaration": false,
        |    "finalDeclaration": false,
        |    "periodFrom": "",
        |    "periodTo": ""
        |  },
        |  "inputs" : {
        |    "personalInformation": {
        |       "identifier": "",
        |       "taxRegime": "UK"
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
        |      "totalIncomeTaxAndNicsDue": 50,
        |      "nics": {
        |        "class2Nics": {
        |          "underSmallProfitThreshold": true,
        |          "underLowerProfitThreshold": true
        |        }
        |      }
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

  val minimumCalculationResponseR8BEnabledJson: JsObject = Json
    .parse(
      """
        |{
        |  "metadata": {
        |    "calculationId": "",
        |    "taxYear": "2017-18",
        |    "requestedBy": "",
        |    "calculationReason": "",
        |    "calculationType": "inYear",
        |    "intentToSubmitFinalDeclaration": false,
        |    "finalDeclaration": false,
        |    "periodFrom": "",
        |    "periodTo": ""
        |  },
        |  "inputs": {
        |    "personalInformation": {
        |      "identifier": "",
        |      "taxRegime": "UK"
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

  val responseAdditionalFieldsEnabledJson: JsObject = Json
    .parse(
      """
    {
      "metadata": {
        "calculationId": "",
        "taxYear": "2017-18",
        "requestedBy": "",
        "calculationReason": "",
        "calculationType": "inYear",
        "intentToSubmitFinalDeclaration": false,
        "finalDeclaration": false,
        "periodFrom": "",
        "periodTo": ""
      },
      "inputs": {
        "personalInformation": {
          "identifier": "",
          "taxRegime": "UK",
          "itsaStatus": "No status"
        },
        "incomeSources": {
          "businessIncomeSources": [
            {
              "incomeSourceId": "000000000000210",
              "incomeSourceType": "self-employment",
              "incomeSourceName": "string",
              "accountingPeriodStartDate": "2018-04-06",
              "accountingPeriodEndDate": "2019-04-05",
              "commencementDate": "2018-04-06",
              "cessationDate": "2019-04-05",
              "source": "MTD-SA",
              "latestPeriodEndDate": "2021-12-02",
              "latestReceivedDateTime": "2021-12-02T15:25:48Z",
              "finalised": true,
              "finalisationTimestamp": "2019-02-15T09:35:15.094Z",
              "submissionPeriods": [
                {
                  "periodId": "001",
                  "startDate": "2018-04-06",
                  "endDate": "2019-04-05",
                  "receivedDateTime": "2019-02-15T09:35:04.843Z"
                }
              ]
            }
          ]
        }
      },
      "calculation": {
        "otherIncome": {
          "totalOtherIncome": 2000.00,
          "postCessationIncome": {
            "totalPostCessationReceipts": 2000.00,
            "postCessationReceipts": [
              {
                "amount": 100.00,
                "taxYearIncomeToBeTaxed": "2019-20"
              }
            ]
          }
        }
      }
    }
    """
    )
    .as[JsObject]

  val minimumResponseCl290EnabledJson: JsObject = Json
    .parse(
      """
        |{
        |   "metadata":{
        |      "calculationId":"",
        |      "taxYear":"2017-18",
        |      "requestedBy":"",
        |      "calculationReason":"",
        |      "calculationType":"inYear",
        |      "intentToSubmitFinalDeclaration":false,
        |      "finalDeclaration":false,
        |      "periodFrom":"",
        |      "periodTo":""
        |   },
        |   "inputs":{
        |      "personalInformation":{
        |         "identifier":"",
        |         "taxRegime":"UK"
        |      },
        |      "incomeSources":{
        |
        |      }
        |   },
        |   "calculation":{
        |      "taxDeductedAtSource":{
        |         "taxTakenOffTradingIncome":2000.00
        |      }
        |   }
        |}
    """.stripMargin
    )
    .as[JsObject]

  val minimumCalculationResponseBasicRateDivergenceEnabledJson: JsObject = Json
    .parse(
      """
        |{
        |  "metadata" : {
        |    "calculationId": "",
        |    "taxYear": "2024-25",
        |    "requestedBy": "",
        |    "calculationReason": "",
        |    "calculationType": "inYear",
        |    "intentToSubmitFinalDeclaration": false,
        |    "finalDeclaration": false,
        |    "periodFrom": "",
        |    "periodTo": ""
        |  },
        |  "inputs" : {
        |    "personalInformation": {
        |       "identifier": "",
        |       "taxRegime": "UK"
        |    },
        |    "incomeSources": {}
        |  },
        |  "calculation": {
        |    "taxCalculation": {
        |      "incomeTax": {
        |        "totalIncomeReceivedFromAllSources": 50,
        |        "totalAllowancesAndDeductions": 50,
        |        "totalTaxableIncome": 50,
        |        "incomeTaxCharged": 50,
        |        "giftAidTaxChargeWhereBasicRateDiffers":2000.25
        |      },
        |      "nics": {
        |        "class2Nics": {
        |          "underSmallProfitThreshold": true,
        |          "underLowerProfitThreshold": true
        |        }
        |      },
        |      "totalIncomeTaxAndNicsDue": 50
        |    },
        |    "reliefs": {
        |      "giftAidTaxReductionWhereBasicRateDiffers": {
        |         "amount": 2000.25
        |      }
        |    }
        |  }
        |}
""".stripMargin
    )
    .as[JsObject]

  val minimumCalculationResponseWithSwitchesDisabledJson: JsObject = Json
    .parse(
      """
        |{
        |  "metadata" : {
        |    "calculationId": "",
        |    "taxYear": "2017-18",
        |    "requestedBy": "",
        |    "calculationReason": "",
        |    "calculationType": "inYear",
        |    "intentToSubmitFinalDeclaration": false,
        |    "finalDeclaration": false,
        |    "periodFrom": "",
        |    "periodTo": ""
        |  },
        |  "inputs" : {
        |    "personalInformation": {
        |       "identifier": "",
        |       "taxRegime": "UK"
        |    },
        |    "incomeSources": {}
        |  },
        |  "calculation": {
        |  "taxCalculation":{
        |    "incomeTax":{
        |       "totalIncomeReceivedFromAllSources":50,
        |       "totalAllowancesAndDeductions":50,
        |       "totalTaxableIncome":50,
        |       "incomeTaxCharged":50
        |    },
        |    "nics":{
        |       "class2Nics":{
        |          "underSmallProfitThreshold":true,
        |          "underLowerProfitThreshold":true
        |          }
        |        },
        |    "totalIncomeTaxAndNicsDue":50
        |   }
        |  }
        |}
    """.stripMargin
    )
    .as[JsObject]

  val emptyCalculationResponse: Def1_RetrieveCalculationResponse = Def1_RetrieveCalculationResponse(
    metadata = metadata,
    inputs = inputs,
    calculation = Some(emptyCalculation),
    messages = None
  )

  val minimumCalculationResponseJson: JsObject = Json
    .parse(
      """
        |{
        |  "metadata" : {
        |    "calculationId": "",
        |    "taxYear": "2017-18",
        |    "requestedBy": "",
        |    "calculationReason": "",
        |    "calculationType": "inYear",
        |    "intentToSubmitFinalDeclaration": false,
        |    "finalDeclaration": false,
        |    "periodFrom": "",
        |    "periodTo": ""
        |  },
        |  "inputs" : {
        |    "personalInformation": {
        |       "identifier": "",
        |       "taxRegime": "UK"
        |    },
        |    "incomeSources": {}
        |  }
        |}
    """.stripMargin
    )
    .as[JsObject]

  val emptyCalculationResponseMtdJson: JsObject = Json
    .parse(
      """
        |{
        |  "metadata" : {
        |    "calculationId": "",
        |    "taxYear": "2017-18",
        |    "requestedBy": "",
        |    "calculationReason": "",
        |    "calculationType": "inYear",
        |    "intentToSubmitFinalDeclaration": false,
        |    "finalDeclaration": false,
        |    "periodFrom": "",
        |    "periodTo": ""
        |  },
        |  "inputs" : {
        |    "personalInformation": {
        |       "identifier": "",
        |       "taxRegime": "UK"
        |    },
        |    "incomeSources": {}
        |  },
        |  "calculation" : {
        |   "endOfYearEstimate": {
        |   }
        |  }
        |}
   """.stripMargin
    )
    .as[JsObject]

  val noEOYCalculationResponseMtdJson: JsObject = Json
    .parse(
      """
        |{
        |  "metadata" : {
        |    "calculationId": "",
        |    "taxYear": "2017-18",
        |    "requestedBy": "",
        |    "calculationReason": "",
        |    "calculationType": "inYear",
        |    "intentToSubmitFinalDeclaration": false,
        |    "finalDeclaration": false,
        |    "periodFrom": "",
        |    "periodTo": ""
        |  },
        |  "inputs" : {
        |    "personalInformation": {
        |       "identifier": "",
        |       "taxRegime": "UK"
        |    },
        |    "incomeSources": {}
        |  }
        |}
   """.stripMargin
    )
    .as[JsObject]

  val noUnderLowerProfitThresholdCalculationResponseMtdJson: JsObject = Json
    .parse(
      """
        |{
        |  "metadata" : {
        |    "calculationId": "",
        |    "taxYear": "2017-18",
        |    "requestedBy": "",
        |    "calculationReason": "",
        |    "calculationType": "inYear",
        |    "intentToSubmitFinalDeclaration": false,
        |    "finalDeclaration": false,
        |    "periodFrom": "",
        |    "periodTo": ""
        |  },
        |  "inputs" : {
        |    "personalInformation": {
        |       "identifier": "",
        |       "taxRegime": "UK"
        |    },
        |    "incomeSources": {}
        |  },
        |  "calculation": {
        |    "taxCalculation":{
        |      "incomeTax":{
        |       "totalIncomeReceivedFromAllSources":50,
        |       "totalAllowancesAndDeductions":50,
        |       "totalTaxableIncome":50,
        |       "incomeTaxCharged":50
        |    },
        |    "nics":{
        |       "class2Nics":{
        |          "underSmallProfitThreshold":true
        |          }
        |    },
        |    "totalIncomeTaxAndNicsDue":50
        |    }
        |   }
        |}
   """.stripMargin
    )
    .as[JsObject]

}
