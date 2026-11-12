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

package v9.createAmendProvisionalFiguresInformation

import api.models.domain.{Nino, TaxYear}
import play.api.libs.json.{JsValue, Json}
import v9.createAmendProvisionalFiguresInformation.model.request.{
  CreateAmendProvisionalFiguresInformationRequestBody,
  CreateAmendProvisionalFiguresInformationRequestData
}

import java.time.{LocalDate, ZoneOffset}

object CreateAmendProvisionalFiguresInformationFixtures {

  val expectedDate: String = LocalDate.now(ZoneOffset.UTC).plusYears(1).toString
  private val dateExpected = expectedDate

  val validMtdRequestJson: JsValue = Json.obj(
    "provisionalFiguresIndicator" -> true,
    "expectedDate"                -> expectedDate,
    "supplementaryInformation"    -> "Provisional figures used pending final confirmation."
  )

  val validDownstreamRequestJson: JsValue = Json.obj(
    "pfIndicator"  -> true,
    "dateExpected" -> dateExpected,
    "whiteSpace"   -> "Provisional figures used pending final confirmation."
  )

  val validMinimumMtdRequestJson: JsValue = Json.parse(
    """
      |{
      |  "provisionalFiguresIndicator": true
      |}
    """.stripMargin
  )

  val validMinimumDownstreamRequestJson: JsValue = Json.obj(
    "pfIndicator" -> true
  )

  val validRequestBody: CreateAmendProvisionalFiguresInformationRequestBody = CreateAmendProvisionalFiguresInformationRequestBody(
    provisionalFiguresIndicator = true,
    expectedDate = Some(expectedDate),
    supplementaryInformation = Some("Provisional figures used pending final confirmation.")
  )

  val validMinimumRequestBody: CreateAmendProvisionalFiguresInformationRequestBody = CreateAmendProvisionalFiguresInformationRequestBody(
    provisionalFiguresIndicator = true,
    expectedDate = None,
    supplementaryInformation = None
  )

  val nino: Nino       = Nino("AA123456A")
  val taxYear: TaxYear = TaxYear.fromMtd("2026-27")

  val validRequestData: CreateAmendProvisionalFiguresInformationRequestData = CreateAmendProvisionalFiguresInformationRequestData(
    nino,
    taxYear,
    validRequestBody
  )

  val validMinimumRequestData: CreateAmendProvisionalFiguresInformationRequestData = CreateAmendProvisionalFiguresInformationRequestData(
    nino,
    taxYear,
    validMinimumRequestBody
  )

}
