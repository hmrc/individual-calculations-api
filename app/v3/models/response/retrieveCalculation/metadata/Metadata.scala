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

package v3.models.response.retrieveCalculation.metadata

import play.api.libs.functional.syntax._
import play.api.libs.json.{Format, JsPath, Json, OWrites, Reads}
import v3.models.domain.TaxYear
import v3.models.response.common.CalculationType

case class Metadata(calculationId: String,
                    taxYear: TaxYear,
                    requestedBy: String,
                    requestedTimestamp: Option[String],
                    calculationReason: String,
                    calculationTimestamp: Option[String],
                    calculationType: CalculationType,
                    intentToSubmitFinalDeclaration: Boolean,
                    finalDeclaration: Boolean,
                    finalDeclarationTimestamp: Option[String],
                    periodFrom: String,
                    periodTo: String)

object Metadata {

  implicit val taxYearFormat: Format[TaxYear] = TaxYear.downstreamIntToMtdFormat

  implicit val writes: OWrites[Metadata] = Json.writes[Metadata]

  implicit val reads: Reads[Metadata] = (
    (JsPath \ "calculationId").read[String] and
      (JsPath \ "taxYear").read[TaxYear] and
      (JsPath \ "requestedBy").read[String] and
      (JsPath \ "requestedTimestamp").readNullable[String] and
      (JsPath \ "calculationReason").read[String] and
      (JsPath \ "calculationTimestamp").readNullable[String] and
      (JsPath \ "calculationType").read[CalculationType] and
      (JsPath \ "intentToCrystallise").readWithDefault[Boolean](false) and
      (JsPath \ "crystallised").readWithDefault[Boolean](false) and
      (JsPath \ "crystallisationTimestamp").readNullable[String] and
      (JsPath \ "periodFrom").read[String] and
      (JsPath \ "periodTo").read[String]
  )(Metadata.apply _)

}
