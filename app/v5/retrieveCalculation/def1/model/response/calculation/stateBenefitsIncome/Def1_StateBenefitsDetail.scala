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

package v5.retrieveCalculation.def1.model.response.calculation.stateBenefitsIncome

import play.api.libs.json.{Json, OFormat}

case class Def1_StateBenefitsDetail(incapacityBenefit: Option[Seq[Def1_CommonBenefitWithTaxPaid]],
                                    statePension: Option[Seq[Def1_CommonBenefit]],
                                    statePensionLumpSum: Option[Seq[Def1_StatePensionLumpSum]],
                                    employmentSupportAllowance: Option[Seq[Def1_CommonBenefitWithTaxPaid]],
                                    jobSeekersAllowance: Option[Seq[Def1_CommonBenefitWithTaxPaid]],
                                    bereavementAllowance: Option[Seq[Def1_CommonBenefit]],
                                    otherStateBenefits: Option[Seq[Def1_CommonBenefit]])

object Def1_StateBenefitsDetail {
  implicit val format: OFormat[Def1_StateBenefitsDetail] = Json.format[Def1_StateBenefitsDetail]
}
