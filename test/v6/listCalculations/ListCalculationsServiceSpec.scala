/*
 * Copyright 2025 HM Revenue & Customs
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

package v6.listCalculations

import shared.models.domain.{Nino, TaxYear}
import shared.models.errors._
import shared.models.outcomes.ResponseWrapper
import shared.services.ServiceSpec
import v6.listCalculations.def1.model.Def1_ListCalculationsFixture
import v6.listCalculations.def1.model.response.Calculation
import v6.listCalculations.model.request.{Def1_ListCalculationsRequestData, ListCalculationsRequestData}
import v6.listCalculations.model.response.ListCalculationsResponse

import scala.concurrent.Future

class ListCalculationsServiceSpec extends ServiceSpec with Def1_ListCalculationsFixture {

  trait Test extends MockListCalculationsConnector {
    val nino: Nino                           = Nino("AA111111A")
    val taxYear: TaxYear                     = TaxYear.fromMtd("2018-19")
    val request: ListCalculationsRequestData = Def1_ListCalculationsRequestData(nino, taxYear)
    val service: ListCalculationsService     = new ListCalculationsService(mockListCalculationsConnector)
  }

  "ListCalculationsService" when {
    "a successful response is returned" must {
      "return a success" in new Test {
        val outcome: Right[Nothing, ResponseWrapper[ListCalculationsResponse[Calculation]]] =
          Right(ResponseWrapper(correlationId, listCalculationsResponseModel))

        MockListCalculationsConnector
          .list(request)
          .returns(
            Future.successful(Right(ResponseWrapper(correlationId, listCalculationsResponseModel)))
          )

        await(service.list(request)) shouldBe outcome
      }
    }

    "an error response is returned" must {
      def checkErrorMappings(downstreamErrorCode: String, mtdError: MtdError): Unit = new Test {
        s"map appropriately for error code: '$downstreamErrorCode'" in {
          val connectorOutcome = Left(ResponseWrapper(correlationId, DownstreamErrors.single(DownstreamErrorCode(downstreamErrorCode))))
          MockListCalculationsConnector.list(request).returns(Future.successful(connectorOutcome))
          await(service.list(request)) shouldBe Left(ErrorWrapper(correlationId, mtdError))
        }
      }

      val commonError = Seq("UNMATCHED_STUB_ERROR" -> RuleIncorrectGovTestScenarioError)

      val nonTysErrors = Seq(
        "1215" -> NinoFormatError,
        "1117" -> TaxYearFormatError,
        "5010" -> NotFoundError
      )

      val tysErrors = Seq(
        "INVALID_TAXABLE_ENTITY_ID" -> NinoFormatError,
        "INVALID_TAX_YEAR"          -> TaxYearFormatError,
        "INVALID_CORRELATION_ID"    -> InternalError,
        "NOT_FOUND"                 -> NotFoundError,
        "TAX_YEAR_NOT_SUPPORTED"    -> RuleTaxYearNotSupportedError,
        "SERVER_ERROR"              -> InternalError,
        "SERVICE_UNAVAILABLE"       -> InternalError
      )

      (commonError ++ nonTysErrors ++ tysErrors).foreach(args => (checkErrorMappings _).tupled(args))

      "return an internal server error for an unexpected error code" in new Test {
        val outcome: Left[ResponseWrapper[DownstreamErrors], Nothing] =
          Left(ResponseWrapper(correlationId, DownstreamErrors.single(DownstreamErrorCode("NOT_MAPPED"))))
        MockListCalculationsConnector.list(request).returns(Future.successful(outcome))
        await(service.list(request)) shouldBe Left(ErrorWrapper(correlationId, InternalError))
      }
    }
  }

}
