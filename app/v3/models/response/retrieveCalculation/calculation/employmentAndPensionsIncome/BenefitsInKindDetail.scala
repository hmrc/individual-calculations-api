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

package v3.models.response.retrieveCalculation.calculation.employmentAndPensionsIncome

import play.api.libs.json.{Format, Json}

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

  implicit val format: Format[BenefitsInKindDetail]  = Json.format[BenefitsInKindDetail]
}
