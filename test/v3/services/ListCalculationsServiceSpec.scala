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

package v3.services

import api.models.domain.{Nino, TaxYear}
import api.models.errors.{DownstreamErrorCode, DownstreamErrors, ErrorWrapper, InternalError, MtdError, NinoFormatError, NotFoundError, RuleIncorrectGovTestScenarioError, RuleTaxYearNotSupportedError, TaxYearFormatError}
import api.models.outcomes.ResponseWrapper
import v3.fixtures.ListCalculationsFixture
import v3.mocks.connectors.MockListCalculationsConnector
import v3.models.request.ListCalculationsRequest

import scala.concurrent.Future

class ListCalculationsServiceSpec extends ServiceSpec with ListCalculationsFixture {

  trait Test extends MockListCalculationsConnector {
    val nino: Nino                       = Nino("AA111111A")
    val taxYear: TaxYear                 = TaxYear.fromMtd("2018-19")
    val request: ListCalculationsRequest = ListCalculationsRequest(nino, taxYear)
    val service: ListCalculationsService = new ListCalculationsService(mockListCalculationsConnector)
  }

  "ListCalculationsService" when {
    "a successful response is returned" must {
      "return a success" in new Test {
        val outcome = Right(ResponseWrapper(correlationId, listCalculationsResponseModel))

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

      val errors = Seq(
        "INVALID_TAXABLE_ENTITY_ID" -> NinoFormatError,
        "INVALID_TAXYEAR"           -> TaxYearFormatError,
        "NOT_FOUND"                 -> NotFoundError,
        "SERVER_ERROR"              -> InternalError,
        "SERVICE_UNAVAILABLE"       -> InternalError,
        "UNMATCHED_STUB_ERROR"      -> RuleIncorrectGovTestScenarioError
      )

      val extraTysErrors = Seq(
        "INVALID_TAX_YEAR"       -> TaxYearFormatError,
        "INVALID_CORRELATION_ID" -> InternalError,
        "TAX_YEAR_NOT_SUPPORTED" -> RuleTaxYearNotSupportedError
      )

      (errors ++ extraTysErrors).foreach(args => (checkErrorMappings _).tupled(args))

      "return an internal server error for an unexpected error code" in new Test {
        val outcome = Left(ResponseWrapper(correlationId, DownstreamErrors.single(DownstreamErrorCode("NOT_MAPPED"))))
        MockListCalculationsConnector.list(request).returns(Future.successful(outcome))
        await(service.list(request)) shouldBe Left(ErrorWrapper(correlationId, InternalError))
      }
    }
  }

}
