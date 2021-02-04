/*
 * Copyright 2021 HM Revenue & Customs
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

package v2.fixtures.getIncomeTaxAndNics.detail

import play.api.libs.json.{JsValue, Json}
import v2.models.response.getIncomeTaxAndNics.detail.StudentLoans

object StudentLoansFixture {

  val planType: String = "01"
  val studentLoanTotalIncomeAmount: BigDecimal = 2000.99
  val studentLoanChargeableIncomeAmount: BigDecimal = 3000.99
  val studentLoanRepaymentAmount: BigDecimal = 4000.99
  val studentLoanDeductionsFromEmployment: Option[BigDecimal] = Some(5000.99)
  val studentLoanRepaymentAmountNetOfDeductions: BigDecimal = 6000.99
  val studentLoanApportionedIncomeThreshold: BigInt = 7000
  val studentLoanRate: BigDecimal = 50.99

  val studentLoansModel: StudentLoans =
    StudentLoans(
      planType = planType,
      studentLoanTotalIncomeAmount = studentLoanTotalIncomeAmount,
      studentLoanChargeableIncomeAmount = studentLoanChargeableIncomeAmount,
      studentLoanRepaymentAmount = studentLoanRepaymentAmount,
      studentLoanDeductionsFromEmployment = studentLoanDeductionsFromEmployment,
      studentLoanRepaymentAmountNetOfDeductions = studentLoanRepaymentAmountNetOfDeductions,
      studentLoanApportionedIncomeThreshold = studentLoanApportionedIncomeThreshold,
      studentLoanRate = studentLoanRate
    )

  val studentLoansJson: JsValue = Json.parse(
    s"""
       |{
       |   "planType": "$planType",
       |   "studentLoanTotalIncomeAmount": $studentLoanTotalIncomeAmount,
       |   "studentLoanChargeableIncomeAmount": $studentLoanChargeableIncomeAmount,
       |   "studentLoanRepaymentAmount": $studentLoanRepaymentAmount,
       |   "studentLoanDeductionsFromEmployment": ${studentLoanDeductionsFromEmployment.get},
       |   "studentLoanRepaymentAmountNetOfDeductions": $studentLoanRepaymentAmountNetOfDeductions,
       |   "studentLoanApportionedIncomeThreshold": $studentLoanApportionedIncomeThreshold,
       |   "studentLoanRate": $studentLoanRate
       |}
     """.stripMargin
  )
}