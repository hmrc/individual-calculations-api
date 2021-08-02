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

package v2.models.response.getIncomeTaxAndNics.detail.nics

import support.UnitSpec
import v2.fixtures.getIncomeTaxAndNics.detail.nics.Class4NicDetailFixture._
import v2.models.utils.JsonErrorValidators

class Class4NicDetailSpec extends UnitSpec with JsonErrorValidators {

  testJsonProperties[Class4NicDetail](class4NicDetailJson)(
    mandatoryProperties = Seq(),
    optionalProperties = Seq(
      "class4Losses",
      "totalIncomeLiableToClass4Charge",
      "totalIncomeChargeableToClass4",
      "class4NicBands"
    )
  )
}