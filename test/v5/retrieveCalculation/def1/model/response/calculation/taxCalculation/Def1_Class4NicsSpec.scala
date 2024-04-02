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

package v5.retrieveCalculation.def1.model.response.calculation.taxCalculation

import api.models.utils.JsonErrorValidators
import support.UnitSpec

class Def1_Class4NicsSpec extends UnitSpec with JsonErrorValidators with Def1_TaxCalculationFixture {

  "have the correct fields optional" when {
    val json = (taxCalculationDownstreamJson \ "nics" \ "class4Nics").get

    testJsonAllPropertiesOptionalExcept[Def1_Class4Nics](json)("totalAmount", "nic4Bands")
  }

}
