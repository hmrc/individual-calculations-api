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

package v1.fixtures.getIncomeTaxAndNics.detail

import play.api.libs.json.{JsValue, Json}
import v1.fixtures.getIncomeTaxAndNics.detail.Class2NicDetailFixture._
import v1.fixtures.getIncomeTaxAndNics.detail.Class4NicDetailFixture._
import v1.models.response.getIncomeTaxAndNics.detail._

object NicDetailFixture {

  val nicDetailModel: NicDetail =
    NicDetail(
      class2Nics = Some(class2NicDetailModel),
      class4Nics = Some(class4NicDetailModel)
    )

  val nicDetailJson: JsValue = Json.parse(
    s"""{
       |	"class2Nics": ${class2NicDetailJson.toString()},
       |	"class4Nics": ${class4NicDetailJson.toString()}
       |}
    """.stripMargin
  )
}