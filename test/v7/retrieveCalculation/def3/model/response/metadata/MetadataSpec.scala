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

package v7.retrieveCalculation.def3.model.response.metadata

import play.api.libs.json.Json
import shared.models.domain.TaxYear
import shared.utils.UnitSpec

class MetadataSpec extends UnitSpec {

  "reads" when {
    "passed valid JSON with all fields populated" should {
      "return the correct model" in {
        Json
          .parse("""{
                  |    "calculationId": "calcId",
                  |    "taxYear": 2018,
                  |    "requestedBy": "customer",
                  |    "requestedTimestamp": "requested timestamp",
                  |    "calculationReason": "customerRequest",
                  |    "calculationTimestamp": "calc timestamp",
                  |    "calculationType": "IY",
                  |    "finalisationTimestamp": "final timestamp",
                  |    "confirmationTimestamp": "conf timestamp",
                  |    "periodFrom": "from",
                  |    "periodTo": "to"
                  |  }
                  |""".stripMargin)
          .as[Metadata] shouldBe
          Metadata(
            calculationId = "calcId",
            taxYear = TaxYear.fromDownstream("2018"),
            requestedBy = "customer",
            requestedTimestamp = Some("requested timestamp"),
            calculationReason = CalculationReason.`customer-request`,
            calculationTimestamp = Some("calc timestamp"),
            calculationType = Def3_CalculationType.`in-year`,
            finalisationTimestamp = Some("final timestamp"),
            confirmationTimestamp = Some("conf timestamp"),
            periodFrom = "from",
            periodTo = "to"
          )
      }
    }

    "only mandatory fields populated" should {
      "return model with mandatory fields only" in {
        Json
          .parse("""{
                   |    "calculationId": "calcId",
                   |    "taxYear": 2018,
                   |    "requestedBy": "customer",                  
                   |    "calculationReason": "customerRequest",
                   |    "calculationType": "IY",
                   |    "periodFrom": "from",
                   |    "periodTo": "to"
                   |  }
                   |""".stripMargin)
          .as[Metadata] shouldBe
          Metadata(
            calculationId = "calcId",
            taxYear = TaxYear.fromDownstream("2018"),
            requestedBy = "customer",
            requestedTimestamp = None,
            calculationReason = CalculationReason.`customer-request`,
            calculationTimestamp = None,
            calculationType = Def3_CalculationType.`in-year`,
            finalisationTimestamp = None,
            confirmationTimestamp = None,
            periodFrom = "from",
            periodTo = "to"
          )
      }
    }
  }

  "writes" should {
    "work" in {
      Json.toJson(
        Metadata(
          calculationId = "calcId",
          taxYear = TaxYear.fromDownstream("2018"),
          requestedBy = "customer",
          requestedTimestamp = Some("requested timestamp"),
          calculationReason = CalculationReason.`customer-request`,
          calculationTimestamp = Some("calc timestamp"),
          calculationType = Def3_CalculationType.`confirm-amendment`,
          finalisationTimestamp = Some("final timestamp"),
          confirmationTimestamp = Some("conf timestamp"),
          periodFrom = "from",
          periodTo = "to"
        )) shouldBe Json.parse("""{
                 |    "calculationId": "calcId",
                 |    "taxYear": "2017-18",
                 |    "requestedBy": "customer",
                 |    "requestedTimestamp": "requested timestamp",
                 |    "calculationReason": "customer-request",
                 |    "calculationTimestamp": "calc timestamp",
                 |    "calculationType": "confirm-amendment",
                 |    "finalisationTimestamp": "final timestamp",
                 |    "confirmationTimestamp": "conf timestamp",
                 |    "periodFrom": "from",
                 |    "periodTo": "to"
                 |  }
                 |""".stripMargin)
    }
  }

}
