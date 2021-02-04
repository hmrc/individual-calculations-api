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

package v1r2.fixtures.getAllowancesDeductionsAndReliefs

import play.api.libs.json.{JsObject, JsValue, Json}
import v1r2.fixtures.getAllowancesDeductionsAndReliefs.detail.CalculationDetailFixture._
import v1r2.fixtures.getAllowancesDeductionsAndReliefs.summary.CalculationSummaryFixture._
import v1r2.fixtures.getMetadata.MetadataResponseFixture._
import v1r2.models.response.getAllowancesDeductionsAndReliefs.AllowancesDeductionsAndReliefsResponse
import v1r2.models.response.getAllowancesDeductionsAndReliefs.detail.CalculationDetail
import v1r2.models.response.getAllowancesDeductionsAndReliefs.summary.CalculationSummary

object AllowancesDeductionsAndReliefsResponseFixture {

  val allowancesDeductionsAndReliefsResponseModel: AllowancesDeductionsAndReliefsResponse =
    AllowancesDeductionsAndReliefsResponse(
      summary = calculationSummaryModel,
      detail = calculationDetailModel,
      id = "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c"
    )

  val allowancesDeductionsAndReliefsResponseModelEmpty: AllowancesDeductionsAndReliefsResponse =
    AllowancesDeductionsAndReliefsResponse(
      summary = CalculationSummary(
        totalAllowancesAndDeductions = None,
        totalReliefs = None
      ),
      detail = CalculationDetail(
        allowancesAndDeductions = None,
        reliefs = None
      ),
      id = "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c"
    )

  val allowancesDeductionsAndReliefsResponseJson: JsObject =
    Json.obj("summary" -> calculationSummaryJson) ++
    Json.obj("detail" -> calculationDetailJson)

  val allowancesDeductionsAndReliefsResponseJsonEmpty: JsObject = Json.parse(
    """
      |{
      |  "allowancesDeductionsAndReliefs": {
      |    "summary": {
      |    },
      |    "detail":{
      |    }
      |  }
      |}
    """.stripMargin
  ).as[JsObject]

  val allowancesDeductionsAndReliefsTopLevelJson: JsValue =
    metadataResponseTopLevelJsonWithoutErrors.as[JsObject] ++
    Json.obj("allowancesDeductionsAndReliefs" -> allowancesDeductionsAndReliefsResponseJson)

  val noAllowancesDeductionsAndReliefsExistJsonFromBackend: JsValue =
    metadataResponseTopLevelJsonWithoutErrors.as[JsObject] ++
    allowancesDeductionsAndReliefsResponseJsonEmpty
}