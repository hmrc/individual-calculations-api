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

package v1.fixtures.getTaxableIncome.detail.ukPropertyFhl

import play.api.libs.json.{JsValue, Json}
import v1.models.response.getTaxableIncome.detail.ukPropertyFhl.{LossClaimsDetail, LossClaimsSummary, UkPropertyFhl}
import v1.models.response.getTaxableIncome.detail.ukPropertyFhl.detail.{DefaultCarriedForwardLoss, LossBroughtForward, ResultOfClaimApplied}

object UkPropertyFhlFixtures {

  val ukPropertyFhlObject = UkPropertyFhl(Some(1000.00),
    Some(1000.00), Some(1000.00), Some(1000.00), Some(1000.00),
    Some(1000.00), Some(1000.00), None, Some(1000), None,
    Some(LossClaimsSummary(Some(1000), None, None, None)),
    Some(LossClaimsDetail(Some(List(LossBroughtForward("2054-55", 1000, mtdLoss = true))),
      Some(List(ResultOfClaimApplied(Some("CCIS12345678901"), "2038-39",
        "carry-forward", mtdLoss = true, "2050-51", 1000, 1000))),
      Some(List(DefaultCarriedForwardLoss("2026-27", 1000))))))

  val ukPropertyFhlWithOutLossClaimsDetailObject = UkPropertyFhl(Some(1000.00),
    Some(1000.00), Some(1000.00), Some(1000.00), Some(1000.00),
    Some(1000.00), Some(1000.00), None, Some(1000), None,
    Some(LossClaimsSummary(Some(1000), None, None, None)), None)

  val emptyUkPropertyFhl = UkPropertyFhl(None, None, None, None, None, None, None, None, None, None, None, None)

  val mtdUkPropertyFhlObj: JsValue = Json.parse(
    """
      |{
      |	"totalIncome": 1000,
      |	"totalExpenses": 1000,
      |	"netProfit": 1000,
      |	"netLoss": 1000,
      |	"totalAdditions": 1000,
      |	"totalDeductions": 1000,
      |	"accountingAdjustments": 1000,
      |	"taxableProfit": 1000,
      |	"lossClaimsSummary": {
      |		"lossForCSFHL": 1000
      |	},
      |	"lossClaimsDetail": {
      |		"lossesBroughtForward": [{
      |			"taxYearLossIncurred": "2054-55",
      |			"currentLossValue": 1000,
      |			"mtdLoss": true
      |		}],
      |		"resultOfClaimsApplied": [{
      |			"claimId": "CCIS12345678901",
      |			"taxYearClaimMade": "2038-39",
      |			"claimType": "carry-forward",
      |			"mtdLoss": true,
      |			"taxYearLossIncurred": "2050-51",
      |			"lossAmountUsed": 1000,
      |			"remainingLossValue": 1000
      |		}],
      |		"carriedForwardLosses": [{
      |			"taxYearLossIncurred": "2026-27",
      |			"currentLossValue": 1000
      |		}]
      |	}
      |}
    """.stripMargin)

  val mtdUkPropertyFhlObjWithOutLossClaimsDetail: JsValue = Json.parse(
    """
      |{
      |	"totalIncome": 1000,
      |	"totalExpenses": 1000,
      |	"netProfit": 1000,
      |	"netLoss": 1000,
      |	"totalAdditions": 1000,
      |	"totalDeductions": 1000,
      |	"accountingAdjustments": 1000,
      |	"taxableProfit": 1000,
      |	"lossClaimsSummary": {
      |		"lossForCSFHL": 1000
      |	}
      |}
    """.stripMargin)

