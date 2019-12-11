package v1.fixtures.getIncomeTaxAndNics.detail

import play.api.libs.json.{JsValue, Json}
import v1.models.response.getIncomeTaxAndNics.detail.GiftAid

object GiftAidFixture {
  val json: JsValue = Json.parse(
    s"""
       |{
       | "grossGiftAidPayments": 100.25,
       | "rate": 200.25,
       | "giftAidTax": 300.25
       |}
           """.stripMargin)

  val model = GiftAid(100.25, 200.25, 300.25)
}
