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

package v1.models.response.getCalculation.taxableIncome.detail.selfEmployment

import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Json, Reads, Writes}
import v1.models.des.LossType
import v1.models.domain.TypeOfLoss
import v1.models.request.DesTaxYear

case class UnclaimedLoss(
    taxYearLossIncurred: String,
    currentLossValue: BigInt,
    expires: String,
    lossType: TypeOfLoss
)

object UnclaimedLoss {

  implicit val writes: Writes[UnclaimedLoss] = Json.writes[UnclaimedLoss]
  implicit val reads: Reads[UnclaimedLoss] = (
    (JsPath \ "taxYearLossIncurred").read[Int].map(DesTaxYear.fromDesIntToString) and
      (JsPath \ "currentLossValue").read[BigInt] and
      (JsPath \ "expires").read[Int].map(DesTaxYear.fromDesIntToString) and
      (JsPath \ "lossType").read[LossType].map(_.toTypeOfLoss)
  )(UnclaimedLoss.apply _)

}
