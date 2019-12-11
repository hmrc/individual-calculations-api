package v1.fixtures.getIncomeTaxAndNics.detail

import play.api.libs.json.Json
import v1.models.response.getIncomeTaxAndNics.detail.Class2NicDetail

object Class2NicDetailFixture {

  val model = Class2NicDetail(Some(100.25), Some(200.25), Some(300.25), Some(400.25), true, Some(false))

  val json = Json.parse(
    """
      |{
      | "weeklyRate" : 100.25,
      | "weeks" : 200.25,
      | "limit" : 300.25,
      | "apportionedLimit" : 400.25,
      | "underSmallProfitThreshold" : true,
      | "actualClass2Nic" : false
      |}
    """.stripMargin)
}
