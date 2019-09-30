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

package v1.models.response.getAllowancesDeductionsAndReliefs

import play.api.libs.json._

case class GetAllowancesDeductionsAndReliefs(summary: CalculationSummary, detail: CalculationDetail) {

  def isEmpty: Boolean =
    (summary.totalAllowancesAndDeductions, summary.totalReliefs) match {
      case (Some(x), _) if x > 0 => false
      case (_, Some(x)) if x > 0 => false
      case _                     => true
    }
}

object GetAllowancesDeductionsAndReliefs {
  implicit val reads: Reads[GetAllowancesDeductionsAndReliefs] =
    (__ \ "allowancesDeductionsAndReliefs").read(Json.reads[GetAllowancesDeductionsAndReliefs])

  implicit val writes: Writes[GetAllowancesDeductionsAndReliefs] = Json.writes[GetAllowancesDeductionsAndReliefs]
}
