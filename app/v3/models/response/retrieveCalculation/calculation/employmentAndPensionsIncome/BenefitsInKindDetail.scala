/*
 * Copyright 2022 HM Revenue & Customs
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

package v3.models.response.retrieveCalculation.calculation.employmentAndPensionsIncome

import play.api.libs.json.{JsPath, Json, OFormat, OWrites, Reads}
import play.api.libs.functional.syntax._


case class BenefitsInKindDetail(apportionedAccommodation: Option[BigDecimal],
                                apportionedAssets: Option[BigDecimal],
                                apportionedAssetTransfer: Option[BigDecimal],
                                apportionedBeneficialLoan: Option[BigDecimal],
                                apportionedCar: Option[BigDecimal],
                                apportionedCarFuel: Option[BigDecimal],
                                apportionedEducationalServices: Option[BigDecimal],
                                apportionedEntertaining: Option[BigDecimal],
                                apportionedExpenses: Option[BigDecimal],
                                apportionedMedicalInsurance: Option[BigDecimal],
                                apportionedTelephone: Option[BigDecimal],
                                apportionedService: Option[BigDecimal],
                                apportionedTaxableExpenses: Option[BigDecimal],
                                apportionedVan: Option[BigDecimal],
                                apportionedVanFuel: Option[BigDecimal],
                                apportionedMileage: Option[BigDecimal],
                                apportionedNonQualifyingRelocationExpenses: Option[BigDecimal],
                                apportionedNurseryPlaces: Option[BigDecimal],
                                apportionedOtherItems: Option[BigDecimal],
                                apportionedPaymentsOnEmployeesBehalf: Option[BigDecimal],
                                apportionedPersonalIncidentalExpenses: Option[BigDecimal],
                                apportionedQualifyingRelocationExpenses: Option[BigDecimal],
                                apportionedEmployerProvidedProfessionalSubscriptions: Option[BigDecimal],
                                apportionedEmployerProvidedServices: Option[BigDecimal],
                                apportionedIncomeTaxPaidByDirector: Option[BigDecimal],
                                apportionedTravelAndSubsistence: Option[BigDecimal],
                                apportionedVouchersAndCreditCards: Option[BigDecimal],
                                apportionedNonCash: Option[BigDecimal]
                               )

object BenefitsInKindDetail {

  case class BenefitsInKindDetailPart1(apportionedAccommodation: Option[BigDecimal],
                                       apportionedAssets: Option[BigDecimal],
                                       apportionedAssetTransfer: Option[BigDecimal],
                                       apportionedBeneficialLoan: Option[BigDecimal],
                                       apportionedCar: Option[BigDecimal],
                                       apportionedCarFuel: Option[BigDecimal],
                                       apportionedEducationalServices: Option[BigDecimal],
                                       apportionedEntertaining: Option[BigDecimal],
                                       apportionedExpenses: Option[BigDecimal],
                                       apportionedMedicalInsurance: Option[BigDecimal],
                                       apportionedTelephone: Option[BigDecimal],
                                       apportionedService: Option[BigDecimal],
                                       apportionedTaxableExpenses: Option[BigDecimal],
                                       apportionedVan: Option[BigDecimal],
                                       apportionedVanFuel: Option[BigDecimal]
                                 )

  case class BenefitsInKindDetailPart2(
                                   apportionedMileage: Option[BigDecimal],
                                   apportionedNonQualifyingRelocationExpenses: Option[BigDecimal],
                                   apportionedNurseryPlaces: Option[BigDecimal],
                                   apportionedOtherItems: Option[BigDecimal],
                                   apportionedPaymentsOnEmployeesBehalf: Option[BigDecimal],
                                   apportionedPersonalIncidentalExpenses: Option[BigDecimal],
                                   apportionedQualifyingRelocationExpenses: Option[BigDecimal],
                                   apportionedEmployerProvidedProfessionalSubscriptions: Option[BigDecimal],
                                   apportionedEmployerProvidedServices: Option[BigDecimal],
                                   apportionedIncomeTaxPaidByDirector: Option[BigDecimal],
                                   apportionedTravelAndSubsistence: Option[BigDecimal],
                                   apportionedVouchersAndCreditCards: Option[BigDecimal],
                                   apportionedNonCash: Option[BigDecimal]
                                  )

  private val formatPt1: OFormat[BenefitsInKindDetailPart1] = Json.format[BenefitsInKindDetailPart1]
  private val formatPt2: OFormat[BenefitsInKindDetailPart2] = Json.format[BenefitsInKindDetailPart2]

  private def buildBenefitsObject(part1: BenefitsInKindDetailPart1, part2: BenefitsInKindDetailPart2): BenefitsInKindDetail = {
    import part1._
    import part2._

    BenefitsInKindDetail(
      apportionedAccommodation = apportionedAccommodation,
      apportionedAssets = apportionedAssets,
      apportionedAssetTransfer = apportionedAssetTransfer,
      apportionedBeneficialLoan = apportionedBeneficialLoan,
      apportionedCar = apportionedCar,
      apportionedCarFuel = apportionedCarFuel,
      apportionedEducationalServices = apportionedEducationalServices,
      apportionedEntertaining = apportionedEntertaining,
      apportionedExpenses = apportionedExpenses,
      apportionedMedicalInsurance = apportionedMedicalInsurance,
      apportionedTelephone = apportionedTelephone,
      apportionedService = apportionedService,
      apportionedTaxableExpenses = apportionedTaxableExpenses,
      apportionedVan = apportionedVan,
      apportionedVanFuel = apportionedVanFuel,
      apportionedMileage = apportionedMileage,
      apportionedNonQualifyingRelocationExpenses = apportionedNonQualifyingRelocationExpenses,
      apportionedNurseryPlaces = apportionedNurseryPlaces,
      apportionedOtherItems = apportionedOtherItems,
      apportionedPaymentsOnEmployeesBehalf = apportionedPaymentsOnEmployeesBehalf,
      apportionedPersonalIncidentalExpenses = apportionedPersonalIncidentalExpenses,
      apportionedQualifyingRelocationExpenses = apportionedQualifyingRelocationExpenses,
      apportionedEmployerProvidedProfessionalSubscriptions = apportionedEmployerProvidedProfessionalSubscriptions,
      apportionedEmployerProvidedServices = apportionedEmployerProvidedServices,
      apportionedIncomeTaxPaidByDirector = apportionedIncomeTaxPaidByDirector,
      apportionedTravelAndSubsistence = apportionedTravelAndSubsistence,
      apportionedVouchersAndCreditCards = apportionedVouchersAndCreditCards,
      apportionedNonCash = apportionedNonCash
    )
  }

  private def splitBenefitsObject(o: BenefitsInKindDetail): (BenefitsInKindDetailPart1, BenefitsInKindDetailPart2) = {
    import o._

    (
      BenefitsInKindDetailPart1(
        apportionedAccommodation = apportionedAccommodation,
        apportionedAssets = apportionedAssets,
        apportionedAssetTransfer = apportionedAssetTransfer,
        apportionedBeneficialLoan = apportionedBeneficialLoan,
        apportionedCar = apportionedCar,
        apportionedCarFuel = apportionedCarFuel,
        apportionedEducationalServices = apportionedEducationalServices,
        apportionedEntertaining = apportionedEntertaining,
        apportionedExpenses = apportionedExpenses,
        apportionedMedicalInsurance = apportionedMedicalInsurance,
        apportionedTelephone = apportionedTelephone,
        apportionedService = apportionedService,
        apportionedTaxableExpenses = apportionedTaxableExpenses,
        apportionedVan = apportionedVan,
        apportionedVanFuel = apportionedVanFuel
      ),
      BenefitsInKindDetailPart2(
        apportionedMileage = apportionedMileage,
        apportionedNonQualifyingRelocationExpenses = apportionedNonQualifyingRelocationExpenses,
        apportionedNurseryPlaces = apportionedNurseryPlaces,
        apportionedOtherItems = apportionedOtherItems,
        apportionedPaymentsOnEmployeesBehalf = apportionedPaymentsOnEmployeesBehalf,
        apportionedPersonalIncidentalExpenses = apportionedPersonalIncidentalExpenses,
        apportionedQualifyingRelocationExpenses = apportionedQualifyingRelocationExpenses,
        apportionedEmployerProvidedProfessionalSubscriptions = apportionedEmployerProvidedProfessionalSubscriptions,
        apportionedEmployerProvidedServices = apportionedEmployerProvidedServices,
        apportionedIncomeTaxPaidByDirector = apportionedIncomeTaxPaidByDirector,
        apportionedTravelAndSubsistence = apportionedTravelAndSubsistence,
        apportionedVouchersAndCreditCards = apportionedVouchersAndCreditCards,
        apportionedNonCash = apportionedNonCash
      )
    )
  }


  implicit val writes: OWrites[BenefitsInKindDetail] = (o: BenefitsInKindDetail) => {
    val splitData = BenefitsInKindDetail.splitBenefitsObject(o)
    Json.toJsObject(splitData._1)(formatPt1) ++ Json.toJsObject(splitData._2)(formatPt2)
  }

  implicit val reads: Reads[BenefitsInKindDetail] = (
    JsPath.read[BenefitsInKindDetailPart1](formatPt1) and
      JsPath.read[BenefitsInKindDetailPart2](formatPt2)
    )(BenefitsInKindDetail.buildBenefitsObject _)
}
