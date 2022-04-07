/*
 * Copyright 2022 HM Revenue & Customs
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

package v3.models.domain

import play.api.libs.json
import utils.enums.Enums
import v3.models.response.retrieveCalculation.calculation.studentLoans.TypeOfStudentPlan

sealed trait StudentLoanPlanType {
  def toTypeOfPlan: TypeOfStudentPlan
}

object StudentLoanPlanType {

  case object `01` extends StudentLoanPlanType {
    override def toTypeOfPlan: TypeOfStudentPlan = TypeOfStudentPlan.`plan1`
  }

  case object `02` extends StudentLoanPlanType {
    override def toTypeOfPlan: TypeOfStudentPlan = TypeOfStudentPlan.`plan2`
  }

  case object `03` extends StudentLoanPlanType {
    override def toTypeOfPlan: TypeOfStudentPlan = TypeOfStudentPlan.`postgraduate`
  }

  case object `04` extends StudentLoanPlanType {
    override def toTypeOfPlan: TypeOfStudentPlan = TypeOfStudentPlan.`plan4`
  }

  implicit val format: json.Format[StudentLoanPlanType] = Enums.format[StudentLoanPlanType]
}
