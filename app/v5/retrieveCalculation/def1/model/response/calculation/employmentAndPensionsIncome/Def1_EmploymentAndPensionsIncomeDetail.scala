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

package v5.retrieveCalculation.def1.model.response.calculation.employmentAndPensionsIncome

import play.api.libs.json.{Format, Json}
import v5.retrieveCalculation.def1.model.response.common.Def1_Source

case class Def1_EmploymentAndPensionsIncomeDetail(incomeSourceId: Option[String],
                                                  source: Option[Def1_Source],
                                                  occupationalPension: Option[Boolean],
                                                  employerRef: Option[String],
                                                  employerName: Option[String],
                                                  offPayrollWorker: Option[Boolean],
                                                  payrollId: Option[String],
                                                  startDate: Option[String],
                                                  dateEmploymentEnded: Option[String],
                                                  taxablePayToDate: Option[BigDecimal],
                                                  totalTaxToDate: Option[BigDecimal],
                                                  disguisedRemuneration: Option[Boolean],
                                                  lumpSums: Option[Def1_LumpSums],
                                                  studentLoans: Option[Def1_StudentLoans],
                                                  benefitsInKind: Option[Def1_BenefitsInKind]) {

  val isDefined: Boolean =
    !(incomeSourceId.isEmpty &&
      source.isEmpty &&
      occupationalPension.isEmpty &&
      employerRef.isEmpty &&
      employerName.isEmpty &&
      offPayrollWorker.isEmpty &&
      payrollId.isEmpty &&
      startDate.isEmpty &&
      dateEmploymentEnded.isEmpty &&
      taxablePayToDate.isEmpty &&
      totalTaxToDate.isEmpty &&
      disguisedRemuneration.isEmpty &&
      lumpSums.isEmpty &&
      studentLoans.isEmpty &&
      benefitsInKind.isEmpty)

  def withoutOffPayrollWorker: Def1_EmploymentAndPensionsIncomeDetail = copy(offPayrollWorker = None)

}

object Def1_EmploymentAndPensionsIncomeDetail {

  implicit val format: Format[Def1_EmploymentAndPensionsIncomeDetail] = Json.format[Def1_EmploymentAndPensionsIncomeDetail]
}
