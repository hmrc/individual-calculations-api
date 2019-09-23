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
import v1.models.des.{LossType, ReliefClaimed}
import v1.models.domain.{TypeOfClaim, TypeOfLoss}
import v1.models.request.DesTaxYear

case class ResultOfClaimApplied(
    claimId: Option[String],
    taxYearClaimMade: String,
    claimType: TypeOfClaim,
    mtdLoss: Boolean,
    taxYearLossIncurred: String,
    lossAmountUsed: BigDecimal,
    remainingLossValue: BigDecimal,
    lossType: TypeOfLoss
)

object ResultOfClaimApplied {

  implicit val writes: Writes[ResultOfClaimApplied] = Json.writes[ResultOfClaimApplied]

  implicit val reads: Reads[ResultOfClaimApplied] = (
    (JsPath \ "claimId").readNullable[String] and
      (JsPath \ "taxYearClaimMade").read[Int].map(DesTaxYear.fromDesIntToString) and
      (JsPath \ "claimType").read[ReliefClaimed].map(des => des.toTypeOfClaim) and
      (JsPath \ "mtdLoss").read[Boolean].orElse(Reads.pure(true)) and
      (JsPath \ "taxYearLossIncurred").read[Int].map(DesTaxYear.fromDesIntToString) and
      (JsPath \ "lossAmountUsed").read[BigDecimal] and
      (JsPath \ "remainingLossValue").read[BigDecimal] and
      (JsPath \ "lossType").read[LossType].map(_.toTypeOfLoss)
    )(ResultOfClaimApplied.apply _)

}