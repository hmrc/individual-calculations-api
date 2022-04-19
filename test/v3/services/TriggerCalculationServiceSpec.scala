/*
 * Copyright 2022 HM Revenue & Customs
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

package v3.services

import v3.mocks.connectors.MockTriggerCalculationConnector
import v3.models.domain.TaxYear
import v3.models.errors._
import v3.models.outcomes.ResponseWrapper
import v3.models.request.TriggerCalculationRequest
import v3.models.response.triggerCalculation.TriggerCalculationResponse

import scala.concurrent.Future

class TriggerCalculationServiceSpec extends ServiceSpec {

  val taxYear: TaxYear = TaxYear.fromDownstream("2020")

  val request: TriggerCalculationRequest = TriggerCalculationRequest(nino, taxYear, finalDeclaration = true)

  val response: TriggerCalculationResponse = TriggerCalculationResponse("someCalcId")

  trait Test extends MockTriggerCalculationConnector {
    val service = new TriggerCalculationService(mockConnector)
  }

  "triggerCalculation" when {
    "finalDeclaration parameter specified" when {
      "successful" must {
        "use the parameters and return the response" in new Test {

          MockTriggerCalculationConnector
            .triggerCalculation(request)
            .returns(Future.successful(Right(ResponseWrapper(correlationId, response))))

          await(service.triggerCalculation(request)) shouldBe Right(ResponseWrapper(correlationId, response))
        }
      }
    }

    "unsuccessful" should {
      "map errors according to spec" when {
        def serviceError(downstreamErrorCode: String, error: MtdError): Unit =
          s"a $downstreamErrorCode error is returned from the service" in new Test {

            MockTriggerCalculationConnector
              .triggerCalculation(request)
              .returns(Future.successful(Left(ResponseWrapper(correlationId, DesErrors.single(DesErrorCode(downstreamErrorCode))))))

            await(service.triggerCalculation(request)) shouldBe Left(ErrorWrapper(correlationId, error))
          }

        val input = Seq(
          ("INVALID_NINO", NinoFormatError),
          ("INVALID_TAX_YEAR", TaxYearFormatError),
          ("INVALID_TAX_CRYSTALLISE", FormatFinalDeclaration),
          ("INVALID_REQUEST", DownstreamError),
          ("NO_SUBMISSION_EXIST", RuleNoSubmissionsExistError),
          ("CONFLICT", RuleFinalDeclarationReceivedError),
          ("SERVER_ERROR", DownstreamError),
          ("SERVICE_UNAVAILABLE", DownstreamError)
        )

        input.foreach(args => (serviceError _).tupled(args))
      }
    }

  }

}
