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

package shared.models.domain

import play.api.libs.json.{JsString, JsValue, Json}
import shared.utils.UnitSpec

class StatusSpec extends UnitSpec {

  "reading a status from json" should {
    "produce the 'valid' status type" in {
      val result: Status = JsString("valid").as[Status]
      result shouldBe Status.valid
    }

    "produce the 'invalid' status type" in {
      val result: Status = JsString("invalid").as[Status]
      result shouldBe Status.invalid
    }

    "produce the 'superseded' status type" in {
      val result: Status = JsString("superseded").as[Status]
      result shouldBe Status.superseded
    }

    "produce a json parse error" in {
      val result: Option[Status] = JsString("not-a-status").validate[Status].asOpt
      result shouldBe None
    }
  }

  "writing a status to json" should {
    "produce the expected json string" in {
      val status: Status  = Status.valid
      val result: JsValue = Json.toJson(status)
      result shouldBe JsString("valid")
    }
  }

}
