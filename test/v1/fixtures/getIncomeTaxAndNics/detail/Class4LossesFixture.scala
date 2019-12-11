package v1.fixtures.getIncomeTaxAndNics.detail

import play.api.libs.json.{JsValue, Json}
import v1.models.response.getIncomeTaxAndNics.detail.Class4Losses

object Class4LossesFixture {

  val model: Class4Losses = Class4Losses(Some(3001), Some(3002), Some(3003))

  val json: JsValue = Json.parse("""{
                                   | "totalClass4LossesAvailable" : 3001,
                                   | "totalClass4LossesUsed" : 3002,
                                   | "totalClass4LossesCarriedForward" : 3003
                                   |}""".stripMargin)
}
