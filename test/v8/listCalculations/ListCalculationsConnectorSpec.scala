/*
 * Copyright 2023 HM Revenue & Customs
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

package v8.listCalculations

import shared.connectors.{ConnectorSpec, DownstreamOutcome}
import shared.models.domain.{Nino, TaxYear}
import shared.models.errors.{DownstreamErrorCode, DownstreamErrors}
import shared.models.outcomes.ResponseWrapper
import v8.common.model.domain._
import v8.listCalculations.def1.model.Def1_ListCalculationsFixture
import v8.listCalculations.model.request.{Def1_ListCalculationsRequestData, Def2_ListCalculationsRequestData, Def3_ListCalculationsRequestData, ListCalculationsRequestData}
import v8.listCalculations.model.response.{Calculation, ListCalculationsResponse}

import scala.concurrent.Future

class ListCalculationsConnectorSpec extends ConnectorSpec with Def1_ListCalculationsFixture {

  val nino: Nino          = Nino("AA111111A")

  val def3TaxYear: TaxYear = TaxYear.fromMtd("2025-26")

  trait Test { _: ConnectorTest =>

    val taxYear: TaxYear
    val request: ListCalculationsRequestData
    val calculationType: Option[CalculationType]

    val connector: ListCalculationsConnector = new ListCalculationsConnector(
      http = mockHttpClient,
      appConfig = mockAppConfig
    )

  }

  "ListCalculationsConnector" should {
    "return successful response" when {
      "Pre 23/24 tax year path param is passed" in new DesTest with Test {
        val taxYear = TaxYear.fromMtd("2018-19")
        val calculationType = None

        val request: Def1_ListCalculationsRequestData    = Def1_ListCalculationsRequestData(nino, taxYear, calculationType)

        val outcome = Right(ResponseWrapper(correlationId, listCalculationsResponseModel))

        willGet(
          s"$baseUrl/income-tax/list-of-calculation-results/${nino.nino}?taxYear=2019"
        )
          .returns(Future.successful(outcome))

        await(connector.list(request)) shouldBe outcome
      }

      "a 23/24 or 24/25 tax year path param is passed" when {
        "an in year calcType query param is passed" in new IfsTest with Test {
          val taxYear = TaxYear.fromMtd("2023-24")
          val calculationType = Some(`in-year`)
          val request: Def2_ListCalculationsRequestData = Def2_ListCalculationsRequestData(nino, taxYear, calculationType)

          val outcome = Right(ResponseWrapper(correlationId, listCalculationsResponseModel))

          willGet(
            url = s"$baseUrl/income-tax/${taxYear.asTysDownstream}/view/calculations-summary/$nino",
            parameters = Seq("calculationType" -> "IY")
          )
            .returns(Future.successful(outcome))

          await(connector.list(request)) shouldBe outcome
        }
        "an intent to finalise calcType query param is passed" in new IfsTest with Test {
          val taxYear = TaxYear.fromMtd("2023-24")
          val calculationType = Some(`intent-to-finalise`)
          val request: Def2_ListCalculationsRequestData = Def2_ListCalculationsRequestData(nino, taxYear, calculationType)

          val outcome = Right(ResponseWrapper(correlationId, listCalculationsResponseModel))

          willGet(
            s"$baseUrl/income-tax/${taxYear.asTysDownstream}/view/calculations-summary/$nino",
            Seq("calculationType" -> "IC")
          )
            .returns(Future.successful(outcome))

          await(connector.list(request)) shouldBe outcome
        }
        "a final declaration calcType query param is passed" in new IfsTest with Test {
          val taxYear = TaxYear.fromMtd("2023-24")
          val calculationType = Some(`final-declaration`)
          val request: Def2_ListCalculationsRequestData = Def2_ListCalculationsRequestData(nino, taxYear, calculationType)

          val outcome = Right(ResponseWrapper(correlationId, listCalculationsResponseModel))

          willGet(
            s"$baseUrl/income-tax/${taxYear.asTysDownstream}/view/calculations-summary/$nino",
            Seq("calculationType" -> "CR")
          )
            .returns(Future.successful(outcome))

          await(connector.list(request)) shouldBe outcome
        }
      }

      "a post 25/26 tax year path param is passed" when {
        "an in year calcType query param is passed" in new IfsTest with Test {
          val taxYear = TaxYear.fromMtd("2025-26")
          val calculationType = Some(`in-year`)
          val request: Def3_ListCalculationsRequestData = Def3_ListCalculationsRequestData(nino, taxYear, calculationType)

          val outcome = Right(ResponseWrapper(correlationId, listCalculationsResponseModel))

          willGet(
            s"$baseUrl/income-tax/${taxYear.asTysDownstream}/view/$nino/calculations-summary",
            Seq("calculationType" -> "IY")
          )
            .returns(Future.successful(outcome))

          await(connector.list(request)) shouldBe outcome
        }
        "an intent to finalise calcType query param is passed" in new IfsTest with Test {
          val taxYear = TaxYear.fromMtd("2025-26")
          val calculationType = Some(`intent-to-finalise`)
          val request: Def3_ListCalculationsRequestData = Def3_ListCalculationsRequestData(nino, taxYear, calculationType)

          val outcome = Right(ResponseWrapper(correlationId, listCalculationsResponseModel))

          willGet(
            s"$baseUrl/income-tax/${taxYear.asTysDownstream}/view/$nino/calculations-summary",
            Seq("calculationType" -> "IF")
          )
            .returns(Future.successful(outcome))

          await(connector.list(request)) shouldBe outcome
        }
        "a final declaration calcType query param is passed" in new IfsTest with Test {
          val taxYear = TaxYear.fromMtd("2025-26")
          val calculationType = Some(`final-declaration`)
          val request: Def3_ListCalculationsRequestData = Def3_ListCalculationsRequestData(nino, taxYear, calculationType)

          val outcome = Right(ResponseWrapper(correlationId, listCalculationsResponseModel))

          willGet(
            s"$baseUrl/income-tax/${taxYear.asTysDownstream}/view/$nino/calculations-summary",
            Seq("calculationType" -> "DF")
          )
            .returns(Future.successful(outcome))

          await(connector.list(request)) shouldBe outcome
        }
        "an intent to amend calcType query param is passed" in new IfsTest with Test {
          val taxYear = TaxYear.fromMtd("2025-26")
          val calculationType = Some(`intent-to-amend`)
          val request: Def3_ListCalculationsRequestData = Def3_ListCalculationsRequestData(nino, taxYear, calculationType)

          val outcome = Right(ResponseWrapper(correlationId, listCalculationsResponseModel))

          willGet(
            s"$baseUrl/income-tax/${taxYear.asTysDownstream}/view/$nino/calculations-summary",
            Seq("calculationType" -> "IA")
          )
            .returns(Future.successful(outcome))

          await(connector.list(request)) shouldBe outcome
        }
        "a confirm amendment calcType query param is passed" in new IfsTest with Test {
          val taxYear = TaxYear.fromMtd("2025-26")
          val calculationType = Some(`confirm-amendment`)
          val request: Def3_ListCalculationsRequestData = Def3_ListCalculationsRequestData(nino, taxYear, calculationType)

          val outcome = Right(ResponseWrapper(correlationId, listCalculationsResponseModel))

          willGet(
            s"$baseUrl/income-tax/${taxYear.asTysDownstream}/view/$nino/calculations-summary",
            Seq("calculationType" -> "CA")
          )
            .returns(Future.successful(outcome))

          await(connector.list(request)) shouldBe outcome
        }
      }
    }

    "an error is received" must {
      "return the expected result" in new DesTest with Test {
        val taxYear = TaxYear.fromMtd("2018-19")
        val calculationType = None

        val request: Def1_ListCalculationsRequestData = Def1_ListCalculationsRequestData(nino, taxYear, calculationType)
        val outcome = Left(ResponseWrapper(correlationId, DownstreamErrors.single(DownstreamErrorCode("ERROR_CODE"))))

        willGet(
          s"$baseUrl/income-tax/list-of-calculation-results/${nino.nino}?taxYear=2019"
        )
          .returns(Future.successful(outcome))

        private val result: DownstreamOutcome[ListCalculationsResponse[Calculation]] = await(connector.list(request))
        result shouldBe outcome
      }
    }
  }

}
