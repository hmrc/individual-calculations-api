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

package v3.connectors

import v3.models.domain.{EmptyJsonBody, Nino, TaxYear}
import v3.models.outcomes.ResponseWrapper
import v3.models.request.SubmitFinalDeclarationRequest

import scala.concurrent.Future

class SubmitFinalDeclarationConnectorSpec extends ConnectorSpec {

  val nino: String          = "AA111111A"
  val taxYear: TaxYear      = TaxYear.fromMtd("2020-21")
  val calculationId: String = "4557ecb5-fd32-48cc-81f5-e6acd1099f3c"

  val request: SubmitFinalDeclarationRequest = SubmitFinalDeclarationRequest(
    Nino(nino),
    taxYear,
    calculationId
  )

  trait Test {
    _: ConnectorTest =>

    val connector: SubmitFinalDeclarationConnector = new SubmitFinalDeclarationConnector(
      http = mockHttpClient,
      appConfig = mockAppConfig
    )

  }

  "Submit Final Declaration" should {
    "return a success response when request is made to DES downstream API" in new DesTestSubmitFinalDeclaration with Test {
      val outcome: Right[Nothing, ResponseWrapper[Unit]] = Right(ResponseWrapper(correlationId, {}))

      willPost(
        url = s"$baseUrl/income-tax/calculation/nino/$nino/${taxYear.asDownstream}/$calculationId/crystallise",
        body = EmptyJsonBody
      )
        .returns(Future.successful(outcome))

      val result: DownstreamOutcome[Unit] = await(connector.submitFinalDeclaration(request))
      result shouldBe outcome
    }

    "return a success response when request is made to IFS downstream API" in new IfsTestSubmitFinalDeclaration with Test {
      val outcome: Right[Nothing, ResponseWrapper[Unit]] = Right(ResponseWrapper(correlationId, {}))

      willPost(
        url = s"$baseUrl/income-tax/${taxYear.asTysDownstream}/calculation/$nino/$calculationId/crystallise",
        body = EmptyJsonBody
      )
        .returns(Future.successful(outcome))

      val result: DownstreamOutcome[Unit] = await(connector.submitFinalDeclaration(request))
      result shouldBe outcome
    }
  }

}
