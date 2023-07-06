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

package v4.models.response.retrieveCalculation

import api.models.domain.TaxYear
import play.api.libs.json.{JsObject, JsValue, Json}
import v4.models.response.common.CalculationType.`inYear`
import v4.models.response.retrieveCalculation.calculation.Calculation
import v4.models.response.retrieveCalculation.calculation.employmentAndPensionsIncome.{EmploymentAndPensionsIncome, EmploymentAndPensionsIncomeDetail}
import v4.models.response.retrieveCalculation.calculation.endOfYearEstimate.EndOfYearEstimate
import v4.models.response.retrieveCalculation.calculation.reliefs.{BasicRateExtension, Reliefs}
import v4.models.response.retrieveCalculation.calculation.taxCalculation.{Class2Nics, IncomeTax, Nics, TaxCalculation}
import v4.models.response.retrieveCalculation.inputs.{IncomeSources, Inputs, PersonalInformation}
import v4.models.response.retrieveCalculation.metadata.Metadata

trait CalculationFixture {

  val totalBasicRateExtension      = 2000
  val totalAllowancesAndDeductions = 100
  val incomeTaxValue               = 50

  val calculationMtdJson: JsValue =
    Json.parse(getClass.getResourceAsStream("/v4/models/response/retrieveCalculation/calculation_mtd.json"))

  val calculationDownstreamJson: JsValue =
    Json.parse(getClass.getResourceAsStream("/v4/models/response/retrieveCalculation/calculation_downstream.json"))

  val reliefs: Reliefs = Reliefs(
    basicRateExtension =
      Some(BasicRateExtension(totalBasicRateExtension = Some(totalBasicRateExtension), giftAidRelief = None, pensionContributionReliefs = None)),
    residentialFinanceCosts = None,
    foreignTaxCreditRelief = None,
    topSlicingRelief = None,
    reliefsClaimed = None
  )

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

  val class2NicsWithoutUnderLowerProfitThreshold: Class2Nics = class2Nics.copy(underLowerProfitThreshold = None)

  val nics: Nics = Nics(class2Nics = Some(class2Nics), class4Nics = None, nic2NetOfDeductions = None, nic4NetOfDeductions = None, totalNic = None)
  val nicsWithoutUnderLowerProfitThreshold: Nics = nics.copy(class2Nics = Some(class2NicsWithoutUnderLowerProfitThreshold))

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
    incomeTax = incomeTax,
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

  val taxCalculationWithoutUnderLowerProfitThreshold: TaxCalculation = taxCalculation.copy(nics = Some(nicsWithoutUnderLowerProfitThreshold))

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
    dividendsIncome = None,
    incomeSummaryTotals = None,
    taxCalculation = Some(taxCalculation),
    previousCalculation = None,
    endOfYearEstimate = Some(eoyEstimates),
    lossesAndClaims = None
  )

  val calculationWithR8BDisabled: Calculation =
    calculationWithR8BData.copy(employmentAndPensionsIncome = None, endOfYearEstimate = None, reliefs = None)

  val metadata: Metadata = Metadata(
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

  val inputs: Inputs = Inputs(
    PersonalInformation("", None, "UK", None, None, None, None, None),
    IncomeSources(None, None),
    None,
    None,
    None,
    None,
    None,
    None,
    None
  )

  val minimalCalculationResponse: RetrieveCalculationResponse = RetrieveCalculationResponse(
    metadata = metadata,
    inputs = inputs,
    calculation = Some(calculationWithR8BData),
    messages = None
  )

  // @formatter:off
  val emptyCalculation: Calculation = Calculation(
    None, None, None, None, None, None,
    None, None, None, None, None, None,
    None, None, None, None, None, None,
    None, None, None, None, None, None,
    None, None, None, None)

  val calcWithoutEndOfYearEstimate: Calculation = emptyCalculation.copy(reliefs= Some(reliefs),employmentAndPensionsIncome = Some(employmentAndPensionsIncome), taxCalculation = Some(taxCalculation))


  val calcWithoutBasicExtension: Calculation = emptyCalculation.copy(endOfYearEstimate =  Some(eoyEstimates), employmentAndPensionsIncome = Some(employmentAndPensionsIncome), taxCalculation = Some(taxCalculation))

 val calcWithoutOffPayrollWorker: Calculation= emptyCalculation.copy(reliefs= Some(reliefs),endOfYearEstimate =  Some(eoyEstimates), taxCalculation = Some(taxCalculation))
  val calcWithoutUnderLowerProfitThreshold: Calculation= emptyCalculation.copy(taxCalculation=Some(taxCalculationWithoutUnderLowerProfitThreshold),  reliefs= Some(reliefs),endOfYearEstimate =  Some(eoyEstimates), employmentAndPensionsIncome = Some(employmentAndPensionsIncome))
  // @formatter:on

  val minimalCalculationResponseWithoutR8BData: RetrieveCalculationResponse = RetrieveCalculationResponse(
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
        |   },
        |  "employmentAndPensionsIncome":{
        |       "employmentAndPensionsIncomeDetail": [
        |          { "offPayrollWorker": true }
        |      ]
        |    },
        |    "reliefs": {
        |       "basicRateExtension": {
        |       "totalBasicRateExtension": 2000.00
        |       }
        |    },
        |    "endOfYearEstimate": {
        |       "totalAllowancesAndDeductions": 100
        |     }
        |  }
        |}
    """.stripMargin
    )
    .as[JsObject]

  val minimumCalculationResponseWithR8BDisabledJson: JsObject = Json
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

  val emptyCalculationResponse: RetrieveCalculationResponse = RetrieveCalculationResponse(
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
