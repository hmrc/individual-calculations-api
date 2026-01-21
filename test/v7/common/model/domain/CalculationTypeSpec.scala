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

package v7.common.model.domain

import shared.utils.UnitSpec

class CalculationTypeSpec extends UnitSpec {

  "CalculationType" should {

    "return the correct downstream codes" in {

      val cases = Seq(
        (`in-year`, "IY", "IY"),
        (`intent-to-finalise`, "IC", "IF"),
        (`intent-to-amend`, "AM", "IA"),
        (`final-declaration`, "CR", "DF"),
        (`confirm-amendment`, "AM", "CA")
      )

      cases.foreach { case (calculationType, expected2150, expectedStandard) =>
        calculationType.to2150Downstream shouldBe expected2150
        calculationType.toDownstream shouldBe expectedStandard
      }
    }
  }

}
