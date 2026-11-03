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

package v9.retrieveProvisionalFiguresInformation.model.response

import api.models.domain.Timestamp
import play.api.libs.functional.syntax.*
import play.api.libs.json.*

case class RetrieveProvisionalFiguresInformationResponse(
    submittedOn: Timestamp,
    provisionalFiguresIndicator: Boolean,
    expectedDate: Option[String],
    supplementaryInformation: Option[String]
)

object RetrieveProvisionalFiguresInformationResponse {

  implicit val reads: Reads[RetrieveProvisionalFiguresInformationResponse] = (
    (JsPath \ "submittedOn").read[Timestamp] and
      (JsPath \ "pfIndicator").read[Boolean] and
      (JsPath \ "dateExpected").readNullable[String] and
      (JsPath \ "whiteSpace").readNullable[String]
  )(RetrieveProvisionalFiguresInformationResponse.apply)

  implicit val writes: OWrites[RetrieveProvisionalFiguresInformationResponse] = Json.writes[RetrieveProvisionalFiguresInformationResponse]
}
