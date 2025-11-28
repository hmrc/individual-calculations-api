package v8.retrieveCalculation.def4.model.response.calculation.foreignPropertyIncome

import play.api.libs.json.{JsError, JsObject, JsValue, Json}
import shared.utils.UnitSpec
import v8.common.model.response.IncomeSourceType
import v8.common.model.response.IncomeSourceType.`foreign-property`

class ForeignPropertyIncomeSpec extends UnitSpec {

  val downstreamJson: JsValue = Json.parse(s"""
      |{
      |    "incomeSourceId": "000000000000210",
      |    "incomeSourceType": "15",
      |    "countryCode": "FRA",
      |    "totalIncome": 5000.99,
      |    "totalExpenses": 5000.99,
      |    "netProfit": 5000.99,
      |    "netLoss": 5000.99,
      |    "totalAdditions": 5000.99,
      |    "totalDeductions": 5000.99,
      |    "adjustedProfit": 5000.99,
      |    "adjustedLoss": 5000.99,
      |    "foreignTaxPaid": 5000.99,
      |    "taxableProfit": 5000.99,
      |    "adjustedIncomeTaxLoss": 5000.99,
      |    "propertyLevelDetail": [
      |        {
      |            "propertyId": "8e8b8450-dc1b-4360-8109-7067337b42cb",
      |            "totalIncome": 5000.99,
      |            "totalExpenses": -99999999999.99,
      |            "netProfit": 5000.99,
      |            "netLoss": 5000.99,
      |            "totalAdditions": 5000.99,
      |            "totalDeductions": 5000.99,
      |            "adjustedProfit": 5000.99,
      |            "adjustedLoss": 5000.99,
      |            "foreignTaxPaid": 5000.99,
      |            "foreignTaxCreditRelief": true,
      |            "taxableProfit": 5000.99,
      |            "adjustedIncomeTaxLoss": 5000.99
      |        }
      |    ]
      |}
      |""".stripMargin)

  val model: ForeignPropertyIncome =
    ForeignPropertyIncome(
      incomeSourceId = "000000000000210",
      incomeSourceType = `foreign-property`,
      countryCode = "FRA",
      totalIncome = Some(5000.99),
      totalExpenses = Some(5000.99),
      netProfit = Some(5000.99),
      netLoss = Some(5000.99),
      totalAdditions = Some(5000.99),
      totalDeductions = Some(5000.99),
      taxableProfit = Some(5000.99),
      adjustedIncomeTaxLoss = Some(5000.99)
    )

  val mtdJson: JsValue = Json.parse(s"""
       |{
       |    "incomeSourceId": "000000000000210",
       |    "incomeSourceType": "foreign-property",
       |    "countryCode": "FRA",
       |    "totalIncome": 5000.99,
       |    "totalExpenses": 5000.99,
       |    "netProfit": 5000.99,
       |    "netLoss": 5000.99,
       |    "totalAdditions": 5000.99,
       |    "totalDeductions": 5000.99,
       |    "adjustedProfit": 5000.99,
       |    "adjustedLoss": 5000.99,
       |    "foreignTaxPaid": 5000.99,
       |    "taxableProfit": 5000.99,
       |    "adjustedIncomeTaxLoss": 5000.99,
       |    "propertyLevelDetail": [
       |        {
       |            "propertyId": "8e8b8450-dc1b-4360-8109-7067337b42cb",
       |            "totalIncome": 5000.99,
       |            "totalExpenses": -99999999999.99,
       |            "netProfit": 5000.99,
       |            "netLoss": 5000.99,
       |            "totalAdditions": 5000.99,
       |            "totalDeductions": 5000.99,
       |            "adjustedProfit": 5000.99,
       |            "adjustedLoss": 5000.99,
       |            "foreignTaxPaid": 5000.99,
       |            "foreignTaxCreditRelief": true,
       |            "taxableProfit": 5000.99,
       |            "adjustedIncomeTaxLoss": 5000.99
       |        }
       |    ]
       |}
       |""".stripMargin)

  "reads" should {
    "successfully read in a model" when {

      s"provided downstream income source type '15'" in {
        downstreamJson.as[ForeignPropertyIncome] shouldBe model
      }
    }
  }

  "writes" should {
    "successfully write a model to json" when {

      s"provided income source type 'foreign-property" in {
        Json.toJson(model) shouldBe mtdJson
      }

    }
  }

  "error when JSON is invalid" in {
    JsObject.empty.validate[ForeignPropertyIncome] shouldBe a[JsError]
  }

}
