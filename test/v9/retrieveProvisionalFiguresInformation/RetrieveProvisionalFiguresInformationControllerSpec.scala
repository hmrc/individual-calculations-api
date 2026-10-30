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

package v9.retrieveProvisionalFiguresInformation

import api.controllers.{ControllerBaseSpec, ControllerTestRunner}
import api.models.audit.{AuditEvent, AuditResponse, GenericAuditDetail}
import api.models.domain.{TaxYear, Timestamp}
import api.models.errors.*
import api.models.outcomes.ResponseWrapper
import play.api.Configuration
import play.api.libs.json.{JsObject, JsValue, Json}
import play.api.mvc.Result
import v9.retrieveProvisionalFiguresInformation.model.request.RetrieveProvisionalFiguresInformationRequestData
import v9.retrieveProvisionalFiguresInformation.model.response.RetrieveProvisionalFiguresInformationResponse

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class RetrieveProvisionalFiguresInformationControllerSpec
    extends ControllerBaseSpec
    with ControllerTestRunner
    with MockRetrieveProvisionalFiguresInformationService
    with MockRetrieveProvisionalFiguresInformationValidatorFactory {

  private val taxYear = "2026-27"

  private val requestData: RetrieveProvisionalFiguresInformationRequestData =
    RetrieveProvisionalFiguresInformationRequestData(parsedNino, TaxYear.fromMtd(taxYear))

  "RetrieveProvisionalFiguresInformationController" should {
    "return OK status" when {
      "the request received is valid" in new Test {
        willUseValidator(returningSuccess(requestData))

        MockRetrieveProvisionalFiguresInformationService
          .retrieveProvisionalFiguresInformation(requestData)
          .returns(Future.successful(Right(ResponseWrapper(
            correlationId,
            RetrieveProvisionalFiguresInformationResponse(
              submittedOn = Timestamp("2027-08-24T14:15:22.544Z"),
              provisionalFiguresIndicator = true,
              expectedDate = Some("2027-10-05"),
              supplementaryInformation = Some("Provisional figures used pending final confirmation.")
            )
          ))))

        val expectedResponseBody: JsObject = Json.obj(
          "submittedOn"                 -> "2027-08-24T14:15:22.544Z",
          "provisionalFiguresIndicator" -> true,
          "expectedDate"                -> "2027-10-05",
          "supplementaryInformation"    -> "Provisional figures used pending final confirmation."
        )

        runOkTestWithAudit(expectedStatus = OK, maybeExpectedResponseBody = Some(expectedResponseBody))
      }
    }

    "return the error as per spec" when {
      "the parser validation fails" in new Test {
        willUseValidator(returning(NinoFormatError))

        runErrorTestWithAudit(NinoFormatError)
      }

      "the service returns an error" in new Test {
        willUseValidator(returningSuccess(requestData))

        MockRetrieveProvisionalFiguresInformationService
          .retrieveProvisionalFiguresInformation(requestData)
          .returns(Future.successful(Left(ErrorWrapper(correlationId, RuleOutsideAmendmentWindowError))))

        runErrorTestWithAudit(RuleOutsideAmendmentWindowError)
      }
    }
  }

  private trait Test extends ControllerTest with AuditEventChecking[GenericAuditDetail] {

    val controller: RetrieveProvisionalFiguresInformationController = new RetrieveProvisionalFiguresInformationController(
      authService = mockEnrolmentsAuthService,
      lookupService = mockMtdIdLookupService,
      service = mockRetrieveProvisionalFiguresInformationService,
      validatorFactory = mockRetrieveProvisionalFiguresInformationValidatorFactory,
      auditService = mockAuditService,
      cc = cc,
      idGenerator = mockIdGenerator
    )

    MockedAppConfig.featureSwitchConfig.anyNumberOfTimes() returns Configuration(
      "supporting-agents-access-control.enabled" -> true
    )

    MockedAppConfig.endpointAllowsSupportingAgents(controller.endpointName).anyNumberOfTimes() returns false

    protected def callController(): Future[Result] = controller.retrieveProvisionalFiguresInformation(validNino, taxYear)(fakeRequest)

    protected def event(auditResponse: AuditResponse, maybeRequestBody: Option[JsValue]): AuditEvent[GenericAuditDetail] =
      AuditEvent(
        auditType = "RetrieveProvisionalFiguresInformation",
        transactionName = "retrieve-provisional-figures-information",
        detail = GenericAuditDetail(
          userType = "Individual",
          agentReferenceNumber = None,
          versionNumber = apiVersion.name,
          params = Map("nino" -> validNino, "taxYear" -> taxYear),
          requestBody = maybeRequestBody,
          `X-CorrelationId` = correlationId,
          auditResponse = auditResponse
        )
      )

  }

}
