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

package v1.fixtures.getEndOfYearEstimate.detail

import play.api.libs.json.{JsValue, Json}
import v1.fixtures.getEndOfYearEstimate.detail.EoyEstimateForeignInterestFixture._
import v1.fixtures.getEndOfYearEstimate.detail.EoyEstimateForeignPropertyFixture._
import v1.fixtures.getEndOfYearEstimate.detail.EoyEstimateOtherDividendsFixture._
import v1.fixtures.getEndOfYearEstimate.detail.EoyEstimateSelfEmploymentsFixture._
import v1.fixtures.getEndOfYearEstimate.detail.EoyEstimateStateBenefitsFixture._
import v1.fixtures.getEndOfYearEstimate.detail.EoyEstimateUkDividendsFixture._
import v1.fixtures.getEndOfYearEstimate.detail.EoyEstimateUkPropertyFhlFixture._
import v1.fixtures.getEndOfYearEstimate.detail.EoyEstimateUkPropertyNonFhlFixture._
import v1.fixtures.getEndOfYearEstimate.detail.EoyEstimateUkSavingsFixture._
import v1.fixtures.getEndOfYearEstimate.detail.EoyEstimateUkSecuritiesFixture._
import v1.models.response.getEoyEstimate.detail.EoyEstimateDetail

object EoyEstimateDetailFixture {

  val eoyEstimateDetailModel: EoyEstimateDetail =
    EoyEstimateDetail(
      selfEmployments = Some(Seq(eoyEstimateSelfEmploymentsModel)),
      ukPropertyFhl = Some(eoyEstimateUkPropertyFhlModel),
      ukPropertyNonFhl = Some(eoyEstimateUkPropertyNonFhlModel),
      ukSavings = Some(Seq(eoyEstimateUkSavingsModel)),
      ukDividends = Some(eoyEstimateUkDividendsModel),
      otherDividends = Some(eoyEstimateOtherDividendsModel),
      stateBenefits = Some(eoyEstimateStateBenefitsModel),
      ukSecurities = Some(eoyEstimateUkSecuritiesModel),
      foreignProperty = Some(eoyEstimateForeignPropertyModel),
      foreignInterest = Some(eoyEstimateForeignInterestModel)
    )

  val eoyEstimateDetailJson: JsValue = Json.parse(
    s"""
       |{
       | "selfEmployments" : [
       |   $eoyEstimateSelfEmploymentsJson
       | ],
       | "ukPropertyFhl" : $eoyEstimateUkPropertyFhlJson,
       | "ukPropertyNonFhl" : $eoyEstimateUkPropertyNonFhlJson,
       | "ukSavings" : [
       |   $eoyEstimateUkSavingsJson
       | ],
       | "ukDividends" : $eoyEstimateUkDividendsJson,
       | "otherDividends" : $eoyEstimateOtherDividendsJson,
       | "stateBenefits" : $eoyEstimateStateBenefitsJson,
       | "ukSecurities" : $eoyEstimateUkSecuritiesJson,
       | "foreignProperty" : $eoyEstimateForeignPropertyJson,
       | "foreignInterest" : $eoyEstimateForeignInterestJson
       |}
    """.stripMargin
  )
}