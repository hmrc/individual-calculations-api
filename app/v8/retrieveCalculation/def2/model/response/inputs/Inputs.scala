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

package v8.retrieveCalculation.def2.model.response.inputs

import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Json, OWrites, Reads}

case class Inputs(personalInformation: PersonalInformation,
                  incomeSources: IncomeSources,
                  annualAdjustments: Option[Seq[AnnualAdjustment]],
                  lossesBroughtForward: Option[Seq[LossBroughtForward]],
                  claims: Option[Seq[Claim]],
                  constructionIndustryScheme: Option[Seq[ConstructionIndustryScheme]], // This field name has been changed from downstream.
                  allowancesReliefsAndDeductions: Option[Seq[AllowancesReliefsAndDeductions]],
                  pensionContributionAndCharges: Option[Seq[PensionContributionAndCharges]],
                  other: Option[Seq[Other]])

object Inputs {
  implicit val writes: OWrites[Inputs] = Json.writes[Inputs]

  implicit val reads: Reads[Inputs] = (
    (JsPath \ "personalInformation").read[PersonalInformation] and
      (JsPath \ "incomeSources").read[IncomeSources] and
      (JsPath \ "annualAdjustments").readNullable[Seq[AnnualAdjustment]] and
      (JsPath \ "lossesBroughtForward").readNullable[Seq[LossBroughtForward]] and
      (JsPath \ "claims").readNullable[Seq[Claim]] and
      (JsPath \ "cis").readNullable[Seq[ConstructionIndustryScheme]] and
      (JsPath \ "allowancesReliefsAndDeductions").readNullable[Seq[AllowancesReliefsAndDeductions]] and
      (JsPath \ "pensionContributionAndCharges").readNullable[Seq[PensionContributionAndCharges]] and
      (JsPath \ "other").readNullable[Seq[Other]]
  )(Inputs.apply)

}
