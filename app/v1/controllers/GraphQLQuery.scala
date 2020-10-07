/*
 * Copyright 2020 HM Revenue & Customs
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

package v1.controllers

trait GraphQLQuery {

  val query: String

  val METADATA_QUERY: String =
    """
      |{
      |  metadata {
      |    id
      |    taxYear
      |    requestedBy
      |    calculationReason
      |    calculationTimestamp
      |    calculationType
      |    intentToCrystallise
      |    crystallised
      |    totalIncomeTaxAndNicsDue
      |    calculationErrorCount
      |  }
      |}
      |""".stripMargin

  val ALLOWANCES_AND_DEDUCTIONS_QUERY: String =
    """
      |{
      |  metadata {
      |    id
      |    calculationErrorCount
      |  }
      |  allowancesDeductionsAndReliefs {
      |    summary {
      |      totalAllowancesAndDeductions
      |      totalReliefs
      |    }
      |    detail {
      |      allowancesAndDeductions {
      |        personalAllowance
      |        reducedPersonalAllowance
      |        giftOfInvestmentsAndPropertyToCharity
      |        blindPersonsAllowance
      |        lossesAppliedToGeneralIncome
      |      }
      |      reliefs {
      |        residentialFinanceCosts {
      |          amountClaimed
      |          allowableAmount
      |          rate
      |          propertyFinanceRelief
      |        }
      |      }
      |    }
      |  }
      |}
      |""".stripMargin

  val EOY_ESTIMATE_QUERY: String =
    """
      |{
      |  metadata {
      |    id
      |    calculationType
      |    calculationErrorCount
      |  }
      |  endOfYearEstimate {
      |    summary {
      |      totalEstimatedIncome
      |      totalTaxableIncome
      |      incomeTaxAmount
      |      nic2
      |      nic4
      |      totalNicAmount
      |      totalStudentLoansRepaymentAmount
      |      totalAnnualPaymentsTaxCharged
      |      totalRoyaltyPaymentsTaxCharged
      |      totalTaxDeducted
      |      incomeTaxNicAmount
      |    }
      |    detail {
      |      selfEmployments {
      |        selfEmploymentId
      |        taxableIncome
      |        finalised
      |      }
      |      ukPropertyFhl {
      |        taxableIncome
      |        finalised
      |      }
      |      ukPropertyNonFhl {
      |        taxableIncome
      |        finalised
      |      }
      |      ukSavings {
      |        savingsAccountId
      |        savingsAccountName
      |        taxableIncome
      |      }
      |      ukDividends {
      |        taxableIncome
      |      }
      |    }
      |  }
      |}
      |""".stripMargin

  val INCOME_TAX_AND_NICS_QUERY: String =
    """
      |{
      |  metadata {
      |    id
      |    calculationErrorCount
      |  }
      |  incomeTaxAndNicsCalculated {
      |    summary {
      |      incomeTax {
      |        incomeTaxCharged
      |        incomeTaxDueAfterReliefs
      |        incomeTaxDueAfterGiftAid
      |      }
      |      nics {
      |        class2NicsAmount
      |        class4NicsAmount
      |        totalNic
      |      }
      |      totalIncomeTaxNicsCharged
      |      totalTaxDeducted
      |      totalIncomeTaxAndNicsDue
      |      taxRegime
      |    }
      |    detail {
      |      incomeTax {
      |        payPensionsProfit {
      |          allowancesAllocated
      |          incomeTaxAmount
      |          taxBands {
      |            name
      |            rate
      |            bandLimit
      |            apportionedBandLimit
      |            income
      |            taxAmount
      |          }
      |        }
      |        savingsAndGains {
      |          allowancesAllocated
      |          incomeTaxAmount
      |          taxBands {
      |            name
      |            rate
      |            bandLimit
      |            apportionedBandLimit
      |            income
      |            taxAmount
      |          }
      |        }
      |        dividends {
      |          allowancesAllocated
      |          incomeTaxAmount
      |          taxBands {
      |            name
      |            rate
      |            bandLimit
      |            apportionedBandLimit
      |            income
      |            taxAmount
      |          }
      |        }
      |        giftAid {
      |          grossGiftAidPayments
      |          rate
      |          giftAidTax
      |        }
      |      }
      |      nics {
      |        class2Nics {
      |          weeklyRate
      |          weeks
      |          limit
      |          apportionedLimit
      |          underSmallProfitThreshold
      |          actualClass2Nic
      |        }
      |        class4Nics {
      |          class4Losses {
      |            totalClass4LossesAvailable
      |            totalClass4LossesUsed
      |            totalClass4LossesCarriedForward
      |          }
      |          totalIncomeLiableToClass4Charge
      |          totalIncomeChargeableToClass4
      |          class4NicBands {
      |            name
      |            rate
      |            threshold
      |            apportionedThreshold
      |            income
      |            amount
      |          }
      |        }
      |      }
      |      taxDeductedAtSource {
      |        ukLandAndProperty
      |        savings
      |        cis
      |      }
      |    }
      |  }
      |}
      |""".stripMargin

  val MESSAGES_QUERY: String =
    """
      |{
      |  metadata {
      |    id
      |  }
      |  messages {
      |    info {
      |      id
      |      text
      |    }
      |    warnings {
      |      id
      |      text
      |    }
      |    errors {
      |      id
      |      text
      |    }
      |  }
      |}
      |""".stripMargin

  val TAXABLE_INCOME_QUERY: String =
    """
      |{
      |  metadata {
      |    id
      |    calculationErrorCount
      |  }
      |  taxableIncome {
      |    summary {
      |      totalIncomeReceivedFromAllSources
      |      totalTaxableIncome
      |    }
      |    detail {
      |      payPensionsProfit {
      |        incomeReceived
      |        taxableIncome
      |        totalSelfEmploymentProfit
      |        totalPropertyProfit
      |        totalFHLPropertyProfit
      |        totalUKOtherPropertyProfit
      |        businessProfitAndLoss {
      |          selfEmployments {
      |            selfEmploymentId
      |            totalIncome
      |            totalExpenses
      |            netProfit
      |            netLoss
      |            class4Loss
      |            totalAdditions
      |            totalDeductions
      |            accountingAdjustments
      |            adjustedIncomeTaxLoss
      |            taxableProfit
      |            taxableProfitAfterIncomeTaxLossesDeduction
      |            lossClaimsSummary {
      |              totalBroughtForwardIncomeTaxLosses
      |              broughtForwardIncomeTaxLossesUsed
      |              carrySidewaysIncomeTaxLossesUsed
      |              totalIncomeTaxLossesCarriedForward
      |              totalBroughtForwardClass4Losses
      |              broughtForwardClass4LossesUsed
      |              carrySidewaysClass4LossesUsed
      |              totalClass4LossesCarriedForward
      |            }
      |            lossClaimsDetail {
      |              lossesBroughtForward {
      |                lossType
      |                taxYearLossIncurred
      |                currentLossValue
      |                mtdLoss
      |              }
      |              resultOfClaimsApplied {
      |                claimId
      |                taxYearClaimMade
      |                claimType
      |                mtdLoss
      |                taxYearLossIncurred
      |                lossAmountUsed
      |                remainingLossValue
      |                lossType
      |              }
      |              unclaimedLosses {
      |                taxYearLossIncurred
      |                currentLossValue
      |                lossType
      |              }
      |              carriedForwardLosses {
      |                claimId
      |                claimType
      |                taxYearClaimMade
      |                taxYearLossIncurred
      |                currentLossValue
      |                lossType
      |              }
      |              claimsNotApplied {
      |                claimId
      |                taxYearClaimMade
      |                claimType
      |              }
      |            }
      |            bsas {
      |              bsasId
      |              applied
      |            }
      |          }
      |          ukPropertyFhl {
      |            totalIncome
      |            totalExpenses
      |            netProfit
      |            netLoss
      |            totalAdditions
      |            totalDeductions
      |            accountingAdjustments
      |            adjustedIncomeTaxLoss
      |            taxableProfit
      |            taxableProfitAfterIncomeTaxLossesDeduction
      |            lossClaimsSummary {
      |              lossForCSFHL
      |              totalBroughtForwardIncomeTaxLosses
      |              broughtForwardIncomeTaxLossesUsed
      |              totalIncomeTaxLossesCarriedForward
      |            }
      |            lossClaimsDetail {
      |              lossesBroughtForward {
      |                taxYearLossIncurred
      |                currentLossValue
      |                mtdLoss
      |              }
      |              resultOfClaimsApplied {
      |                claimId
      |                taxYearClaimMade
      |                claimType
      |                mtdLoss
      |                taxYearLossIncurred
      |                lossAmountUsed
      |                remainingLossValue
      |              }
      |              defaultCarriedForwardLosses {
      |                taxYearLossIncurred
      |                currentLossValue
      |              }
      |            }
      |            bsas {
      |              bsasId
      |              applied
      |            }
      |          }
      |          ukPropertyNonFhl {
      |            totalIncome
      |            totalExpenses
      |            netProfit
      |            netLoss
      |            totalAdditions
      |            totalDeductions
      |            accountingAdjustments
      |            adjustedIncomeTaxLoss
      |            taxableProfit
      |            taxableProfitAfterIncomeTaxLossesDeduction
      |            lossClaimsSummary {
      |              totalBroughtForwardIncomeTaxLosses
      |              broughtForwardIncomeTaxLossesUsed
      |              carrySidewaysIncomeTaxLossesUsed
      |              totalIncomeTaxLossesCarriedForward
      |              broughtForwardCarrySidewaysIncomeTaxLossesUsed
      |            }
      |            lossClaimsDetail {
      |              lossesBroughtForward {
      |                taxYearLossIncurred
      |                currentLossValue
      |                mtdLoss
      |              }
      |              resultOfClaimsApplied {
      |                claimId
      |                originatingClaimId
      |                taxYearClaimMade
      |                claimType
      |                mtdLoss
      |                taxYearLossIncurred
      |                lossAmountUsed
      |                remainingLossValue
      |              }
      |              defaultCarriedForwardLosses {
      |                taxYearLossIncurred
      |                currentLossValue
      |              }
      |              claimsNotApplied {
      |                claimId
      |                taxYearClaimMade
      |                claimType
      |              }
      |            }
      |            bsas {
      |              bsasId
      |              applied
      |            }
      |          }
      |        }
      |      }
      |      savingsAndGains {
      |        incomeReceived
      |        taxableIncome
      |        ukSavings {
      |          savingsAccountId
      |          savingsAccountName
      |          grossIncome
      |          netIncome
      |          taxDeducted
      |        }
      |      }
      |      dividends {
      |        incomeReceived
      |        taxableIncome
      |      }
      |    }
      |  }
      |}
      |""".stripMargin
}
