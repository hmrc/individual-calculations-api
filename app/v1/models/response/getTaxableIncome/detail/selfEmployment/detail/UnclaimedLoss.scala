/*
 * Copyright 2020 HM Revenue & Customs
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

package v1.models.response.getTaxableIncome.detail.selfEmployment.detail

import play.api.libs.json._
import v1.models.response.common.LossType

case class UnclaimedLoss(taxYearLossIncurred: String, currentLossValue: BigInt, expires: String, lossType: LossType)

object UnclaimedLoss {
  implicit val format: OFormat[UnclaimedLoss] = Json.format[UnclaimedLoss]
}
