/*
 * Copyright 2026 HM Revenue & Customs
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

package v9.retrieveCalculation.def1.model.response.calculation.studentLoans

import api.utils.UnitSpec
import play.api.libs.json.{JsError, JsObject, JsValue, Json}
import v9.common.model.response.StudentLoanPlanType.`plan1`

class StudentLoansSpec extends UnitSpec {

  val model: StudentLoans = StudentLoans(
    planType = `plan1`,
    studentLoanTotalIncomeAmount = 123.45,
    studentLoanChargeableIncomeAmount = 123.45,
    studentLoanRepaymentAmount = 123.45,
    studentLoanDeductionsFromEmployment = Some(123.45),
    studentLoanRepaymentAmountNetOfDeductions = 123.45,
    studentLoanApportionedIncomeThreshold = 123,
    studentLoanRate = 123.45,
    payeIncomeForStudentLoan = Some(123.45),
    nonPayeIncomeForStudentLoan = Some(123.45)
  )

  val readJson: JsValue = Json.parse(
    """
      |{
      |  "planType": "01",
      |  "studentLoanTotalIncomeAmount": 123.45,
      |  "studentLoanChargeableIncomeAmount": 123.45,
      |  "studentLoanRepaymentAmount": 123.45,
      |  "studentLoanDeductionsFromEmployment": 123.45,
      |  "studentLoanRepaymentAmountNetOfDeductions": 123.45,
      |  "studentLoanApportionedIncomeThreshold": 123,
      |  "studentLoanRate": 123.45,
      |  "payeIncomeForStudentLoan": 123.45,
      |  "nonPayeIncomeForStudentLoan": 123.45
      |}
      |""".stripMargin
  )

  val writeJson: JsValue = Json.parse(
    """
      |{
      |  "planType": "plan1",
      |  "studentLoanTotalIncomeAmount": 123.45,
      |  "studentLoanChargeableIncomeAmount": 123.45,
      |  "studentLoanRepaymentAmount": 123.45,
      |  "studentLoanDeductionsFromEmployment": 123.45,
      |  "studentLoanRepaymentAmountNetOfDeductions": 123.45,
      |  "studentLoanApportionedIncomeThreshold": 123,
      |  "studentLoanRate": 123.45,
      |  "payeIncomeForStudentLoan": 123.45,
      |  "nonPayeIncomeForStudentLoan": 123.45
      |}
      |""".stripMargin
  )

  val badJson: JsValue = Json.parse(
    """
      |{
      |  "planType": "01",
      |  "studentLoanTotalIncomeAmount": "FIFTY",
      |  "studentLoanChargeableIncomeAmount": 123.45,
      |  "studentLoanRepaymentAmount": 123.45,
      |  "studentLoanDeductionsFromEmployment": 123.45,
      |  "studentLoanRepaymentAmountNetOfDeductions": 123.45,
      |  "studentLoanApportionedIncomeThreshold": 123,
      |  "studentLoanRate": 123.45,
      |  "payeIncomeForStudentLoan": 123.45,
      |  "nonPayeIncomeForStudentLoan": 123.45
      |}
      |""".stripMargin
  )

  "reads" when {
    "passed valid JSON" should {
      "return a valid model" in {
        readJson.as[StudentLoans] shouldBe model
      }
    }
  }

  "writes" when {
    "passed valid model" should {
      "return valid JSON" in {
        Json.toJson(model) shouldBe writeJson
      }
    }
  }

  "format" when {
    "passed the wrong type" should {
      "return an error" in {
        badJson.validate[StudentLoans] shouldBe a[JsError]
      }
    }

    "error when JSON is invalid" in {
      JsObject.empty.validate[StudentLoans] shouldBe a[JsError]
    }
  }

}
