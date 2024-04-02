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

package v5.retrieveCalculation.def1.model.response.inputs

import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Json, OWrites, Reads}

case class Def1_Inputs(personalInformation: Def1_PersonalInformation,
                       incomeSources: Def1_IncomeSources,
                       annualAdjustments: Option[Seq[Def1_AnnualAdjustment]],
                       lossesBroughtForward: Option[Seq[Def1_LossBroughtForward]],
                       claims: Option[Seq[Def1_Claim]],
                       constructionIndustryScheme: Option[Seq[Def1_ConstructionIndustryScheme]], // This field name has been changed from downstream.
                       allowancesReliefsAndDeductions: Option[Seq[Def1_AllowancesReliefsAndDeductions]],
                       pensionContributionAndCharges: Option[Seq[Def1_PensionContributionAndCharges]],
                       other: Option[Seq[Def1_Other]]) {
  def withoutCessationDate: Def1_Inputs = copy(incomeSources = incomeSources.withoutCessationDate)

  def withoutCommencementDate: Def1_Inputs = copy(incomeSources = incomeSources.withoutCommencementDate)

  def withoutItsaStatus: Def1_Inputs = copy(personalInformation = personalInformation.withoutItsaStatus)
}

object Def1_Inputs {
  implicit val writes: OWrites[Def1_Inputs] = Json.writes[Def1_Inputs]

  implicit val reads: Reads[Def1_Inputs] = (
    (JsPath \ "personalInformation").read[Def1_PersonalInformation] and
      (JsPath \ "incomeSources").read[Def1_IncomeSources] and
      (JsPath \ "annualAdjustments").readNullable[Seq[Def1_AnnualAdjustment]] and
      (JsPath \ "lossesBroughtForward").readNullable[Seq[Def1_LossBroughtForward]] and
      (JsPath \ "claims").readNullable[Seq[Def1_Claim]] and
      (JsPath \ "cis").readNullable[Seq[Def1_ConstructionIndustryScheme]] and
      (JsPath \ "allowancesReliefsAndDeductions").readNullable[Seq[Def1_AllowancesReliefsAndDeductions]] and
      (JsPath \ "pensionContributionAndCharges").readNullable[Seq[Def1_PensionContributionAndCharges]] and
      (JsPath \ "other").readNullable[Seq[Def1_Other]]
  )(Def1_Inputs.apply _)

}
