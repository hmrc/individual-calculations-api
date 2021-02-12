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

package v1r2.models.response.common

import play.api.libs.json.Format
import utils.enums.Enums

sealed trait CalculationReason

object CalculationReason {

  case object customerRequest extends CalculationReason
  case object class2NICEvent extends CalculationReason
  case object newLossEvent extends CalculationReason
  case object updatedLossEvent extends CalculationReason
  case object newClaimEvent extends CalculationReason
  case object updatedClaimEvent extends CalculationReason

  implicit val format: Format[CalculationReason] = Enums.format[CalculationReason]
}