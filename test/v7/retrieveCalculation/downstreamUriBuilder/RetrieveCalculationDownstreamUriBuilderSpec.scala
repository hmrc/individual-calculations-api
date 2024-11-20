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

package v7.retrieveCalculation.downstreamUriBuilder

import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks
import shared.config.{DownstreamConfig, MockAppConfig}
import shared.connectors.DownstreamUri
import shared.models.domain.{CalculationId, Nino, TaxYear, TaxYearPropertyCheckSupport}
import shared.utils.UnitSpec
import v7.retrieveCalculation.downstreamUriBuilder.RetrieveCalculationDownstreamUriBuilder._
import v7.retrieveCalculation.models.response.RetrieveCalculationResponse

import scala.concurrent.ExecutionContext.Implicits.global

class RetrieveCalculationDownstreamUriBuilderSpec
  extends UnitSpec
    with ScalaCheckDrivenPropertyChecks
    with TaxYearPropertyCheckSupport
    with MockAppConfig
    with ScalaFutures {

  private val nino: Nino = Nino("AA123456A")
  private val calculationId: CalculationId = CalculationId("f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c")
  private val correlationId: String = "a1e8057e-fbbc-47a8-a8b4-78d9f015c253"

  private val ifsDownstreamConfig: DownstreamConfig = DownstreamConfig("ifsBaseUrl", "ifsEnv", "ifsToken", None)
  private val tysIfsDownstreamConfig: DownstreamConfig = DownstreamConfig("tysIfsBaseUrl", "tysIfsEnv", "tysIfsToken", None)

  private def contractHeaders(downstreamConfig: DownstreamConfig): Seq[(String, String)] = Seq(
    "Authorization" -> s"Bearer ${downstreamConfig.token}",
    "Environment"   -> downstreamConfig.env,
    "CorrelationId" -> correlationId
  )

  private trait Test {
    MockedAppConfig.ifsDownstreamConfig.anyNumberOfTimes() returns ifsDownstreamConfig
    MockedAppConfig.tysIfsDownstreamConfig.anyNumberOfTimes() returns tysIfsDownstreamConfig
  }

  "RetrieveCalculationDownstreamUriBuilder" should {
    "return correct URI builder, path and strategy for Non TYS tax years" in new Test {
      forPreTysTaxYears { taxYear =>
        val downstreamUriBuilder: RetrieveCalculationDownstreamUriBuilder[RetrieveCalculationResponse] =
          RetrieveCalculationDownstreamUriBuilder.downstreamUriFor[RetrieveCalculationResponse](taxYear)

        downstreamUriBuilder shouldBe Api1523

        val uri: DownstreamUri[RetrieveCalculationResponse] =
          downstreamUriBuilder.buildUri(nino, taxYear, calculationId)

        uri.path shouldBe s"income-tax/view/calculations/liability/$nino/$calculationId"
        uri.strategy.baseUrl shouldBe ifsDownstreamConfig.baseUrl
        uri.strategy.contractHeaders(correlationId).futureValue shouldBe contractHeaders(ifsDownstreamConfig)
      }
    }

    "return correct URI builder, path and strategy for TYS tax years" in new Test {
      forTaxYearsFrom(TaxYear.fromMtd("2023-24")) { taxYear =>
        val downstreamUriBuilder: RetrieveCalculationDownstreamUriBuilder[RetrieveCalculationResponse] =
          RetrieveCalculationDownstreamUriBuilder.downstreamUriFor[RetrieveCalculationResponse](taxYear)

        downstreamUriBuilder shouldBe Api1885

        val uri: DownstreamUri[RetrieveCalculationResponse] =
          downstreamUriBuilder.buildUri(nino, taxYear, calculationId)

        uri.path shouldBe s"income-tax/view/calculations/liability/${taxYear.asTysDownstream}/$nino/$calculationId"
        uri.strategy.baseUrl shouldBe tysIfsDownstreamConfig.baseUrl
        uri.strategy.contractHeaders(correlationId).futureValue shouldBe contractHeaders(tysIfsDownstreamConfig)
      }
    }
  }
}
