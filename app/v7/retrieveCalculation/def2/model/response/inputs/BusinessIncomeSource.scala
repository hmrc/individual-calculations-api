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

package v7.retrieveCalculation.def2.model.response.inputs

import play.api.libs.json.{Format, Json, OFormat}
import v7.common.model.response.IncomeSourceType
import IncomeSourceType._

case class BusinessIncomeSource(incomeSourceId: String,
                                incomeSourceType: IncomeSourceType,
                                incomeSourceName: Option[String],
                                accountingPeriodStartDate: String,
                                accountingPeriodEndDate: String,
                                source: String,
                                commencementDate: Option[String],
                                cessationDate: Option[String],
                                latestPeriodEndDate: String,
                                latestReceivedDateTime: String,
                                submissionPeriods: Option[Seq[SubmissionPeriod]])

case object BusinessIncomeSource {

  implicit val incomeSourceTypeFormat: Format[IncomeSourceType] = IncomeSourceType.formatRestricted(
    `self-employment`,
    `uk-property`,
    `uk-property-fhl`,
    `foreign-property-fhl-eea`,
    `foreign-property`
  )

  implicit val format: OFormat[BusinessIncomeSource] = Json.format[BusinessIncomeSource]

}
