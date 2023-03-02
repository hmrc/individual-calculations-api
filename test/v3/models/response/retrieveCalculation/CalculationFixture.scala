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

package v3.models.response.retrieveCalculation

import play.api.libs.json.{JsObject, JsValue, Json}
import v3.models.domain.TaxYear
import v3.models.response.common.CalculationType.`inYear`
import v3.models.response.retrieveCalculation.calculation.Calculation
import v3.models.response.retrieveCalculation.calculation.reliefs.{BasicRateExtension, Reliefs}
import v3.models.response.retrieveCalculation.inputs.{IncomeSources, Inputs, PersonalInformation}
import v3.models.response.retrieveCalculation.metadata.Metadata

trait CalculationFixture {

  val calculationMtdJson: JsValue =
    Json.parse(getClass.getResourceAsStream("/v3/models/response/retrieveCalculation/calculation_mtd.json"))

  val calculationDownstreamJson: JsValue =
    Json.parse(getClass.getResourceAsStream("/v3/models/response/retrieveCalculation/calculation_downstream.json"))

  val totalBasicRateExtension = 2000

  val reliefs = Reliefs(
    basicRateExtension =
      Some(BasicRateExtension(totalBasicRateExtension = Some(totalBasicRateExtension), giftAidRelief = None, pensionContributionReliefs = None)),
    residentialFinanceCosts = None,
    foreignTaxCreditRelief = None,
    topSlicingRelief = None,
    reliefsClaimed = None
  )

  val calculationWithBasicRateExtension = Calculation(
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
    employmentAndPensionsIncome = None,
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
    taxCalculation = None,
    previousCalculation = None,
    endOfYearEstimate = None,
    lossesAndClaims = None
  )

  val minimalCalculationResponse: RetrieveCalculationResponse = RetrieveCalculationResponse(
    metadata = Metadata(
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
    ),
    inputs = Inputs(
      PersonalInformation("", None, "UK", None, None, None, None, None),
      IncomeSources(None, None),
      None,
      None,
      None,
      None,
      None,
      None,
      None
    ),
    calculation = Some(calculationWithBasicRateExtension),
    messages = None
  )

  val minimalCalculationResponseWithoutBasicRateExtension: RetrieveCalculationResponse = RetrieveCalculationResponse(
    metadata = Metadata(
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
    ),
    inputs = Inputs(
      PersonalInformation("", None, "UK", None, None, None, None, None),
      IncomeSources(None, None),
      None,
      None,
      None,
      None,
      None,
      None,
      None
    ),
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
        |    "reliefs": {
        |       "basicRateExtension": {
        |       "totalBasicRateExtension": 2000.00
        |       }
        |    }
        |  }
        |}
  """.stripMargin
    )
    .as[JsObject]

  val minimumCalculationResponseWithBasicRateExtensionMtdJson: JsObject = Json
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
      |    "reliefs": {
      |       "basicRateExtension": {
      |       "totalBasicRateExtension": 2000.00
      |       }
      |    }
      |  }
      |}
    """.stripMargin
    )
    .as[JsObject]

  val minimumCalculationResponseWithoutBasicRateExtensionMtdJson: JsObject = Json
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

}
