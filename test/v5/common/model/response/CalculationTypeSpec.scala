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

package v5.common.model.response

import common.utils.enums.EnumJsonSpecSupport
import shared.utils.UnitSpec
import v5.common.model.response.CalculationType._

class CalculationTypeSpec extends UnitSpec with EnumJsonSpecSupport {

  testReads[CalculationType](
    "inYear"          -> `inYear`,
    "crystallisation" -> `finalDeclaration`
  )

  testWrites[CalculationType](
    `inYear`           -> "inYear",
    `finalDeclaration` -> "finalDeclaration"
  )

}
