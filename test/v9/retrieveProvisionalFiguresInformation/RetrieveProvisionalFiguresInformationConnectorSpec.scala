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

package v9.retrieveProvisionalFiguresInformation

import api.connectors.{ConnectorSpec, DownstreamOutcome}
import api.models.domain.{Nino, TaxYear}
import api.models.errors.{DownstreamErrorCode, DownstreamErrors}
import api.models.outcomes.ResponseWrapper
import uk.gov.hmrc.http.StringContextOps
import v9.retrieveProvisionalFiguresInformation.model.request.RetrieveProvisionalFiguresInformationRequestData
import v9.retrieveProvisionalFiguresInformation.model.response.RetrieveProvisionalFiguresInformationResponse

import scala.concurrent.Future

class RetrieveProvisionalFiguresInformationConnectorSpec extends ConnectorSpec {

  private val nino: String    = "AA123456A"
  private val taxYear: String = "2026-27"

  private val request: RetrieveProvisionalFiguresInformationRequestData =
    RetrieveProvisionalFiguresInformationRequestData(Nino(nino), TaxYear.fromMtd(taxYear))

  "RetrieveProvisionalFiguresInformationConnector" should {
    "return a success response" when {
      "the downstream call is successful" in new HipTest with Test {
        val outcome: Right[Nothing, ResponseWrapper[RetrieveProvisionalFiguresInformationResponse]] =
          Right(
            ResponseWrapper(
              correlationId,
              RetrieveProvisionalFiguresInformationResponse(
                submittedOn = api.models.domain.Timestamp("2027-08-24T14:15:22.544Z"),
                provisionalFiguresIndicator = true,
                expectedDate = Some("2027-10-05"),
                supplementaryInformation = Some("Provisional figures used pending final confirmation.")
              )
            ))

        willGet(url"$baseUrl/itsd/provisional-figures/$nino?taxYear=26-27")
          .returns(Future.successful(outcome))

        val result: DownstreamOutcome[RetrieveProvisionalFiguresInformationResponse] = await(connector.retrieveProvisionalFiguresInformation(request))
        result shouldBe outcome
      }
    }

    "return an unsuccessful response" when {
      "the downstream call is unsuccessful" in new HipTest with Test {
        val outcome: Left[ResponseWrapper[DownstreamErrors], Nothing] =
          Left(ResponseWrapper(correlationId, DownstreamErrors.single(DownstreamErrorCode("SOME_ERROR"))))

        willGet(url"$baseUrl/itsd/provisional-figures/$nino?taxYear=26-27")
          .returns(Future.successful(outcome))

        val result: DownstreamOutcome[RetrieveProvisionalFiguresInformationResponse] = await(connector.retrieveProvisionalFiguresInformation(request))
        result shouldBe outcome
      }
    }
  }

  private trait Test { self: ConnectorTest =>

    protected val connector: RetrieveProvisionalFiguresInformationConnector =
      new RetrieveProvisionalFiguresInformationConnector(http = mockHttpClient, appConfig = mockAppConfig)

  }

}
