/*
 * Copyright 2023 HM Revenue & Customs
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

package v5.retrieveCalculation.def1.model.response.calculation.lossesAndClaims

import play.api.libs.json.{Json, OFormat}

case class Def1_LossesAndClaims(
                                 resultOfClaimsApplied: Option[Seq[Def1_ResultOfClaimsApplied]],
                                 unclaimedLosses: Option[Seq[Def1_UnclaimedLoss]],
                                 carriedForwardLosses: Option[Seq[Def1_CarriedForwardLoss]],
                                 defaultCarriedForwardLosses: Option[Seq[Def1_DefaultCarriedForwardLoss]],
                                 claimsNotApplied: Option[Seq[Def1_ClaimNotApplied]]
)

object Def1_LossesAndClaims {
  implicit val format: OFormat[Def1_LossesAndClaims] = Json.format
}
