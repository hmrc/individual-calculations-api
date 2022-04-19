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

import v3.mocks.connectors.MockSubmitFinalDeclarationConnector
import v3.models.domain.Nino
import v3.models.errors._
import v3.models.outcomes.ResponseWrapper
import v3.models.request.SubmitFinalDeclarationRequest

import scala.concurrent.Future

class SubmitFinalDeclarationServiceSpec extends ServiceSpec {

  trait Test extends MockSubmitFinalDeclarationConnector {

    val service: SubmitFinalDeclarationService = new SubmitFinalDeclarationService(
      mockSubmitFinalDeclarationConnector
    )
  }

  "SubmitFinalDeclarationService" when {
    val nino: String          = "AA112233A"
    val taxYear: String       = "2019-20"
    val calculationId: String = "a1e8057e-fbbc-47a8-a8b4-78d9f015c253"

    val request: SubmitFinalDeclarationRequest = SubmitFinalDeclarationRequest(
      Nino(nino),
      taxYear,
      calculationId
    )

    "SubmitFinalDeclaration" must {
      "return correct result for a success" in new Test {
        val outcome = Right(ResponseWrapper(correlationId, ()))

        MockSubmitFinalDeclarationConnector
          .submitFinalDeclaration(request)
          .returns(Future.successful(outcome))

        await(service.submitFinalDeclaration(request)) shouldBe outcome
      }
    }

    "map errors according to spec" when {

      def serviceError(downstreamErrorCode: String, error: MtdError, downstreamErrorStatus: Int): Unit =
        s"a $downstreamErrorCode error is returned from the service" in new Test {

          MockSubmitFinalDeclarationConnector
            .submitFinalDeclaration(request)
            .returns(Future.successful(
              Left(ResponseWrapper(
                correlationId,
                BackendErrors.single(downstreamErrorStatus, BackendErrorCode(downstreamErrorCode))))))

          await(service.submitFinalDeclaration(request)) shouldBe Left(ErrorWrapper(correlationId, error, None, downstreamErrorStatus))
        }

      val input = Seq(
        ("INVALID_IDTYPE", DownstreamError, BAD_REQUEST),
        ("INVALID_IDVALUE", NinoFormatError, BAD_REQUEST),
        ("INVALID_TAXYEAR", TaxYearFormatError, BAD_REQUEST),
        ("INVALID_CALCID", CalculationIdFormatError, BAD_REQUEST),
        ("NOT_FOUND", NotFoundError, NOT_FOUND),
        ("INCOME_SOURCES_CHANGED", RuleIncomeSourcesChangedError, CONFLICT),
        ("RECENT_SUBMISSIONS_EXIST", RuleRecentSubmissionsExistError, CONFLICT),
        ("RESIDENCY_CHANGED", RuleResidencyChangedError, CONFLICT),
        ("INVALID_INCOME_SOURCES", RuleIncomeSourcesInvalid, UNPROCESSABLE_ENTITY),
        ("INCOME_SUBMISSIONS_NOT_EXIST", RuleNoIncomeSubmissionsExistError, UNPROCESSABLE_ENTITY),
        ("BUSINESS_VALIDATION", RuleSubmissionFailed, UNPROCESSABLE_ENTITY),
        ("FINAL_DECLARATION_RECEIVED", RuleFinalDeclarationReceivedError, CONFLICT),
        ("SERVER_ERROR", DownstreamError, INTERNAL_SERVER_ERROR),
        ("SERVICE_UNAVAILABLE", DownstreamError, SERVICE_UNAVAILABLE)
      )

      input.foreach(args => (serviceError _).tupled(args))
    }
  }
}
