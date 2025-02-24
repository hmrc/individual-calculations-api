/*
 * Copyright 2024 HM Revenue & Customs
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

package v8.retrieveCalculation.def3.model.response.inputs.incomeSources

import play.api.libs.json.{JsError, JsObject, JsValue, Json}
import shared.utils.UnitSpec
import v8.common.model.response.IncomeSourceType
import v8.common.model.response.IncomeSourceType._
import v8.retrieveCalculation.def3.model.response.inputs.{BusinessIncomeSource, SubmissionPeriod}

class BusinessIncomeSourceSpec extends UnitSpec {

  private def businessIncomeSourceJson(downstreamIncomeSourceType: String): JsValue = Json.parse(
    s"""
       |{
       |  "incomeSourceId": "123456789012345",
       |  "incomeSourceType": "$downstreamIncomeSourceType",
       |  "incomeSourceName": "string",
       |  "accountingPeriodStartDate": "2018-04-06",
       |  "accountingPeriodEndDate": "2019-04-05",
       |  "source": "MTD-SA",
       |  "commencementDate": "2018-04-06",
       |  "cessationDate": "2019-04-05",
       |  "latestPeriodEndDate": "2019-04-05",
       |  "latestReceivedDateTime": "2019-04-05T09:30:00Z",
       |  "submissionPeriod":
       |  {
       |  "submissionId": "001",
       |  "startDate": "2018-04-06",
       |  "endDate": "2019-04-05",
       |  "receivedDateTime": "2019-04-05T09:30:00Z"
       |  }
       |}
    """.stripMargin
  )

  private def businessIncomeSourceModel(mtdIncomeSourceType: IncomeSourceType) = BusinessIncomeSource(
    incomeSourceId = "123456789012345",
    incomeSourceType = mtdIncomeSourceType,
    incomeSourceName = Some("string"),
    accountingPeriodStartDate = "2018-04-06",
    accountingPeriodEndDate = "2019-04-05",
    source = "MTD-SA",
    commencementDate = Some("2018-04-06"),
    cessationDate = Some("2019-04-05"),
    latestPeriodEndDate = "2019-04-05",
    latestReceivedDateTime = "2019-04-05T09:30:00Z",
    submissionPeriod = Some(SubmissionPeriod(Some("001"), "2018-04-06", "2019-04-05", "2019-04-05T09:30:00Z"))
  )

  "BusinessIncomeSource" should {
    "serialise to Json" when {
      Seq(`self-employment`, `uk-property`, `foreign-property`).foreach { mtdIncomeSourceType =>
        s"the provided MTD IncomeSourceType is $mtdIncomeSourceType" in {
          Json.toJson(businessIncomeSourceModel(mtdIncomeSourceType)) shouldBe businessIncomeSourceJson(mtdIncomeSourceType.toString)
        }
      }
    }

    "deserialise from Json" when {
      Seq(("01", `self-employment`), ("02", `uk-property`), ("15", `foreign-property`))
        .foreach { case (downstreamIncomeSourceType, mtdIncomeSourceType) =>
          s"the provided downstream IncomeSourceType is $downstreamIncomeSourceType" in {
            businessIncomeSourceJson(downstreamIncomeSourceType).as[BusinessIncomeSource] shouldBe businessIncomeSourceModel(mtdIncomeSourceType)
          }
        }
    }

    "error when JSON is invalid" in {
      JsObject.empty.validate[BusinessIncomeSource] shouldBe a[JsError]
    }
  }

}
