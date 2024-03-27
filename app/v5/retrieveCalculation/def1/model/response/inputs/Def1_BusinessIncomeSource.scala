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

import play.api.libs.json.{Format, Json, OFormat}
import v5.retrieveCalculation.def1.model.response.common.Def1_IncomeSourceType
import v5.retrieveCalculation.def1.model.response.common.Def1_IncomeSourceType._

case class Def1_BusinessIncomeSource(incomeSourceId: String,
                                     incomeSourceType: Def1_IncomeSourceType,
                                     incomeSourceName: Option[String],
                                     accountingPeriodStartDate: String,
                                     accountingPeriodEndDate: String,
                                     source: String,
                                     commencementDate: Option[String],
                                     cessationDate: Option[String],
                                     latestPeriodEndDate: String,
                                     latestReceivedDateTime: String,
                                     finalised: Option[Boolean],
                                     finalisationTimestamp: Option[String],
                                     submissionPeriods: Option[Seq[Def1_SubmissionPeriod]]) {

  val isDefined: Boolean =
    !(incomeSourceName.isEmpty &&
      commencementDate.isEmpty &&
      cessationDate.isEmpty &&
      finalised.isEmpty &&
      finalisationTimestamp.isEmpty &&
      finalisationTimestamp.isEmpty &&
      submissionPeriods.isEmpty)

  def withoutCessationDate: Def1_BusinessIncomeSource = copy(cessationDate = None)

  def withoutCommencementDate: Def1_BusinessIncomeSource = copy(commencementDate = None)
}

case object Def1_BusinessIncomeSource {

  implicit val incomeSourceTypeFormat: Format[Def1_IncomeSourceType] = Def1_IncomeSourceType.formatRestricted(
    `self-employment`,
    `uk-property-non-fhl`,
    `uk-property-fhl`,
    `foreign-property-fhl-eea`,
    `foreign-property`
  )

  implicit val format: OFormat[Def1_BusinessIncomeSource] = Json.format[Def1_BusinessIncomeSource]

}
