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

package v7.listCalculationsOld.def1.model.response

import play.api.libs.json.{JsArray, Json}
import shared.utils.UnitSpec
import v7.listCalculationsOld.def1.model.Def1_ListCalculationsFixture
import v7.listCalculationsOld.model.response.Def1_ListCalculationsResponse

class Def1_ListCalculationsResponseSpec extends UnitSpec with Def1_ListCalculationsFixture {

  "ListCalculationsResponse" when {
    "read from downstream JSON" must {
      "return the expected data model" in {
        listCalculationsDownstreamJson.as[Def1_ListCalculationsResponse[Def1_Calculation]] shouldBe listCalculationsResponseModel
      }
    }

    "written to MTD JSON" must {
      "produce the expected JSON body" in {
        Json.toJson(listCalculationsResponseModel) shouldBe listCalculationsMtdJson
      }
    }

    "fail to read when JSON is not an array" in {
      val invalidJson = Json.obj("calculations" -> Json.arr())

      invalidJson.validate[Def1_ListCalculationsResponse[Def1_Calculation]].isError shouldBe true
    }

    "mapItems" must {

      "apply the mapping function to every calculation" in {
        val mapped =
          listCalculationsResponseModel.mapItems(_.calculationId)

        val originalIds =
          (Json.toJson(listCalculationsResponseModel) \ "calculations")
            .as[JsArray]
            .value
            .map(_("calculationId"))

        val mappedIds =
          (Json.toJson(mapped) \ "calculations")
            .as[JsArray]
            .value

        mappedIds shouldBe originalIds
      }

      "return an empty response when calculations are empty" in {
        val emptyResponse =
          Def1_ListCalculationsResponse[Def1_Calculation](Seq.empty)

        emptyResponse.mapItems(_.calculationId) shouldBe
          Def1_ListCalculationsResponse(Seq.empty)
      }
    }
  }

}