  val desJsonWithOutLossClaimsDetail: JsValue = Json.parse(
    """{
      |"inputs": {
      |        "personalInformation": {
      |            "identifier": "VO123456A",
      |            "dateOfBirth": "2001-01-01",
      |            "taxRegime": "UK",
      |            "statePensionAgeDate": "2050-01-01"
      |        },
      |        "incomeSources": {
      |            "businessIncomeSources": [
      |                {
      |                    "incomeSourceId": "AAIS12345678901",
      |                    "incomeSourceType": "04",
      |                    "incomeSourceName": "uk property",
      |                    "accountingPeriodStartDate": "2018-01-01",
      |                    "accountingPeriodEndDate": "2019-01-01",
      |                    "source": "MTD-SA",
      |                    "latestPeriodEndDate": "2019-01-01",
      |                    "latestReceivedDateTime": "2019-08-06T11:45:01Z",
      |                    "finalised": false,
      |                    "finalisationTimestamp": "2019-02-15T09:35:15.094Z",
      |                    "submissionPeriods": [
      |                        {
      |                            "periodId": "abcdefghijk",
      |                            "startDate": "2018-01-01",
      |                            "endDate": "2019-01-01",
      |                            "receivedDateTime": "2019-02-15T09:35:04.843Z"
      |                        }
      |                    ]
      |                }
      |            ],
      |            "nonBusinessIncomeSources": [
      |                {
      |                    "incomeSourceId": "AAIS12345678902",
      |                    "incomeSourceType": "09",
      |                    "incomeSourceName": "savings account 1",
      |                    "startDate": "2018-01-01",
      |                    "endDate": "2019-01-01",
      |                    "source": "MTD-SA",
      |                    "periodId": "001",
      |                    "latestReceivedDateTime": "2019-08-06T11:45:01Z"
      |                }
      |            ]
      |        },
      |        "annualAdjustments": [
      |            {
      |                "incomeSourceId": "AAIS12345678903",
      |                "incomeSourceType": "04",
      |                "bissId": "12345678",
      |                "receivedDateTime": "2019-07-17T08:15:28Z",
      |                "applied": false
      |            }
      |        ],
      |        "lossesBroughtForward": [
      |            {
      |                "lossId": "0yriP9QrW2jTa6n",
      |                "incomeSourceId": "AAIS12345678904",
      |                "incomeSourceType": "01",
      |                "submissionTimestamp": "2019-07-13T07:51:43Z",
      |                "lossType": "income",
      |                "taxYearLossIncurred": 2055,
      |                "currentLossValue": 1000.00,
      |                "mtdLoss": true
      |            }
      |        ],
      |        "claims": [
      |            {
      |                "claimId": "CCIS12345678901",
      |                "originatingClaimId": "LLIS12345678905",
      |                "incomeSourceId": "LLIS12345678905",
      |                "incomeSourceType": "04",
      |                "submissionTimestamp": "2019-08-13T07:51:43Z",
      |                "taxYearClaimMade": 2038,
      |                "claimType": "CF"
      |            }
      |        ]
      |    },
      |    "calculation": {
      |        "allowancesAndDeductions": {
      |            "personalAllowance": 22379293018,
      |            "reducedPersonalAllowance": 72019097299,
      |            "giftOfInvestmentsAndPropertyToCharity": 11668447864,
      |            "blindPersonsAllowance": 50089903405,
      |            "lossesAppliedToGeneralIncome": 80901594332
      |        },
      |        "reliefs": {
      |            "residentialFinanceCosts": {
      |                "amountClaimed": 6282356308,
      |                "allowableAmount": 56668463807,
      |                "rate": 2,
      |                "propertyFinanceRelief": 67923591034
      |            }
      |        },
      |        "taxDeductedAtSource": {
      |            "bbsi": 33549669917,
      |            "ukLandAndProperty": 51037177800
      |        },
      |        "giftAid": {
      |            "grossGiftAidPayments": 44059476261,
      |            "rate": 35,
      |            "giftAidTax": 57448329153
      |        },
      |        "businessProfitAndLoss": [
      |            {
      |                "incomeSourceId": "LLIS12345678908",
      |                "incomeSourceType": "04",
      |                "incomeSourceName": "abcdefghijklm",
      |                "totalIncome": 1000.00,
      |                "totalExpenses": 1000.00,
      |                "netProfit": 1000.00,
      |                "netLoss": 1000.00,
      |                "totalAdditions": 1000.00,
      |                "totalDeductions": 1000.00,
      |                "accountingAdjustments": 1000.00,
      |                "taxableProfit": 1000.00,
      |                "finalLoss": 1000.00,
      |                "totalBroughtForwardLosses": 1000.00,
      |                "lossForCSFHL": 1000.00,
      |                "broughtForwardLossesUsed": 1000.00,
      |                "taxableProfitAfterLossesDeduction": 1000.00,
      |                "totalLossesCarriedForward": 1000.00
      |            }
      |        ],
      |        "savingsAndGainsIncome": [
      |            {
      |                "incomeSourceId": "AAIS12345678909",
      |                "incomeSourceType": "09",
      |                "incomeSourceName": "dividends",
      |                "grossIncome": 49133741215,
      |                "netIncome": 25746446655,
      |                "taxDeducted": 21468592451
      |            }
      |        ],
      |        "taxCalculation": {
      |            "incomeTax": {
      |                "totalIncomeReceivedFromAllSources": 2108191510,
      |                "totalAllowancesAndDeductions": 89321074915,
      |                "totalTaxableIncome": 68088189411,
      |                "payPensionsProfit": {
      |                    "incomeReceived": 61640964595,
      |                    "allowancesAllocated": 37316373820,
      |                    "taxableIncome": 26528758114,
      |                    "incomeTaxAmount": 94116371209,
      |                    "taxBands": [
      |                        {
      |                            "name": "SSR",
      |                            "rate": 39,
      |                            "bandLimit": 97549992711,
      |                            "apportionedBandLimit": 10073393964,
      |                            "income": 30463861685,
      |                            "taxAmount": 30329561574
      |                        }
      |                    ]
      |                },
      |                "savingsAndGains": {
      |                    "incomeReceived": 57870656441,
      |                    "allowancesAllocated": 56090805229,
      |                    "taxableIncome": 35640354645,
      |                    "incomeTaxAmount": 36918514728,
      |                    "taxBands": [
      |                        {
      |                            "name": "SSR",
      |                            "rate": 42,
      |                            "bandLimit": 82525776063,
      |                            "apportionedBandLimit": 75540902862,
      |                            "income": 26490916856,
      |                            "taxAmount": 29035446015
      |                        }
      |                    ]
      |                },
      |                "dividends": {
      |                    "incomeReceived": 65400536342,
      |                    "allowancesAllocated": 21219069593,
      |                    "taxableIncome": 45348222304,
      |                    "incomeTaxAmount": 84026153149,
      |                    "taxBands": [
      |                        {
      |                            "name": "SSR",
      |                            "rate": 88,
      |                            "bandLimit": 53631705334,
      |                            "apportionedBandLimit": 23082429613,
      |                            "income": 5273766759,
      |                            "taxAmount": 28734309002
      |                        }
      |                    ]
      |                },
      |                "incomeTaxCharged": 16364761452,
      |                "totalReliefs": 45905746078,
      |                "incomeTaxDueAfterReliefs": -99999999999.99,
      |                "incomeTaxDueAfterGiftAid": 69148904014
      |            },
      |            "nics": {
      |                "class2Nics": {
      |                    "amount": 84600115559,
      |                    "weeklyRate": 90698496,
      |                    "weeks": 49391,
      |                    "limit": 23088840156,
      |                    "apportionedLimit": 40438633072,
      |                    "underSmallProfitThreshold": false,
      |                    "actualClass2Nic": false
      |                },
      |                "class4Nics": {
      |                    "totalAmount": 58586220777,
      |                    "nic4Bands": [
      |                        {
      |                            "name": "ZRT",
      |                            "rate": 1,
      |                            "threshold": 42990923205,
      |                            "apportionedThreshold": 44527507878,
      |                            "income": 12399207894,
      |                            "amount": 21849327402
      |                        }
      |                    ]
      |                },
      |                "nic2NetOfDeductions": -99999999999.99,
      |                "nic4NetOfDeductions": -99999999999.99,
      |                "totalNic": -82964148664.99
      |            },
      |            "totalIncomeTaxNicsCharged": 65062942055,
      |            "totalTaxDeducted": 49524197674,
      |            "totalIncomeTaxAndNicsDue": -99999999999.99
      |        },
      |        "previousCalculation": {
      |            "calculationTimestamp": "2019-02-15T09:35:15.094Z",
      |            "calculationId": "12345678",
      |            "totalIncomeTaxAndNicsDue": -99999999999.99,
      |            "incomeTaxNicDueThisPeriod": -99999999999.99
      |        },
      |        "endOfYearEstimate": {
      |            "incomeSource": [
      |                {
      |                    "incomeSourceId": "AaIS12345678910",
      |                    "incomeSourceType": "04",
      |                    "incomeSourceName": "Savings Account 2",
      |                    "taxableIncome": 97753343583,
      |                    "finalised": true
      |                }
      |            ],
      |			"totalEstimatedIncome": 99999999999,
      |            "totalTaxableIncome": 75848369823,
      |            "incomeTaxAmount": 85973811677,
      |            "nic2": 12587297384,
      |            "nic4": 2065566328,
      |            "totalNicAmount": 93262766933,
      |            "incomeTaxNicAmount": 692547010
      |        },
      |        "lossesAndClaims": {
      |            "resultOfClaimsApplied": [
      |                {
      |                    "claimId": "CCIS12345678901",
      |                    "originatingClaimId": "000000000000210",
      |                    "incomeSourceId": "LLIS12345678911",
      |                    "incomeSourceType": "01",
      |                    "taxYearClaimMade": 2039,
      |                    "claimType": "CF",
      |                    "mtdLoss": true,
      |                    "taxYearLossIncurred": 2051,
      |                    "lossAmountUsed": 1000.00,
      |                    "remainingLossValue": 1000.00,
      |                    "lossType": "income"
      |                }
      |            ],
      |            "unclaimedLosses": [
      |                {
      |                    "incomeSourceId": "LLIS12345678913",
      |                    "incomeSourceType": "04",
      |                    "taxYearLossIncurred": 2024,
      |                    "currentLossValue": 71438847594,
      |                    "expires": 2079,
      |                    "lossType": "income"
      |                }
      |            ],
      |            "carriedForwardLosses": [
      |                {
      |                    "claimId": "CCIS12345678911",
      |                    "originatingClaimId": "OCIS12345678901",
      |                    "incomeSourceId": "AAIS12345678901",
      |                    "incomeSourceType": "04",
      |                    "claimType": "CF",
      |                    "taxYearClaimMade": 2047,
      |                    "taxYearLossIncurred": 2045,
      |                    "currentLossValue": 49177438626,
      |                    "lossType": "income"
      |                }
      |            ],
      |            "defaultCarriedForwardLosses": [
      |                {
      |                    "incomeSourceId": "AAIS12345678912",
      |                    "incomeSourceType": "01",
      |                    "taxYearLossIncurred": 2027,
      |                    "currentLossValue": 1000.00
      |                }
      |            ],
      |            "claimsNotApplied": [
      |                {
      |                    "claimId": "CCIS12345678912",
      |                    "incomeSourceId": "AAIS12345678901",
      |                    "incomeSourceType": "04",
      |                    "taxYearClaimMade": 2046,
      |                    "claimType": "CF"
      |                }
      |            ]
      |        }
      |    }
      |}""".stripMargin)
}
