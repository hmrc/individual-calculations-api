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

package v5.retrieveCalculation.def1.model.response.inputs

import play.api.libs.json.{Json, OFormat}

case class Def1_PersonalInformation(identifier: String,
                                    dateOfBirth: Option[String],
                                    taxRegime: String,
                                    statePensionAgeDate: Option[String],
                                    studentLoanPlan: Option[Seq[Def1_StudentLoanPlan]],
                                    class2VoluntaryContributions: Option[Boolean],
                                    marriageAllowance: Option[String],
                                    uniqueTaxpayerReference: Option[String],
                                    itsaStatus: Option[String]) {
  def withoutItsaStatus: Def1_PersonalInformation = copy(itsaStatus = None)
}

object Def1_PersonalInformation {
  implicit val format: OFormat[Def1_PersonalInformation] = Json.format[Def1_PersonalInformation]
}
