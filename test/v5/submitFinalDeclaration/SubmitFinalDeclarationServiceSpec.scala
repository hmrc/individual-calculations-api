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

package v5.submitFinalDeclaration

import api.errors._
import org.scalatest.concurrent.Eventually
import shared.models.domain.{CalculationId, Nino, TaxYear}
import shared.models.errors._
import shared.models.outcomes.ResponseWrapper
import shared.services.ServiceSpec
import v5.retrieveCalculation.def1.model.Def1_CalculationFixture
import v5.submitFinalDeclaration.model.request.{Def1_SubmitFinalDeclarationRequestData, SubmitFinalDeclarationRequestData}

import scala.concurrent.Future

class SubmitFinalDeclarationServiceSpec extends ServiceSpec with Eventually with StubNrsService with Def1_CalculationFixture {

  trait Test extends MockSubmitFinalDeclarationConnector with MockNrsService {

    val service: SubmitFinalDeclarationService = new SubmitFinalDeclarationService(
      mockSubmitFinalDeclarationConnector,
      stubNrsService
    )

  }

  "SubmitFinalDeclarationService" when {
    val nino: Nino                   = Nino("ZG903729C")
    val taxYear: TaxYear             = TaxYear.fromMtd("2019-20")
    val calculationId: CalculationId = CalculationId("a1e8057e-fbbc-47a8-a8b4-78d9f015c253")

    val request: SubmitFinalDeclarationRequestData = Def1_SubmitFinalDeclarationRequestData(
      nino,
      taxYear,
      calculationId
    )

    "SubmitFinalDeclaration" must {
      "return correct result for a success" in new Test {
        val outcome: Right[Nothing, ResponseWrapper[Unit]] = Right(ResponseWrapper(correlationId, ()))

        MockNrsService
          .updateNrs(nino.nino, request)
          .returns(Future.successful(()))

        MockSubmitFinalDeclarationConnector
          .submitFinalDeclaration(request)
          .returns(Future.successful(outcome))

        val result: Either[ErrorWrapper, ResponseWrapper[Unit]] = await(service.submitFinalDeclaration(nino.nino, request))
        result shouldBe outcome
      }
    }

    "map errors according to spec" when {
      def serviceError(downstreamErrorCode: String, error: MtdError): Unit =
        s"a $downstreamErrorCode error is returned from the service" in new Test {

          MockNrsService
            .updateNrs(nino.nino, request)
            .returns(Future.successful(()))

          MockSubmitFinalDeclarationConnector
            .submitFinalDeclaration(request)
            .returns(Future.successful(Left(ResponseWrapper(correlationId, DownstreamErrors.single(DownstreamErrorCode(downstreamErrorCode))))))

          val result: Either[ErrorWrapper, ResponseWrapper[Unit]] = await(service.submitFinalDeclaration(nino.nino, request))
          result shouldBe Left(ErrorWrapper(correlationId, error))
        }

      val errors = List(
        ("INVALID_TAXABLE_ENTITY_ID", NinoFormatError),
        ("INVALID_TAX_YEAR", TaxYearFormatError),
        ("INVALID_CALCID", CalculationIdFormatError),
        ("INVALID_CORRELATION_ID", InternalError),
        ("NOT_FOUND", NotFoundError),
        ("INCOME_SOURCES_CHANGED", RuleIncomeSourcesChangedError),
        ("RECENT_SUBMISSIONS_EXIST", RuleRecentSubmissionsExistError),
        ("RESIDENCY_CHANGED", RuleResidencyChangedError),
        ("FINAL_DECLARATION_RECEIVED", RuleFinalDeclarationReceivedError),
        ("INVALID_INCOME_SOURCES", RuleIncomeSourcesInvalidError),
        ("INCOME_SUBMISSIONS_NOT_EXIST", RuleNoIncomeSubmissionsExistError),
        ("BUSINESS_VALIDATION", RuleSubmissionFailedError),
        ("CRYSTALLISATION_TAX_YEAR_ERROR", RuleFinalDeclarationTaxYearError),
        ("CRYSTALLISATION_IN_PROGRESS", RuleFinalDeclarationInProgressError),
        ("TAX_YEAR_NOT_SUPPORTED", RuleTaxYearNotSupportedError),
        ("SERVER_ERROR", InternalError),
        ("SERVICE_UNAVAILABLE", InternalError),
        ("UNMATCHED_STUB_ERROR", RuleIncorrectGovTestScenarioError)
      )

      val extraDesErrors = List(
        ("INVALID_IDTYPE", InternalError),
        ("INVALID_IDVALUE", NinoFormatError),
        ("INVALID_TAXYEAR", TaxYearFormatError)
      )

      (errors ++ extraDesErrors).foreach(args => (serviceError _).tupled(args))
    }
  }

}
