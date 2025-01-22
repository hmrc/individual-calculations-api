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

package v7.common.model.response

import common.utils.enums.EnumJsonSpecSupport
import shared.utils.UnitSpec
import v7.common.model.response.StudentLoanPlanType._

class StudentLoanPlanTypeSpec extends UnitSpec with EnumJsonSpecSupport {

  testReads[StudentLoanPlanType](
    "01" -> `plan1`,
    "02" -> `plan2`,
    "03" -> `postgraduate`,
    "04" -> `plan4`
  )

  testWrites[StudentLoanPlanType](
    `plan1`        -> "plan1",
    `plan2`        -> "plan2",
    `postgraduate` -> "postgraduate",
    `plan4`        -> "plan4"
  )

}
