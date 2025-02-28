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

package v8.retrieveCalculation.def3.model.response.calculation.stateBenefitsIncome

import play.api.libs.json.{Json, OFormat}

case class StateBenefitsDetail(incapacityBenefit: Option[Seq[CommonBenefitWithTaxPaid]],
                                    statePension: Option[Seq[CommonBenefit]],
                                    statePensionLumpSum: Option[Seq[StatePensionLumpSum]],
                                    employmentSupportAllowance: Option[Seq[CommonBenefitWithTaxPaid]],
                                    jobSeekersAllowance: Option[Seq[CommonBenefitWithTaxPaid]],
                                    bereavementAllowance: Option[Seq[CommonBenefit]],
                                    otherStateBenefits: Option[Seq[CommonBenefit]])

object StateBenefitsDetail {
  implicit val format: OFormat[StateBenefitsDetail] = Json.format[StateBenefitsDetail]
}
