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

package v3.models.response.common

import support.UnitSpec
import utils.enums.EnumJsonSpecSupport
import LossType._

class LossTypeSpec extends UnitSpec with EnumJsonSpecSupport {

  testReads[LossType](
    "income"     -> income,
    "class4nics" -> class4nics
  )

  testWrites[LossType](
    income     -> "income",
    class4nics -> "class4nics"
  )

}