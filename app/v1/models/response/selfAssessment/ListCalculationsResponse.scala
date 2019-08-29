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

package v1.models.response.selfAssessment

import play.api.libs.json.{Format, Json}

case class CalculationListItem(
                                id: String,
                                calculationTimestamp: String,
                                `type`: CalculationType,
                                requestedBy: Option[CalculationRequestor]
                              )

object CalculationListItem{
  implicit val format: Format[CalculationListItem] = Json.format
}

case class ListCalculationsResponse(calculations: Seq[CalculationListItem])

object ListCalculationsResponse {
  implicit val format: Format[ListCalculationsResponse] = Json.format
}


