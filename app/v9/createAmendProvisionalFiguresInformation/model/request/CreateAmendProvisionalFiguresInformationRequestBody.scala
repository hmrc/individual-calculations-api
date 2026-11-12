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

package v9.createAmendProvisionalFiguresInformation.model.request

import play.api.libs.functional.syntax.toFunctionalBuilderOps
import play.api.libs.json.{JsPath, OWrites, Reads}

case class CreateAmendProvisionalFiguresInformationRequestBody(
    provisionalFiguresIndicator: Boolean,
    expectedDate: Option[String],
    supplementaryInformation: Option[String]
)

object CreateAmendProvisionalFiguresInformationRequestBody {

  implicit val writes: OWrites[CreateAmendProvisionalFiguresInformationRequestBody] = (
    (JsPath \ "pfIndicator").write[Boolean] and
      (JsPath \ "dateExpected").writeNullable[String] and
      (JsPath \ "whiteSpace").writeNullable[String]
  ).tupled.contramap(body => (body.provisionalFiguresIndicator, body.expectedDate, body.supplementaryInformation))

  implicit val reads: Reads[CreateAmendProvisionalFiguresInformationRequestBody] = (
    (JsPath \ "provisionalFiguresIndicator").read[Boolean] and
      (JsPath \ "expectedDate").readNullable[String] and
      (JsPath \ "supplementaryInformation").readNullable[String]
  )(CreateAmendProvisionalFiguresInformationRequestBody.apply)

}
