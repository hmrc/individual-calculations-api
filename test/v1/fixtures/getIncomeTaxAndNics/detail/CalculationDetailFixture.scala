/*
 * Copyright 2021 HM Revenue & Customs
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

package v1.fixtures.getIncomeTaxAndNics.detail

import play.api.libs.json.{JsValue, Json}
import v1.fixtures.getIncomeTaxAndNics.detail.IncomeTaxDetailFixture._
import v1.fixtures.getIncomeTaxAndNics.detail.NicDetailFixture._
import v1.fixtures.getIncomeTaxAndNics.detail.PensionSavingsTaxChargesFixture._
import v1.fixtures.getIncomeTaxAndNics.detail.StudentLoansFixture._
import v1.fixtures.getIncomeTaxAndNics.detail.TaxDeductedAtSourceFixture._
import v1.models.response.getIncomeTaxAndNics.detail._

object CalculationDetailFixture {

  val calculationDetailModel: CalculationDetail =
    CalculationDetail(
      incomeTax = incomeTaxDetailModel,
      studentLoans = Some(Seq(studentLoansModel)),
      pensionSavingsTaxCharges = Some(pensionSavingsTaxChargesModel),
      nics = Some(nicDetailModel),
      taxDeductedAtSource = Some(taxDeductedAtSourceModel)
    )

  val calculationDetailJson: JsValue = Json.parse(
    s"""
       |{
       |   "incomeTax" : $incomeTaxDetailJson,
       |   "studentLoans" : [$studentLoansJson],
       |   "pensionSavingsTaxCharges" : $pensionSavingsTaxChargesJson,
       |   "nics" : $nicDetailJson,
       |   "taxDeductedAtSource" : $taxDeductedAtSourceJson
       |}
    """.stripMargin
  )
}
