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

package v2.services

import v2.mocks.connectors.MockIntentToCrystalliseConnector
import v2.models.domain.{DownstreamTaxYear, Nino}
import v2.models.errors._
import v2.models.outcomes.ResponseWrapper
import v2.models.request.intentToCrystallise.IntentToCrystalliseRequest
import v2.models.response.intentToCrystallise.IntentToCrystalliseResponse

import scala.concurrent.Future

class IntentToCrystalliseServiceSpec extends ServiceSpec {

  trait Test extends MockIntentToCrystalliseConnector {

    val service: IntentToCrystalliseService = new IntentToCrystalliseService(
      connector = mockIntentToCrystalliseConnector
    )

  }

  "IntentToCrystalliseService" when {
    val nino: String    = "AA112233A"
    val taxYear: String = "2019-20"

    val request: IntentToCrystalliseRequest = IntentToCrystalliseRequest(
      nino = Nino(nino),
      taxYear = DownstreamTaxYear.fromMtd(taxYear)
    )

    val response: IntentToCrystalliseResponse = IntentToCrystalliseResponse(
      calculationId = "a1e8057e-fbbc-47a8-a8b4-78d9f015c253"
    )

    "submitIntent" must {
      "return correct result for a success" in new Test {
        val outcome = Right(ResponseWrapper(correlationId, response))

        MockIntentToCrystalliseConnector
          .submitIntent(request)
          .returns(Future.successful(outcome))

        await(service.submitIntentToCrystallise(request)) shouldBe outcome
      }

      "map errors according to spec" when {

        def serviceError(downstreamErrorCode: String, error: MtdError, downstreamErrorStatus: Int): Unit =
          s"a $downstreamErrorCode error is returned from the service" in new Test {

            MockIntentToCrystalliseConnector
              .submitIntent(request)
              .returns(Future.successful(
                Left(ResponseWrapper(correlationId, BackendErrors.single(downstreamErrorStatus, BackendErrorCode(downstreamErrorCode))))))

            await(service.submitIntentToCrystallise(request)) shouldBe Left(ErrorWrapper(correlationId, error, None, downstreamErrorStatus))
          }

        val input = Seq(
          ("INVALID_NINO", NinoFormatError, BAD_REQUEST),
          ("INVALID_TAX_YEAR", TaxYearFormatError, BAD_REQUEST),
          ("INVALID_TAX_CRYSTALLISE", DownstreamError, BAD_REQUEST),
          ("INVALID_REQUEST", DownstreamError, BAD_REQUEST),
          ("NO_SUBMISSION_EXIST", RuleNoSubmissionsExistError, FORBIDDEN),
          ("CONFLICT", RuleFinalDeclarationReceivedError, CONFLICT),
          ("SERVER_ERROR", DownstreamError, INTERNAL_SERVER_ERROR),
          ("SERVICE_UNAVAILABLE", DownstreamError, SERVICE_UNAVAILABLE),
          ("UNMATCHED_STUB_ERROR", RuleIncorrectGovTestScenarioError, BAD_REQUEST)
        )

        input.foreach(args => (serviceError _).tupled(args))
      }
    }
  }

}
