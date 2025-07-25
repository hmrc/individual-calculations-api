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

import shared.utils.UnitSpec

class EmploymentAndPensionsIncomeSpec extends UnitSpec {

  val detail: EmploymentAndPensionsIncomeDetail = EmploymentAndPensionsIncomeDetail(
    incomeSourceId = None,
    source = None,
    occupationalPension = None,
    employerRef = None,
    employerName = None,
    offPayrollWorker = Some(true),
    payrollId = None,
    startDate = None,
    dateEmploymentEnded = None,
    taxablePayToDate = None,
    totalTaxToDate = None,
    disguisedRemuneration = None,
    lumpSums = None,
    studentLoans = None,
    benefitsInKind = None
  )

  val detailWithIncomeSource: EmploymentAndPensionsIncomeDetail        = detail.copy(incomeSourceId = Some("incomeSourceId"))
  val updatedDetailWithIncomeSource: EmploymentAndPensionsIncomeDetail = detailWithIncomeSource.copy(offPayrollWorker = None)
  val model: EmploymentAndPensionsIncome                               = EmploymentAndPensionsIncome(None, None, None, None, Some(Seq(detail)))
  val updatedModel: EmploymentAndPensionsIncome                        = model.copy(employmentAndPensionsIncomeDetail = None)

  "withoutOffPayrollWorker" when {
    "employmentAndPensionIncome is defined, returns true" should {
      "employmentAndPensionDetail should be Some(_)" in {
        val employmentAndPensionsIncome: EmploymentAndPensionsIncome =
          model.copy(employmentAndPensionsIncomeDetail = Some(Seq(detailWithIncomeSource)))
        val result: EmploymentAndPensionsIncome = employmentAndPensionsIncome.withoutOffPayrollWorker
        result.employmentAndPensionsIncomeDetail shouldBe Some(Seq(updatedDetailWithIncomeSource))
      }
    }
    "employmentAndPensionIncome is defined, returns false" should {
      "employmentAndPensionDetail should be None" in {
        val employmentAndPensionsIncome: EmploymentAndPensionsIncome = model
        val result                                                   = employmentAndPensionsIncome.withoutOffPayrollWorker
        result.employmentAndPensionsIncomeDetail shouldBe None
      }
    }

    "employmentAndPensionIncome is not defined, returns false" should {
      "employmentAndPensionDetail should be None" in {
        val employmentAndPensionsIncome: EmploymentAndPensionsIncome = updatedModel
        val result                                                   = employmentAndPensionsIncome.withoutOffPayrollWorker
        result.employmentAndPensionsIncomeDetail shouldBe None
      }
    }
  }

}
