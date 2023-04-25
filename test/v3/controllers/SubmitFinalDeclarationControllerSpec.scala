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

package v3.controllers

import api.controllers.{ControllerBaseSpec, ControllerTestRunner}
import api.mocks.MockIdGenerator
import api.models.domain.Nino
import api.models.errors.{ErrorWrapper, NinoFormatError, RuleTaxYearNotSupportedError}
import api.models.outcomes.ResponseWrapper
import play.api.libs.json.JsValue
import play.api.mvc.Result
import v3.mocks.requestParsers.MockSubmitFinalDeclarationParser
import v3.mocks.services._
import v3.models.audit.{AuditEvent, AuditResponse, GenericAuditDetail}
import v3.models.domain.TaxYear
import v3.models.request._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class SubmitFinalDeclarationControllerSpec
    extends ControllerBaseSpec
    with ControllerTestRunner
    with MockEnrolmentsAuthService
    with MockMtdIdLookupService
    with MockSubmitFinalDeclarationService
    with MockSubmitFinalDeclarationParser
    with MockAuditService
    with MockIdGenerator {

  private val taxYear       = "2020-21"
  private val calculationId = "4557ecb5-fd32-48cc-81f5-e6acd1099f3c"

  trait Test extends ControllerTest with AuditEventChecking {

    val controller = new SubmitFinalDeclarationController(
      authService = mockEnrolmentsAuthService,
      lookupService = mockMtdIdLookupService,
      parser = mockSubmitFinalDeclarationParser,
      service = mockSubmitFinalDeclarationService,
      cc = cc,
      auditService = mockAuditService,
      idGenerator = mockIdGenerator
    )

    protected def callController(): Future[Result] = controller.submitFinalDeclaration(nino, taxYear, calculationId)(fakeRequest)

    override protected def event(auditResponse: AuditResponse, maybeRequestBody: Option[JsValue]): AuditEvent[GenericAuditDetail] =
      AuditEvent(
        "SubmitAFinalDeclaration",
        "submit-a-final-declaration",
        GenericAuditDetail(
          userType = "Individual",
          agentReferenceNumber = None,
          params = Map("nino" -> nino, "taxYear" -> taxYear, "calculationId" -> calculationId),
          requestBody = None,
          `X-CorrelationId` = correlationId,
          versionNumber = "3.0",
          auditResponse = auditResponse
        )
      )

  }

  private val rawData     = SubmitFinalDeclarationRawData(nino, taxYear, calculationId)
  private val requestData = SubmitFinalDeclarationRequest(Nino(nino), TaxYear.fromMtd(taxYear), calculationId)

  "submit final declaration" should {
    "return a successful response" when {
      "the request received is valid" in new Test {

        MockSubmitFinalDeclarationParser
          .parseRequest(SubmitFinalDeclarationRawData(nino, taxYear, calculationId))
          .returns(Right(requestData))

        MockSubmitFinalDeclarationService
          .submitFinalDeclaration(requestData)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, ()))))

        runOkTestWithAudit(
          expectedStatus = NO_CONTENT
        )
      }
    }

    "return the error as per spec" when {
      "the parser validation fails" in new Test {
        MockSubmitFinalDeclarationParser
          .parseRequest(rawData)
          .returns(Left(ErrorWrapper(correlationId, NinoFormatError, None)))

        runErrorTestWithAudit(NinoFormatError)
      }

      "the service returns an error" in new Test {
        MockSubmitFinalDeclarationParser
          .parseRequest(rawData)
          .returns(Right(requestData))

        MockSubmitFinalDeclarationService
          .submitFinalDeclaration(requestData)
          .returns(Future.successful(Left(ErrorWrapper(correlationId, RuleTaxYearNotSupportedError))))

        runErrorTestWithAudit(RuleTaxYearNotSupportedError)
      }
    }

  }

}
