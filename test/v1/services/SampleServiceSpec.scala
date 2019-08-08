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

package v1.services

import support.UnitSpec
import uk.gov.hmrc.domain.Nino
import uk.gov.hmrc.http.HeaderCarrier
import v1.controllers.EndpointLogContext
import v1.mocks.connectors.MockSampleConnector
import v1.models.backend.BackendSampleResponse
import v1.models.domain.{SampleRequestBody, SampleResponse}
import v1.models.errors._
import v1.models.outcomes.ResponseWrapper
import v1.models.requestData.{TaxYear, SampleRequestData}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class SampleServiceSpec extends UnitSpec {

  private val nino = "AA123456A"
  private val taxYear = "2017-18"
  private val correlationId = "X-123"

  private val requestBody = SampleRequestBody("someData")

  private val requestData = SampleRequestData(Nino(nino), TaxYear.toYearEnding(taxYear), requestBody)

  trait Test extends MockSampleConnector {
    implicit val hc: HeaderCarrier = HeaderCarrier()
    implicit val logContext: EndpointLogContext = EndpointLogContext("c", "ep")

    val service = new SampleService(
      sampleConnector = mockSampleConnector
    )
  }

  "service" when {
    "service call successsful" must {
      "return mapped result" in new Test {
        MockSampleConnector.doConnectorThing(requestData)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, BackendSampleResponse("result")))))

        await(service.doServiceThing(requestData)) shouldBe Right(ResponseWrapper(correlationId, SampleResponse("result")))
      }
    }

    "unsuccessful" must {
      "map errors according to spec" when {

        def serviceError(errorCode: String, error: MtdError): Unit =
          s"a $errorCode error is returned from the service" in new Test {

            MockSampleConnector.doConnectorThing(requestData)
              .returns(Future.successful(Left(ResponseWrapper(correlationId, BackendErrors.single(BackendErrorCode(errorCode))))))

            await(service.doServiceThing(requestData)) shouldBe Left(ErrorWrapper(Some(correlationId), error))
          }

        val input = Seq(
          ("NOT_FOUND", NotFoundError),
          ("SERVER_ERROR", DownstreamError),
          ("SERVICE_UNAVAILABLE", DownstreamError)
        )

        input.foreach(args => (serviceError _).tupled(args))
      }
    }
  }
}
