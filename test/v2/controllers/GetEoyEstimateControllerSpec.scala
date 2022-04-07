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

package v2.controllers

import mocks.MockIdGenerator
import play.api.libs.json.{JsObject, Json}
import play.api.mvc.Result
import uk.gov.hmrc.http.HeaderCarrier
import v2.fixtures.getEndOfYearEstimate.EoyEstimateResponseFixture
import v2.handler.RequestDefn
import v2.mocks.hateoas.MockHateoasFactory
import v2.mocks.requestParsers.MockGetCalculationParser
import v2.mocks.services.{MockAuditService, MockEnrolmentsAuthService, MockMtdIdLookupService, MockStandardService}
import v2.models.domain.Nino
import v2.models.audit.{AuditError, AuditEvent, AuditResponse, GenericAuditDetail}
import v2.models.errors.{EndOfYearEstimateNotPresentError, RuleCalculationErrorMessagesExist}
import v2.models.hateoas.{HateoasWrapper, Link}
import v2.models.hateoas.Method.GET
import v2.models.outcomes.ResponseWrapper
import v2.models.request.{GetCalculationRawData, GetCalculationRequest}
import v2.models.response.calculationWrappers.EoyEstimateWrapperOrError
import v2.models.response.calculationWrappers.EoyEstimateWrapperOrError.EoyEstimateWrapper
import v2.models.response.getEoyEstimate.EoyEstimateHateoasData

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class GetEoyEstimateControllerSpec
    extends ControllerBaseSpec
    with MockEnrolmentsAuthService
    with MockMtdIdLookupService
    with MockGetCalculationParser
    with MockStandardService
    with MockHateoasFactory
    with MockAuditService
    with MockIdGenerator {

  private val nino          = "AA123456A"
  private val calcId        = "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c"
  private val correlationId = "X-123"

  private val rawData     = GetCalculationRawData(nino, calcId)
  private val requestData = GetCalculationRequest(Nino(nino), calcId)

  val testHateoasLink: Link = Link(href = "/foo/bar", method = GET, rel = "test-relationship")

  val linksJson: JsObject = Json
    .parse(
      """
      |{
      |  "links" : [
      |     {
      |       "href": "/foo/bar",
      |       "method": "GET",
      |       "rel": "test-relationship"
      |     }
      |  ]
      |}
    """.stripMargin
    )
    .as[JsObject]

  private def uri      = s"/$nino/self-assessment/$calcId"
  private def queryUri = "/input/uri"

  trait Test {
    val hc: HeaderCarrier = HeaderCarrier()

    val controller = new GetEoyEstimateController(
      authService = mockEnrolmentsAuthService,
      lookupService = mockMtdIdLookupService,
      parser = mockGetCalculationParser,
      service = mockStandardService,
      hateoasFactory = mockHateoasFactory,
      auditService = mockAuditService,
      cc = cc,
      idGenerator = mockIdGenerator
    )

    MockedMtdIdLookupService.lookup(nino).returns(Future.successful(Right("test-mtd-id")))
    MockedEnrolmentsAuthService.authoriseUser()
    MockIdGenerator.getCorrelationId.returns(correlationId)
  }

  "handleRequest" should {
    "return OK with the calculation" when {
      "happy path" in new Test {
        MockGetCalculationParser
          .parse(rawData)
          .returns(Right(requestData))

        MockStandardService
          .doService(RequestDefn.Get(uri), OK)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, EoyEstimateWrapper(EoyEstimateResponseFixture.eoyEstimateResponseModel)))))

        MockHateoasFactory
          .wrap(EoyEstimateResponseFixture.eoyEstimateResponseModel, EoyEstimateHateoasData(nino, calcId))
          .returns(HateoasWrapper(EoyEstimateResponseFixture.eoyEstimateResponseModel, Seq(testHateoasLink)))

        val result: Future[Result] = controller.getEoyEstimate(nino, calcId)(fakeGetRequest(queryUri))

        val responseBody: JsObject = EoyEstimateResponseFixture.eoyEstimateResponseJson.deepMerge(linksJson)
        status(result) shouldBe OK
        contentAsJson(result) shouldBe responseBody
        header("X-CorrelationId", result) shouldBe Some(correlationId)

        val detail: GenericAuditDetail = GenericAuditDetail(
          "Individual",
          None,
          Map("nino" -> nino, "calculationId" -> calcId),
          None,
          correlationId,
          AuditResponse(OK, None, Some(responseBody)))
        val event: AuditEvent[GenericAuditDetail] =
          AuditEvent("retrieveSelfAssessmentTaxCalculationEndOfYearEstimate", "retrieve-self-assessment-tax-calculation-end-of-year-estimate", detail)
        MockedAuditService.verifyAuditEvent(event).once
      }
    }

    "return FORBIDDEN with the error message" when {
      "error count is greater than zero" in new Test {
        MockGetCalculationParser
          .parse(rawData)
          .returns(Right(requestData))

        MockStandardService
          .doService(RequestDefn.Get(uri), OK)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, EoyEstimateWrapperOrError.EoyErrorMessages))))

        val result: Future[Result] = controller.getEoyEstimate(nino, calcId)(fakeGetRequest(queryUri))

        status(result) shouldBe FORBIDDEN
        contentAsJson(result) shouldBe Json.toJson(RuleCalculationErrorMessagesExist)
        header("X-CorrelationId", result) shouldBe Some(correlationId)

        val detail: GenericAuditDetail = GenericAuditDetail(
          "Individual",
          None,
          Map("nino" -> nino, "calculationId" -> calcId),
          None,
          correlationId,
          AuditResponse(FORBIDDEN, Some(Seq(AuditError(RuleCalculationErrorMessagesExist.code))), None)
        )
        val event: AuditEvent[GenericAuditDetail] =
          AuditEvent("retrieveSelfAssessmentTaxCalculationEndOfYearEstimate", "retrieve-self-assessment-tax-calculation-end-of-year-estimate", detail)
        MockedAuditService.verifyAuditEvent(event).once
      }
    }

    "return NOT FOUND with the error message" when {
      "calculation type is crystallised" in new Test {
        MockGetCalculationParser
          .parse(rawData)
          .returns(Right(requestData))

        MockStandardService
          .doService(RequestDefn.Get(uri), OK)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, EoyEstimateWrapperOrError.EoyCrystallisedError))))

        val result: Future[Result] = controller.getEoyEstimate(nino, calcId)(fakeGetRequest(queryUri))

        status(result) shouldBe NOT_FOUND
        contentAsJson(result) shouldBe Json.toJson(EndOfYearEstimateNotPresentError)
        header("X-CorrelationId", result) shouldBe Some(correlationId)

        val detail: GenericAuditDetail = GenericAuditDetail(
          "Individual",
          None,
          Map("nino" -> nino, "calculationId" -> calcId),
          None,
          correlationId,
          AuditResponse(NOT_FOUND, Some(Seq(AuditError(EndOfYearEstimateNotPresentError.code))), None)
        )
        val event: AuditEvent[GenericAuditDetail] =
          AuditEvent("retrieveSelfAssessmentTaxCalculationEndOfYearEstimate", "retrieve-self-assessment-tax-calculation-end-of-year-estimate", detail)
        MockedAuditService.verifyAuditEvent(event).once
      }
    }
  }

}
