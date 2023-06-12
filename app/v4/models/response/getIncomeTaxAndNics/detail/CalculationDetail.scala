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

package v4.models.response.getIncomeTaxAndNics.detail

import play.api.libs.json.{Json, OFormat}
import v4.models.response.getIncomeTaxAndNics.detail.capitalGainsTax.CapitalGainsTaxDetail
import v4.models.response.getIncomeTaxAndNics.detail.incomeTax.IncomeTaxDetail
import v4.models.response.getIncomeTaxAndNics.detail.marriageAllowanceTransferredIn.MarriageAllowanceTransferredIn
import v4.models.response.getIncomeTaxAndNics.detail.nics.NicDetail
import v4.models.response.getIncomeTaxAndNics.detail.pensionSavingsTaxCharges.PensionSavingsTaxCharges
import v4.models.response.getIncomeTaxAndNics.detail.studentLoans.StudentLoans
import v4.models.response.getIncomeTaxAndNics.detail.taxDeductedAtSource.TaxDeductedAtSource

case class CalculationDetail(incomeTax: IncomeTaxDetail,
                             studentLoans: Option[Seq[StudentLoans]],
                             pensionSavingsTaxCharges: Option[PensionSavingsTaxCharges],
                             nics: Option[NicDetail],
                             taxDeductedAtSource: Option[TaxDeductedAtSource],
                             capitalGainsTax: Option[CapitalGainsTaxDetail],
                             marriageAllowanceTransferredIn: Option[MarriageAllowanceTransferredIn])

object CalculationDetail {
  implicit val format: OFormat[CalculationDetail] = Json.format[CalculationDetail]
}
