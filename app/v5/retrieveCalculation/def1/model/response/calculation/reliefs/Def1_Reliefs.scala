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

package v5.retrieveCalculation.def1.model.response.calculation.reliefs

import play.api.libs.json.{Json, OFormat}

case class Def1_Reliefs(residentialFinanceCosts: Option[Def1_ResidentialFinanceCosts],
                        reliefsClaimed: Option[Seq[Def1_ReliefsClaimed]],
                        foreignTaxCreditRelief: Option[Def1_ForeignTaxCreditRelief],
                        topSlicingRelief: Option[Def1_TopSlicingRelief],
                        basicRateExtension: Option[Def1_BasicRateExtension],
                        giftAidTaxReductionWhereBasicRateDiffers: Option[Def1_GiftAidTaxReductionWhereBasicRateDiffers]) {

  val isDefined: Boolean =
    !(residentialFinanceCosts.isEmpty && reliefsClaimed.isEmpty && foreignTaxCreditRelief.isEmpty && topSlicingRelief.isEmpty && basicRateExtension.isEmpty && giftAidTaxReductionWhereBasicRateDiffers.isEmpty)

  def withoutBasicExtension: Def1_Reliefs = copy(basicRateExtension = None)

  def withoutGiftAidTaxReductionWhereBasicRateDiffers: Def1_Reliefs = copy(giftAidTaxReductionWhereBasicRateDiffers = None)

}

object Def1_Reliefs extends {

  implicit val format: OFormat[Def1_Reliefs] = Json.format[Def1_Reliefs]
}
