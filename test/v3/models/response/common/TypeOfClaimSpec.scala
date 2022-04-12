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
import TypeOfClaim._
import play.api.libs.json.{JsResultException, JsString, Json}

class TypeOfClaimSpec extends UnitSpec with EnumJsonSpecSupport {

  testReads[TypeOfClaim](
    "CF"     -> `carry-forward`,
    "CSGI"   -> `carry-sideways`,
    "CFCSGI" -> `carry-forward-to-carry-sideways-general-income`,
    "CSFHL"  -> `carry-sideways-fhl`,
    "CB"     -> `carry-backwards`,
    "CBGI"   -> `carry-backwards-general-income`
  )

  testWrites[TypeOfClaim](
    `carry-forward`                                  -> "carry-forward",
    `carry-sideways`                                 -> "carry-sideways",
    `carry-forward-to-carry-sideways-general-income` -> "carry-forward-to-carry-sideways-general-income",
    `carry-sideways-fhl`                             -> "carry-sideways-fhl",
    `carry-backwards`                                -> "carry-backwards",
    `carry-backwards-general-income`                 -> "carry-backwards-general-income"
  )

  "formatRestricted" when {
    "reads" should {
      "work when the provided IncomeSourceType is in the list" in {
        JsString("CF").as[TypeOfClaim](TypeOfClaim.formatRestricted(TypeOfClaim.`carry-forward`)) shouldBe TypeOfClaim.`carry-forward`
      }
      "fail when the provided IncomeSourceType is not in the list" in {
        val exception = intercept[JsResultException] {
          JsString("CSGI").as[TypeOfClaim](TypeOfClaim.formatRestricted(TypeOfClaim.`carry-forward`))
        }
        exception.errors.head._2.head.message shouldBe "error.expected.TypeOfClaim"
      }
    }
    "writes" should {
      "work" in {
        Json.toJson(TypeOfClaim.`carry-forward`)(TypeOfClaim.formatRestricted(TypeOfClaim.`carry-forward`).writes) shouldBe
          JsString("carry-forward")
      }
    }
  }

}
