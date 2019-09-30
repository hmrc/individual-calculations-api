/*
 * Copyright 2019 HM Revenue & Customs
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

package v1.models.response.getCalculationMetadata

import play.api.libs.json.{JsPath, Json, Reads, Writes}
import v1.models.response.common.{CalculationReason, CalculationRequestor, CalculationType}

case class CalculationMetadata(
    id: String,
    taxYear: String,
    requestedBy: CalculationRequestor,
    calculationReason: CalculationReason,
    calculationTimestamp: String,
    calculationType: CalculationType,
    intentToCrystallise: Boolean,
    crystallised: Boolean,
    totalIncomeTaxAndNicsDue: Option[BigDecimal],
    calculationErrorCount: Option[Int]
)

object CalculationMetadata {

  implicit val writes: Writes[CalculationMetadata] =
    Json.writes[CalculationMetadata]

  implicit def reads: Reads[CalculationMetadata] =
    (JsPath \ "metadata").read[CalculationMetadata](Json.reads[CalculationMetadata])
}
