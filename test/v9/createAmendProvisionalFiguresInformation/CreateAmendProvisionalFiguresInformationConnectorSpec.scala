/*
 * Copyright 2026 HM Revenue & Customs
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

package v9.createAmendProvisionalFiguresInformation

import api.connectors.{ConnectorSpec, DownstreamOutcome}
import api.models.domain.{Nino, TaxYear}
import api.models.errors.{DownstreamErrorCode, DownstreamErrors}
import api.models.outcomes.ResponseWrapper
import uk.gov.hmrc.http.StringContextOps
import v9.createAmendProvisionalFiguresInformation.model.request.{
  CreateAmendProvisionalFiguresInformationRequestBody,
  CreateAmendProvisionalFiguresInformationRequestData
}

import scala.concurrent.Future

class CreateAmendProvisionalFiguresInformationConnectorSpec extends ConnectorSpec {

  private val nino: String    = "AA123456A"
  private val taxYear: String = "2026-27"

  private val body: CreateAmendProvisionalFiguresInformationRequestBody =
    CreateAmendProvisionalFiguresInformationRequestBody(
      provisionalFiguresIndicator = true,
      expectedDate = Some("2027-10-05"),
      supplementaryInformation = Some("Provisional figures are being confirmed.")
    )

  private val request: CreateAmendProvisionalFiguresInformationRequestData =
    CreateAmendProvisionalFiguresInformationRequestData(Nino(nino), TaxYear.fromMtd(taxYear), body)

  "CreateAmendProvisionalFiguresInformationConnector" should {
    "return a success response" when {
      "the downstream call is successful" in new HipTest with Test {
        val outcome: Right[Nothing, ResponseWrapper[Unit]] = Right(ResponseWrapper(correlationId, ()))

        willPut(url"$baseUrl/itsd/provisional-figures/$nino?taxYear=26-27", body)
          .returns(Future.successful(outcome))

        val result: DownstreamOutcome[Unit] = await(connector.createAmendProvisionalFiguresInformation(request))
        result shouldBe outcome
      }
    }

    "return an unsuccessful response" when {
      "the downstream call is unsuccessful" in new HipTest with Test {
        val outcome: Left[ResponseWrapper[DownstreamErrors], Nothing] =
          Left(ResponseWrapper(correlationId, DownstreamErrors.single(DownstreamErrorCode("SOME_ERROR"))))

        willPut(url"$baseUrl/itsd/provisional-figures/$nino?taxYear=26-27", body)
          .returns(Future.successful(outcome))

        val result: DownstreamOutcome[Unit] = await(connector.createAmendProvisionalFiguresInformation(request))
        result shouldBe outcome
      }
    }
  }

  private trait Test { self: ConnectorTest =>

    protected val connector: CreateAmendProvisionalFiguresInformationConnector =
      new CreateAmendProvisionalFiguresInformationConnector(http = mockHttpClient, appConfig = mockAppConfig)

  }

}
