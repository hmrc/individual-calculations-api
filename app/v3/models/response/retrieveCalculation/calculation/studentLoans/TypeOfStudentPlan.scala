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

package v3.models.response.retrieveCalculation.calculation.studentLoans

import play.api.libs.json.Format
import utils.enums.Enums
import v3.models.domain.StudentLoanPlanType

sealed trait TypeOfStudentPlan{
  def toPlanType: StudentLoanPlanType
}

object TypeOfStudentPlan {

  case object plan1 extends TypeOfStudentPlan {
    override def toPlanType: StudentLoanPlanType = StudentLoanPlanType.`01`
  }

  case object plan2 extends TypeOfStudentPlan {
    override def toPlanType: StudentLoanPlanType = StudentLoanPlanType.`02`
  }

  case object postgraduate extends TypeOfStudentPlan {
    override def toPlanType: StudentLoanPlanType = StudentLoanPlanType.`03`
  }

  case object plan4 extends TypeOfStudentPlan {
    override def toPlanType: StudentLoanPlanType = StudentLoanPlanType.`04`
  }

  implicit val format: Format[TypeOfStudentPlan] = Enums.format[TypeOfStudentPlan]
}


