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

package v9.retrieveProvisionalFiguresInformation

import api.models.domain.Timestamp
import play.api.libs.json.{JsValue, Json}
import v9.retrieveProvisionalFiguresInformation.model.response.RetrieveProvisionalFiguresInformationResponse

object RetrieveProvisionalFiguresInformationFixtures {

  val responseModel: RetrieveProvisionalFiguresInformationResponse = RetrieveProvisionalFiguresInformationResponse(
    submittedOn = Timestamp("2027-08-24T14:15:22.544Z"),
    provisionalFiguresIndicator = true,
    expectedDate = Some("2027-10-05"),
    supplementaryInformation = Some("Provisional figures used pending final confirmation.")
  )

  val minimumResponseModel: RetrieveProvisionalFiguresInformationResponse = RetrieveProvisionalFiguresInformationResponse(
    submittedOn = Timestamp("2027-08-24T14:15:22.544Z"),
    provisionalFiguresIndicator = true,
    expectedDate = None,
    supplementaryInformation = None
  )

  val responseMtdJson: JsValue = Json.parse(
    """
      |{
      |  "submittedOn": "2027-08-24T14:15:22.544Z",
      |  "provisionalFiguresIndicator": true,
      |  "expectedDate": "2027-10-05",
      |  "supplementaryInformation": "Provisional figures used pending final confirmation."
      |}
    """.stripMargin
  )

  val minimumResponseMtdJson: JsValue = Json.parse(
    """
      |{
      |  "submittedOn": "2027-08-24T14:15:22.544Z",
      |  "provisionalFiguresIndicator": true
      |}
    """.stripMargin
  )

  val responseDownstreamJson: JsValue = Json.parse(
    """
      |{
      |  "submittedOn": "2027-08-24T14:15:22.544Z",
      |  "pfIndicator": true,
      |  "dateExpected": "2027-10-05",
      |  "whiteSpace": "Provisional figures used pending final confirmation."
      |}
    """.stripMargin
  )

  val minimumResponseDownstreamJson: JsValue = Json.parse(
    """
      |{
      |  "submittedOn": "2027-08-24T14:15:22.544Z",
      |  "pfIndicator": true
      |}
    """.stripMargin
  )

}
