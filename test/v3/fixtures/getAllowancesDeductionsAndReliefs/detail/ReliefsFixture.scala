/*
 * Copyright 2022 HM Revenue & Customs
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

package v3.fixtures.getAllowancesDeductionsAndReliefs.detail

import play.api.libs.json.{JsValue, Json}
import v3.fixtures.getAllowancesDeductionsAndReliefs.detail.ForeignTaxCreditReliefFixture._
import v3.fixtures.getAllowancesDeductionsAndReliefs.detail.PensionContributionReliefsFixture._
import v3.fixtures.getAllowancesDeductionsAndReliefs.detail.ReliefsClaimedFixture._
import v3.fixtures.getAllowancesDeductionsAndReliefs.detail.ResidentialFinanceCostsFixture._
import v3.fixtures.getAllowancesDeductionsAndReliefs.detail.TopSlicingReliefFixture.{topSlicingReliefJson, topSlicingReliefModel}
import v3.models.response.getAllowancesDeductionsAndReliefs.detail.Reliefs

object ReliefsFixture {

  val reliefsModel: Reliefs = Reliefs(
    residentialFinanceCosts = Some(residentialFinanceCostsModel),
    foreignTaxCreditRelief = Some(foreignTaxCreditReliefModel),
    pensionContributionReliefs = Some(pensionContributionReliefsModel),
    reliefsClaimed = Some(Seq(reliefsClaimedModel)),
    topSlicingRelief = Some(topSlicingReliefModel)
  )

  val reliefsJson: JsValue = Json.parse(
    s"""
       |{
       |  "residentialFinanceCosts": $residentialFinanceCostsJson,
       |  "foreignTaxCreditRelief": $foreignTaxCreditReliefJson,
       |  "pensionContributionReliefs": $pensionContributionReliefsJson,
       |  "reliefsClaimed": [$reliefsClaimedJson],
       |  "topSlicingRelief": $topSlicingReliefJson
       |}
     """.stripMargin
  )
}