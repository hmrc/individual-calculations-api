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

import play.api.libs.json._

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
                                apportionedNonCash: Option[BigDecimal])

object BenefitsInKindDetail {

  implicit val reads: Reads[BenefitsInKindDetail] = for {
    apportionedAccommodation                             <- (JsPath \ "apportionedAccommodation").readNullable[BigDecimal]
    apportionedAssets                                    <- (JsPath \ "apportionedAssets").readNullable[BigDecimal]
    apportionedAssetTransfer                             <- (JsPath \ "apportionedAssetTransfer").readNullable[BigDecimal]
    apportionedBeneficialLoan                            <- (JsPath \ "apportionedBeneficialLoan").readNullable[BigDecimal]
    apportionedCar                                       <- (JsPath \ "apportionedCar").readNullable[BigDecimal]
    apportionedCarFuel                                   <- (JsPath \ "apportionedCarFuel").readNullable[BigDecimal]
    apportionedEducationalServices                       <- (JsPath \ "apportionedEducationalServices").readNullable[BigDecimal]
    apportionedEntertaining                              <- (JsPath \ "apportionedEntertaining").readNullable[BigDecimal]
    apportionedExpenses                                  <- (JsPath \ "apportionedExpenses").readNullable[BigDecimal]
    apportionedMedicalInsurance                          <- (JsPath \ "apportionedMedicalInsurance").readNullable[BigDecimal]
    apportionedTelephone                                 <- (JsPath \ "apportionedTelephone").readNullable[BigDecimal]
    apportionedTaxableExpenses                           <- (JsPath \ "apportionedTaxableExpenses").readNullable[BigDecimal]
    apportionedService                                   <- (JsPath \ "apportionedService").readNullable[BigDecimal]
    apportionedVan                                       <- (JsPath \ "apportionedVan").readNullable[BigDecimal]
    apportionedVanFuel                                   <- (JsPath \ "apportionedVanFuel").readNullable[BigDecimal]
    apportionedMileage                                   <- (JsPath \ "apportionedMileage").readNullable[BigDecimal]
    apportionedNonQualifyingRelocationExpenses           <- (JsPath \ "apportionedNonQualifyingRelocationExpenses").readNullable[BigDecimal]
    apportionedNurseryPlaces                             <- (JsPath \ "apportionedNurseryPlaces").readNullable[BigDecimal]
    apportionedOtherItems                                <- (JsPath \ "apportionedOtherItems").readNullable[BigDecimal]
    apportionedPaymentsOnEmployeesBehalf                 <- (JsPath \ "apportionedPaymentsOnEmployeesBehalf").readNullable[BigDecimal]
    apportionedPersonalIncidentalExpenses                <- (JsPath \ "apportionedPersonalIncidentalExpenses").readNullable[BigDecimal]
    apportionedQualifyingRelocationExpenses              <- (JsPath \ "apportionedQualifyingRelocationExpenses").readNullable[BigDecimal]
    apportionedEmployerProvidedProfessionalSubscriptions <- (JsPath \ "apportionedEmployerProvidedProfessionalSubscriptions").readNullable[BigDecimal]
    apportionedEmployerProvidedServices                  <- (JsPath \ "apportionedEmployerProvidedServices").readNullable[BigDecimal]
    apportionedIncomeTaxPaidByDirector                   <- (JsPath \ "apportionedIncomeTaxPaidByDirector").readNullable[BigDecimal]
    apportionedTravelAndSubsistence                      <- (JsPath \ "apportionedTravelAndSubsistence").readNullable[BigDecimal]
    apportionedVouchersAndCreditCards                    <- (JsPath \ "apportionedVouchersAndCreditCards").readNullable[BigDecimal]
    apportionedNonCash                                   <- (JsPath \ "apportionedNonCash").readNullable[BigDecimal]

  } yield {
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

  implicit val writes: OWrites[BenefitsInKindDetail] = (o: BenefitsInKindDetail) => {
    JsObject(
      Map(
        "apportionedAccommodation"                             -> Json.toJson(o.apportionedAccommodation),
        "apportionedAssets"                                    -> Json.toJson(o.apportionedAssets),
        "apportionedAssetTransfer"                             -> Json.toJson(o.apportionedAssetTransfer),
        "apportionedBeneficialLoan"                            -> Json.toJson(o.apportionedBeneficialLoan),
        "apportionedCar"                                       -> Json.toJson(o.apportionedCar),
        "apportionedCarFuel"                                   -> Json.toJson(o.apportionedCarFuel),
        "apportionedEducationalServices"                       -> Json.toJson(o.apportionedEducationalServices),
        "apportionedEntertaining"                              -> Json.toJson(o.apportionedEntertaining),
        "apportionedExpenses"                                  -> Json.toJson(o.apportionedExpenses),
        "apportionedMedicalInsurance"                          -> Json.toJson(o.apportionedMedicalInsurance),
        "apportionedTelephone"                                 -> Json.toJson(o.apportionedTelephone),
        "apportionedService"                                   -> Json.toJson(o.apportionedService),
        "apportionedTaxableExpenses"                           -> Json.toJson(o.apportionedTaxableExpenses),
        "apportionedVan"                                       -> Json.toJson(o.apportionedVan),
        "apportionedVanFuel"                                   -> Json.toJson(o.apportionedVanFuel),
        "apportionedMileage"                                   -> Json.toJson(o.apportionedMileage),
        "apportionedNonQualifyingRelocationExpenses"           -> Json.toJson(o.apportionedNonQualifyingRelocationExpenses),
        "apportionedNurseryPlaces"                             -> Json.toJson(o.apportionedNurseryPlaces),
        "apportionedOtherItems"                                -> Json.toJson(o.apportionedOtherItems),
        "apportionedPaymentsOnEmployeesBehalf"                 -> Json.toJson(o.apportionedPaymentsOnEmployeesBehalf),
        "apportionedPersonalIncidentalExpenses"                -> Json.toJson(o.apportionedPersonalIncidentalExpenses),
        "apportionedQualifyingRelocationExpenses"              -> Json.toJson(o.apportionedQualifyingRelocationExpenses),
        "apportionedEmployerProvidedProfessionalSubscriptions" -> Json.toJson(o.apportionedEmployerProvidedProfessionalSubscriptions),
        "apportionedEmployerProvidedServices"                  -> Json.toJson(o.apportionedEmployerProvidedServices),
        "apportionedIncomeTaxPaidByDirector"                   -> Json.toJson(o.apportionedIncomeTaxPaidByDirector),
        "apportionedTravelAndSubsistence"                      -> Json.toJson(o.apportionedTravelAndSubsistence),
        "apportionedVouchersAndCreditCards"                    -> Json.toJson(o.apportionedVouchersAndCreditCards),
        "apportionedNonCash"                                   -> Json.toJson(o.apportionedNonCash)
      ).filterNot { case (_, value) =>
        value == JsNull
      }
    )
  }
}
