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

package v9.deleteProvisionalFiguresInformation

import api.models.domain.{Nino, TaxYear}
import api.models.errors.*
import api.models.outcomes.ResponseWrapper
import api.services.ServiceSpec
import v9.deleteProvisionalFiguresInformation.model.request.DeleteProvisionalFiguresInformationRequestData

import scala.concurrent.Future

class DeleteProvisionalFiguresInformationServiceSpec extends ServiceSpec {

  private val request: DeleteProvisionalFiguresInformationRequestData =
    DeleteProvisionalFiguresInformationRequestData(Nino("AA123456A"), TaxYear.fromMtd("2026-27"))

  "DeleteProvisionalFiguresInformationService" should {
    "return a success response" when {
      "the connector call is successful" in new Test {
        val outcome: Right[Nothing, ResponseWrapper[Unit]] = Right(ResponseWrapper(correlationId, ()))

        MockDeleteProvisionalFiguresInformationConnector.deleteProvisionalFiguresInformation(request).returns(Future.successful(outcome))

        await(service.deleteProvisionalFiguresInformation(request)) shouldBe outcome
      }
    }

    "map errors according to spec" when {
      def serviceError(downstreamErrorCode: String, error: MtdError): Unit =
        s"a $downstreamErrorCode error is returned from the service" in new Test {
          MockDeleteProvisionalFiguresInformationConnector
            .deleteProvisionalFiguresInformation(request)
            .returns(Future.successful(Left(ResponseWrapper(correlationId, DownstreamErrors.single(DownstreamErrorCode(downstreamErrorCode))))))

          await(service.deleteProvisionalFiguresInformation(request)) shouldBe Left(ErrorWrapper(correlationId, error))
        }

      val errors = List(
        "1215" -> NinoFormatError,
        "1117" -> TaxYearFormatError,
        "1216" -> InternalError,
        "4200" -> RuleOutsideAmendmentWindowError,
        "5010" -> NotFoundError
      )

      errors.foreach(args => serviceError.tupled(args))
    }
  }

  private trait Test extends MockDeleteProvisionalFiguresInformationConnector {
    val service: DeleteProvisionalFiguresInformationService = new DeleteProvisionalFiguresInformationService(mockConnector)
  }

}
