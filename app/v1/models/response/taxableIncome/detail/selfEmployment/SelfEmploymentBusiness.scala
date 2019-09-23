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
import play.api.libs.json.{JsPath, Json, OWrites, Reads}
import utils.NestedJsonReads
import v1.models.response.getCalculation.taxableIncome.detail.selfEmployment.{LossClaimsDetail, LossClaimsSummary}

case class SelfEmploymentBusiness(
    selfEmploymentId: String,
    totalIncome: Option[BigDecimal],
    totalExpenses: Option[BigDecimal],
    netProfit: Option[BigDecimal],
    netLoss: Option[BigDecimal],
    class4loss: Option[BigDecimal],
    totalAdditions: Option[BigDecimal],
    totalDeductions: Option[BigDecimal],
    accountingAdjustments: Option[BigDecimal],
    adjustedIncomeTaxLoss: Option[BigDecimal],
    taxableProfit: Option[BigDecimal],
    taxableProfitAfterIncomeTaxLossesDeduction: Option[BigDecimal],
    lossClaimsSummary: Option[LossClaimsSummary],
    lossClaimsDetail: Option[LossClaimsDetail]
)

object SelfEmploymentBusiness extends NestedJsonReads {
  lazy val bpalPath: JsPath                            = JsPath \ "calculation" \ "businessProfitAndLoss"
  val safetyValue                                      = SelfEmploymentBusiness("test", None, None, None, None, None, None, None, None, None, None, None, None, None)
  implicit val writes: OWrites[SelfEmploymentBusiness] = Json.writes[SelfEmploymentBusiness]

  implicit val singleReads: Reads[SelfEmploymentBusiness] = ((JsPath \ "incomeSourceId").read[String] and
    (JsPath \ "totalIncome").readNullable[BigDecimal] and
    (JsPath \ "totalExpenses").readNullable[BigDecimal] and
    (JsPath \ "netProfit").readNullable[BigDecimal] and
    (JsPath \ "netLoss").readNullable[BigDecimal] and
    (JsPath \ "class4Loss").readNullable[BigDecimal] and
    (JsPath \ "totalAdditions").readNullable[BigDecimal] and
    (JsPath \ "totalDeductions").readNullable[BigDecimal] and
    (JsPath \ "accountingAdjustments").readNullable[BigDecimal] and
    (JsPath \ "adjustedIncomeTaxLoss").readNullable[BigDecimal] and
    (JsPath \ "taxableProfit").readNullable[BigDecimal] and
    (JsPath \ "taxableProfitAfterLossesDeduction").readNullable[BigDecimal] and
    JsPath.readNullable[LossClaimsSummary] and Reads.pure(None))(SelfEmploymentBusiness.apply _)
    .orElse(Reads.pure(SelfEmploymentBusiness.safetyValue))

  implicit val seqReads: Reads[Seq[SelfEmploymentBusiness]] = (bpalPath.readNestedNullable[Seq[SelfEmploymentBusiness]] and
    JsPath.readNullable[LossClaimsDetail])(filterAndBuild(_, _))

  def filterAndBuild(maybeBusinesses: Option[Seq[SelfEmploymentBusiness]], maybeValue: Option[LossClaimsDetail]): Seq[SelfEmploymentBusiness] = {
    maybeBusinesses
      .getOrElse(Seq())
      .map(seb => seb.copy(lossClaimsDetail = Some(maybeValue.getOrElse(LossClaimsDetail.emptyLossClaimsDetail))))
    // TODO: Add the 'incomeSourceId' field into each of the LossClaimsDetail sub-models and update the reads,
    // TODO: Write an explicit Writes function for each which doesn't write the incomeSourceId field,
    // TODO: Update the tests (+fixtures) to include the new field in the response, and desJson values,
    // TODO: An example filter is already implemented in LossClaimDetail,
    // TODO: Add a case in the reads for when LossClaimDetail is empty (map it to None so it isn't written)
  }

}
