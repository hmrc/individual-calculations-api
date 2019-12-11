package v1.fixtures.getIncomeTaxAndNics.detail

import play.api.libs.json.{JsValue, Json}
import v1.models.response.getIncomeTaxAndNics.detail.{Class4Losses, Class4NicDetail, NicBand}

object Class4NicDetailFixture {

  val model = Class4NicDetail(
    Some(Class4Losses(Some(3001), Some(3002), Some(3003))),
    Some(3003),
    Some(3004),
    Some(
      Seq(NicBand(
        name = "name",
        rate = 100.25,
        threshold = Some(200),
        apportionedThreshold = Some(300),
        income = 400,
        amount = 500.25
      )))
  )

  val json: JsValue = Json.parse("""{
     | "class4Losses" : {
     | "totalClass4LossesAvailable" : 3001,
     | "totalClass4LossesUsed" : 3002,
     | "totalClass4LossesCarriedForward" : 3003
     | },
     | "totalIncomeLiableToClass4Charge" : 3003,
     | "totalIncomeChargeableToClass4" :3004,
     |	"class4NicBands": [{
     |					"name": "name",
     |					"rate": 100.25,
     |					"threshold": 200,
     |					"apportionedThreshold": 300,
     |					"income": 400,
     |					"amount": 500.25
     |				}]
     |}""".stripMargin)
}
