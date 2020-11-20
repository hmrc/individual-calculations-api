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
      |    metadataExistence {
      |      incomeTaxAndNicsCalculated
      |      messages
      |      taxableIncome
      |      endOfYearEstimate
      |      allowancesDeductionsAndReliefs
      |    }
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
      |        qualifyingLoanInterestFromInvestments
      |        postCessationTradeReceipts
      |        paymentsToTradeUnionsForDeathBenefits
      |        annualPayments {
      |          grossAnnualPayments
      |          reliefClaimed
      |          rate
      |        }
      |        pensionContributions {
      |          totalPensionContributions
      |          retirementAnnuityPayments
      |          paymentToEmployersSchemeNoTaxRelief
      |          overseasPensionSchemeContributions
      |        }
      |      }
      |      reliefs {
      |        residentialFinanceCosts {
      |          amountClaimed
      |          allowableAmount
      |          rate
      |          propertyFinanceRelief
      |        }
      |        foreignTaxCreditRelief {
      |          incomeSourceType
      |          incomeSourceId
      |          countryCode
      |          allowableAmount
      |          rate
      |          amountUsed
      |        }
      |        pensionContributionReliefs {
      |          totalPensionContributionReliefs
      |          regularPensionContributions
      |          oneOffPensionContributionsPaid
      |        }
      |        reliefsClaimed {
      |          type
      |          amountClaimed
      |          allowableAmount
      |          amountUsed
      |          rate
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
      |      otherDividends {
      |        taxableIncome
      |      }
      |      stateBenefits {
      |        taxableIncome
      |      }
      |      ukSecurities {
      |        taxableIncome
      |      }
      |      foreignProperty {
      |        taxableIncome
      |        finalised
      |      }
      |      foreignInterest {
      |        taxableIncome
      |      }
      |    }
      |  }
      |}
      |""".stripMargin

  val INCOME_TAX_AND_NICS_QUERY: String =
    """
      |fragment incomeTypeBreakdown on IncomeTypeBreakdown {
      |  allowancesAllocated
      |  incomeTaxAmount
      |  taxBands {
      |    name
      |    rate
      |    bandLimit
      |    apportionedBandLimit
      |    income
      |    taxAmount
      |  }
      |}
      |
      |fragment pensionTypeBreakdown on PensionTypeBreakdown {
      |  amount
      |  taxPaid
      |  rate
      |  chargeableAmount
      |}
      |
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
      |        totalNotionalTax
      |        totalPensionSavingsTaxCharges
      |        statePensionLumpSumCharges
      |        incomeTaxDueAfterTaxReductions
      |        totalIncomeTaxDue
      |      }
      |      nics {
      |        class2NicsAmount
      |        class4NicsAmount
      |        totalNic
      |      }
      |      totalStudentLoansRepaymentAmount
      |      totalAnnualPaymentsTaxCharged
      |      totalRoyaltyPaymentsTaxCharged
      |      totalIncomeTaxNicsCharged
      |      totalTaxDeducted
      |      totalIncomeTaxAndNicsDue
      |      taxRegime
      |    }
      |    detail {
      |      incomeTax {
      |        payPensionsProfit {
      |          ...incomeTypeBreakdown
      |        }
      |        savingsAndGains {
      |          ...incomeTypeBreakdown
      |        }
      |        lumpSums {
      |          ...incomeTypeBreakdown
      |        }
      |        dividends {
      |          ...incomeTypeBreakdown
      |        }
      |        gainsOnLifePolicies {
      |          ...incomeTypeBreakdown
      |        }
      |        giftAid {
      |          grossGiftAidPayments
      |          rate
      |          giftAidTax
      |        }
      |      }
      |      studentLoans {
      |        planType
      |        studentLoanTotalIncomeAmount
      |        studentLoanChargeableIncomeAmount
      |        studentLoanRepaymentAmount
      |        studentLoanDeductionsFromEmployment
      |        studentLoanRepaymentAmountNetOfDeductions
      |        studentLoanApportionedIncomeThreshold
      |        studentLoanRate
      |      }
      |      pensionSavingsTaxCharges {
      |        totalPensionCharges
      |        totalTaxPaid
      |        totalPensionChargesDue
      |        pensionSavingsTaxChargesDetail {
      |          lumpSumBenefitTakenInExcessOfLifetimeAllowance {
      |            ...pensionTypeBreakdown
      |          }
      |          benefitInExcessOfLifetimeAllowance {
      |            ...pensionTypeBreakdown
      |          }
      |          pensionSchemeUnauthorisedPaymentsSurcharge {
      |            ...pensionTypeBreakdown
      |          }
      |          pensionSchemeUnauthorisedPaymentsNonSurcharge {
      |            ...pensionTypeBreakdown
      |          }
      |          pensionSchemeOverseasTransfers {
      |            transferCharge
      |            transferChargeTaxPaid
      |            rate
      |            chargeableAmount
      |          }
      |          pensionContributionsInExcessOfTheAnnualAllowance {
      |            totalContributions
      |            totalPensionCharge
      |            annualAllowanceTaxPaid
      |            totalPensionChargeDue
      |            pensionBands {
      |              name
      |              rate
      |              bandLimit
      |              apportionedBandLimit
      |              contributionAmount
      |              pensionCharge
      |            }
      |          }
      |          overseasPensionContributions {
      |            totalShortServiceRefund
      |            totalShortServiceRefundCharge
      |            shortServiceRefundTaxPaid
      |            totalShortServiceRefundChargeDue
      |            shortServiceRefundBands {
      |              name
      |              rate
      |              bandLimit
      |              apportionedBandLimit
      |              shortServiceRefundAmount
      |              shortServiceRefundCharge
      |            }
      |          }
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
      |        securities
      |        voidedIsa
      |        payeEmployments
      |        occupationalPensions
      |        stateBenefits
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
      |        totalForeignPropertyProfit
      |        totalEeaFhlProfit
      |        totalOccupationalPensionIncome
      |        totalStateBenefitsIncome
      |        totalBenefitsInKind
      |        totalPayeEmploymentAndLumpSumIncome
      |        totalEmploymentExpenses
      |        totalEmploymentIncome
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
      |          eeaPropertyFhl {
      |            totalIncome
      |            totalExpenses
      |            netProfit
      |            netLoss
      |            totalAdditions
      |            totalDeductions
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
      |          foreignProperty {
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
      |        ukSecurities {
      |          ukSecuritiesAccountId
      |          ukSecuritiesAccountName
      |          grossIncome
      |          netIncome
      |          taxDeducted
      |        }
      |      }
      |      dividends {
      |        incomeReceived
      |        taxableIncome
      |      }
      |      lumpSums {
      |        incomeReceived
      |        taxableIncome
      |      }
      |      gainsOnLifePolicies {
      |        incomeReceived
      |        taxableIncome
      |      }
      |    }
      |  }
      |}
      |""".stripMargin
}
