package v1.fixtures.getIncomeTaxAndNics.detail

import play.api.libs.json.{JsValue, Json}
import v1.models.response.getIncomeTaxAndNics.detail._

object CalculationDetailFixture {

  val incomeTaxDetail = IncomeTaxDetail(Some(IncomeTypeBreakdown(100.25, 200.25, None)), None, None, None)
  val nicDetail = NicDetail(Some(Class2NicDetail(Some(300.25), None, None, None, true, Some(false))), None)
  val taxDeductedAtSource = TaxDeductedAtSource(Some(400.25), None)

  val json: JsValue = Json.parse(
    s"""
       |{
       | "incomeTax" : ${Json.toJson(incomeTaxDetail).toString()},
       | "nics" : ${Json.toJson(nicDetail).toString()},
       | "taxDeductedAtSource" : ${Json.toJson(taxDeductedAtSource).toString()}
       |}
    """.stripMargin)

  val model = CalculationDetail(incomeTaxDetail, Some(nicDetail), Some(taxDeductedAtSource))
}
