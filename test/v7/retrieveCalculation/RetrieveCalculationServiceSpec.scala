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

package v7.retrieveCalculation

import shared.models.domain.{CalculationId, Nino, TaxYear}
import shared.models.errors._
import shared.models.outcomes.ResponseWrapper
import shared.services.ServiceSpec
import uk.gov.hmrc.http.HeaderCarrier
import v7.retrieveCalculation.def1.model.Def1_CalculationFixture
import v7.retrieveCalculation.downstreamErrorMapping.RetrieveCalculationDownstreamErrorMapping._
import v7.retrieveCalculation.downstreamErrorMapping.RetrieveCalculationDownstreamErrorMapping
import v7.retrieveCalculation.models.request.{Def1_RetrieveCalculationRequestData, RetrieveCalculationRequestData}
import v7.retrieveCalculation.models.response.RetrieveCalculationResponse

import scala.concurrent.Future

class RetrieveCalculationServiceSpec extends ServiceSpec with Def1_CalculationFixture {

  private val nino: Nino = Nino("ZG903729C")
  private val calculationId: CalculationId = CalculationId("someCalcId")

  def request(taxYear: String): RetrieveCalculationRequestData = Def1_RetrieveCalculationRequestData(
    nino = nino,
    taxYear = TaxYear.fromMtd(taxYear),
    calculationId = calculationId
  )
  val response: RetrieveCalculationResponse   = minimalCalculationResponse

  trait Test extends MockRetrieveCalculationConnector {
    implicit val hc: HeaderCarrier = HeaderCarrier()

    val service = new RetrieveCalculationService(mockConnector)
  }

  "RetrieveCalculationService" when {
    "retrieveCalculation" should {
      "return a valid response" when {
        "a valid request is supplied" in new Test {
          MockRetrieveCalculationConnector
            .retrieveCalculation(request("2025-26"))
            .returns(Future.successful(Right(ResponseWrapper(correlationId, response))))

          await(service.retrieveCalculation(request("2025-26"))) shouldBe Right(ResponseWrapper(correlationId, response))
        }
      }

      "return error response" when {
        def serviceError(taxYear: String, downstreamErrorMapping: RetrieveCalculationDownstreamErrorMapping): Unit = {
          val fullDownstreamErrorMap: Map[String, MtdError] = downstreamErrorMapping.errorMap ++ Map(
            "UNMATCHED_STUB_ERROR" -> RuleIncorrectGovTestScenarioError
          )
          fullDownstreamErrorMap.foreach { case (downstreamErrorCode, expectedError) =>
            s"the $downstreamErrorCode error for tax year $taxYear is returned from the service" in new Test {
              MockRetrieveCalculationConnector
                .retrieveCalculation(request(taxYear))
                .returns(Future.successful(
                  Left(ResponseWrapper(correlationId, DownstreamErrors.single(DownstreamErrorCode(downstreamErrorCode))))
                ))

              await(service.retrieveCalculation(request(taxYear))) shouldBe Left(ErrorWrapper(correlationId, expectedError))
            }
          }
        }

        val input: Seq[(String, RetrieveCalculationDownstreamErrorMapping)] = Seq(
          ("2022-23", Api1523),
          ("2023-24", Api1885),
          ("2024-25", Api1885),
          ("2025-26", Api1885)
        )

        input.foreach(args => (serviceError _).tupled(args))
      }
    }
  }
}
