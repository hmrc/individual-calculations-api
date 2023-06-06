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

package v4.controllers

import api.mocks.MockIdGenerator
import play.api.libs.json.{JsObject, Json}
import play.api.mvc.Result
import uk.gov.hmrc.http.HeaderCarrier
import v4.fixtures.getIncomeTaxAndNics.IncomeTaxAndNicsResponseFixture._
import v4.handler.RequestDefn
import v4.mocks.hateoas.MockHateoasFactory
import v4.mocks.requestParsers.MockGetCalculationParser
import v4.mocks.services.{MockAuditService, MockEnrolmentsAuthService, MockMtdIdLookupService, MockStandardService}
import v4.models.audit.{AuditError, AuditEvent, AuditResponse, GenericAuditDetail}
import v4.models.domain.Nino
import v4.models.errors._
import v4.models.hateoas.Method.GET
import v4.models.hateoas.{HateoasWrapper, Link}
import v4.models.outcomes.ResponseWrapper
import v4.models.request.{GetCalculationRawData, GetCalculationRequest}
import v4.models.response.calculationWrappers.CalculationWrapperOrError
import v4.models.response.getIncomeTaxAndNics.IncomeTaxAndNicsHateoasData

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class GetIncomeTaxAndNicsControllerSpec
    extends ControllerBaseSpec
    with MockEnrolmentsAuthService
    with MockMtdIdLookupService
    with MockGetCalculationParser
    with MockStandardService
    with MockHateoasFactory
    with MockAuditService
    with MockIdGenerator {

  private val nino          = "AA123456A"
  private val correlationId = "X-123"

  private val rawData     = GetCalculationRawData(nino, fixtureCalculationId)
  private val requestData = GetCalculationRequest(Nino(nino), fixtureCalculationId)

  private def uri      = s"/$nino/self-assessment/$fixtureCalculationId"
  private def queryUri = "/input/uri"

  val testHateoasLink: Link = Link(href = "/foo/bar", method = GET, rel = "test-relationship")

  val linksJson: JsObject = Json
    .parse(
      """
      |{
      |    "links": [
      |      {
      |       "href": "/foo/bar",
      |       "method": "GET",
      |       "rel": "test-relationship"
      |      }
      |    ]
      |}
    """.stripMargin
    )
    .as[JsObject]

  trait Test {
    val hc: HeaderCarrier = HeaderCarrier()

    val controller = new GetIncomeTaxAndNicsController(
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
    MockIdGenerator.generateCorrelationId.returns(correlationId)
  }

  "handleRequest" should {
    "return OK with the calculation" when {
      "happy path" in new Test {
        MockGetCalculationParser
          .parse(rawData)
          .returns(Right(requestData))

        MockStandardService
          .doService(RequestDefn.Get(uri), OK)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, wrappedIncomeTaxAndNicsResponseModel))))

        MockHateoasFactory
          .wrap(incomeTaxAndNicsResponseModel, IncomeTaxAndNicsHateoasData(nino, fixtureCalculationId))
          .returns(HateoasWrapper(incomeTaxAndNicsResponseModel, Seq(testHateoasLink)))

        val result: Future[Result] = controller.getIncomeTaxAndNics(nino, fixtureCalculationId)(fakeGetRequest(queryUri))

        status(result) shouldBe OK
        val responseBody: JsObject = incomeTaxNicsResponseJson.as[JsObject].deepMerge(linksJson)
        contentAsJson(result) shouldBe responseBody
        header("X-CorrelationId", result) shouldBe Some(correlationId)

        val detail: GenericAuditDetail = GenericAuditDetail(
          "Individual",
          None,
          Map("nino" -> nino, "calculationId" -> fixtureCalculationId),
          None,
          correlationId,
          AuditResponse(OK, None, Some(responseBody)))
        val event: AuditEvent[GenericAuditDetail] = AuditEvent(
          "retrieveSelfAssessmentTaxCalculationIncomeTaxNicsCalculated",
          "retrieve-self-assessment-tax-calculation-income-tax-nics-calculated",
          detail)
        MockedAuditService.verifyAuditEvent(event).once()
      }
    }

    "return FORBIDDEN with the error message" when {
      "error count is greater than zero" in new Test {
        MockGetCalculationParser
          .parse(rawData)
          .returns(Right(requestData))

        MockStandardService
          .doService(RequestDefn.Get(uri), OK)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, CalculationWrapperOrError.ErrorsInCalculation))))

        val result: Future[Result] = controller.getIncomeTaxAndNics(nino, fixtureCalculationId)(fakeGetRequest(queryUri))

        status(result) shouldBe FORBIDDEN
        contentAsJson(result) shouldBe Json.toJson(RuleCalculationErrorMessagesExist)
        header("X-CorrelationId", result) shouldBe Some(correlationId)

        val detail: GenericAuditDetail = GenericAuditDetail(
          "Individual",
          None,
          Map("nino" -> nino, "calculationId" -> fixtureCalculationId),
          None,
          correlationId,
          AuditResponse(FORBIDDEN, Some(Seq(AuditError(RuleCalculationErrorMessagesExist.code))), None)
        )
        val event: AuditEvent[GenericAuditDetail] = AuditEvent(
          "retrieveSelfAssessmentTaxCalculationIncomeTaxNicsCalculated",
          "retrieve-self-assessment-tax-calculation-income-tax-nics-calculated",
          detail)
        MockedAuditService.verifyAuditEvent(event).once()
      }
    }
  }

}
