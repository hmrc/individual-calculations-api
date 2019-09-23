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
import play.api.libs.json._
import v1.models.des.{LossType, ReliefClaimed}
import v1.models.domain.TypeOfClaim
import v1.models.request.DesTaxYear

case class CarriedForwardLoss(
    claimId: Option[String],
    claimType: TypeOfClaim,
    taxYearClaimMade: Option[String],
    taxYearLossIncurred: String,
    currentLossValue: BigInt,
    lossType: LossType
)

object CarriedForwardLoss {

  implicit val writes: OWrites[CarriedForwardLoss] = Json.writes[CarriedForwardLoss]
  implicit val reads: Reads[CarriedForwardLoss] = (
    (JsPath \ "claimId").readNullable[String] and
      (JsPath \ "claimType").read[ReliefClaimed].map(des => des.toTypeOfClaim) and
      (JsPath \ "taxYearClaimMade").readNullable[Int].map(_.map(DesTaxYear.fromDesIntToString)) and
      (JsPath \ "taxYearLossIncurred").read[Int].map(DesTaxYear.fromDesIntToString) and
      (JsPath \ "currentLossValue").read[BigInt] and
      (JsPath \ "lossType").read[LossType]
  )(CarriedForwardLoss.apply _)

}
