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

package v7.retrieveCalculation.def3.model.response

import shared.utils.UnitSpec
import ClaimType._
import common.utils.enums.EnumJsonSpecSupport

class ClaimTypeSpec extends UnitSpec with EnumJsonSpecSupport {

  testReads[ClaimType](
    "CF"     -> `carry-forward`,
    "CSGI"   -> `carry-sideways`,
    "CFCSGI" -> `carry-forward-to-carry-sideways`,
    "CB"     -> `carry-backwards`,
    "CBGI"   -> `carry-backwards-general-income`
  )

  testWrites[ClaimType](
    `carry-forward`                   -> "carry-forward",
    `carry-sideways`                  -> "carry-sideways",
    `carry-forward-to-carry-sideways` -> "carry-forward-to-carry-sideways",
    `carry-backwards`                 -> "carry-backwards",
    `carry-backwards-general-income`  -> "carry-backwards-general-income"
  )

}
