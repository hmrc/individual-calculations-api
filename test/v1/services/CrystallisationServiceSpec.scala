/*
 * Copyright 2020 HM Revenue & Customs
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

import uk.gov.hmrc.domain.Nino
import v1.mocks.connectors.MockCrystallisationConnector
import v1.models.domain.DesTaxYear
import v1.models.errors._
import v1.models.outcomes.ResponseWrapper
import v1.models.request.crystallisation.CrystallisationRequest
import v1.models.response.common.DesUnit

import scala.concurrent.Future

class CrystallisationServiceSpec extends ServiceSpec {

  trait Test extends MockCrystallisationConnector {
    val service: CrystallisationService = new CrystallisationService(
      connector = mockCrystallisationConnector
    )
  }

  "CrystallisationService" when {
    val nino = "AA112233A"
    val taxYear = "2019-20"
    val calculationId = "a1e8057e-fbbc-47a8-a8b4-78d9f015c253"

    val request: CrystallisationRequest = CrystallisationRequest(
      nino = Nino(nino),
      taxYear = DesTaxYear.fromMtd(taxYear),
      calculationId = calculationId
    )

    "declareCrystallisation" must {
      "return correct result for a success" in new Test {
        val outcome = Right(ResponseWrapper(correlationId, DesUnit))

        MockCrystallisationConnector.submitIntent(request)
          .returns(Future.successful(outcome))

        await(service.declareCrystallisation(request)) shouldBe outcome
      }

      "map errors according to spec" when {

        def serviceError(desErrorCode: String, error: MtdError, desErrorStatus: Int): Unit =
          s"a $desErrorCode error is returned from the service" in new Test {

            MockCrystallisationConnector.submitIntent(request)
              .returns(Future.successful(Left(ResponseWrapper(correlationId, BackendErrors.single(desErrorStatus, BackendErrorCode(desErrorCode))))))

            await(service.declareCrystallisation(request)) shouldBe Left(ErrorWrapper(correlationId, error, None, desErrorStatus))
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
          ("FINAL_DECLARATION_RECEIVED", RuleFinalDeclarationReceivedError, CONFLICT),
          ("SERVER_ERROR", DownstreamError, INTERNAL_SERVER_ERROR),
          ("SERVICE_UNAVAILABLE", DownstreamError, SERVICE_UNAVAILABLE)
        )

        input.foreach(args => (serviceError _).tupled(args))
      }
    }
  }
}
