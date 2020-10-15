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

package v1.controllers

import play.api.libs.json.{JsValue, Json}
import play.api.mvc.Result
import uk.gov.hmrc.domain.Nino
import uk.gov.hmrc.http.HeaderCarrier
import v1.hateoas.HateoasLinks
import v1.mocks.MockAppConfig
import v1.mocks.hateoas.MockHateoasFactory
import v1.mocks.requestParsers.MockIntentToCrystalliseRequestParser
import v1.mocks.services.{MockEnrolmentsAuthService, MockIntentToCrystalliseService, MockMtdIdLookupService}
import v1.models.domain.DesTaxYear
import v1.models.errors._
import v1.models.hateoas.{HateoasWrapper, Link}
import v1.models.outcomes.ResponseWrapper
import v1.models.request.intentToCrystallise.{IntentToCrystalliseRawData, IntentToCrystalliseRequest}
import v1.models.response.intentToCrystallise.{IntentToCrystalliseHateaosData, IntentToCrystalliseResponse}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class IntentToCrystalliseControllerSpec
  extends ControllerBaseSpec
    with MockEnrolmentsAuthService
    with MockMtdIdLookupService
    with MockAppConfig
    with MockIntentToCrystalliseService
    with MockIntentToCrystalliseRequestParser
    with MockHateoasFactory
    with HateoasLinks {

  trait Test {
    val hc = HeaderCarrier()

    val controller = new IntentToCrystalliseController(
      authService = mockEnrolmentsAuthService,
      lookupService = mockMtdIdLookupService,
      appConfig = mockAppConfig,
      requestParser = mockIntentToCrystalliseRequestParser,
      service = mockIntentToCrystalliseService,
      hateoasFactory = mockHateoasFactory,
      cc = cc
    )

    MockedMtdIdLookupService.lookup(nino).returns(Future.successful(Right("test-mtd-id")))
    MockedEnrolmentsAuthService.authoriseUser()
    MockedAppConfig.apiGatewayContext.returns("individuals/calculations").anyNumberOfTimes()

    val links: List[Link] = List(
      getMetadata(mockAppConfig, nino, calculationId, isSelf = true),
      crystallise(mockAppConfig, nino, taxYear)
    )
  }

  val nino: String = "AA123456A"
  val taxYear: String = "2019-20"
  val calculationId: String = "4557ecb5-fd32-48cc-81f5-e6acd1099f3c"
  val correlationId: String = "a1e8057e-fbbc-47a8-a8b4-78d9f015c253"

  val rawData: IntentToCrystalliseRawData = IntentToCrystalliseRawData(
    nino = nino,
    taxYear = taxYear
  )

  val requestData: IntentToCrystalliseRequest = IntentToCrystalliseRequest(
    nino = Nino(nino),
    taxYear = DesTaxYear.fromMtd(taxYear)
  )

  val responseData: IntentToCrystalliseResponse = IntentToCrystalliseResponse(
    calculationId = calculationId
  )

  val responseJson: JsValue = Json.parse(
    s"""
      |{
      |   "calculationId": "$calculationId",
      |   "links":[
      |      {
      |         "href": "/individuals/calculations/$nino/self-assessment/$calculationId",
      |         "method": "GET",
      |         "rel": "self"
      |      },
      |      {
      |         "href": "/individuals/calculations/crystallisation/$nino/$taxYear/crystallise",
      |         "method": "POST",
      |         "rel": "crystallise"
      |      }
      |   ]
      |}
    """.stripMargin
  )

  "IntentToCrystalliseController" should {
    "return OK" when {
      "happy path" in new Test {

        MockIntentToCrystalliseRequestParser
          .parse(rawData)
          .returns(Right(requestData))

        MockIntentToCrystalliseService
          .submitIntent(requestData)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, responseData))))

        MockHateoasFactory
          .wrap(responseData, IntentToCrystalliseHateaosData(nino, taxYear, calculationId))
          .returns(HateoasWrapper(responseData, links))

        val result: Future[Result] = controller.submitIntent(nino, taxYear)(fakeRequest)

        status(result) shouldBe OK
        contentAsJson(result) shouldBe responseJson
        header("X-CorrelationId", result) shouldBe Some(correlationId)
      }
    }

    "return the error as per spec" when {
      "parser errors occur" must {
        def errorsFromParserTester(error: MtdError, expectedStatus: Int): Unit = {
          s"a ${error.code} error is returned from the parser" in new Test {

            MockIntentToCrystalliseRequestParser
              .parse(rawData)
              .returns(Left(ErrorWrapper(Some(correlationId), error, None, expectedStatus)))

            val result: Future[Result] = controller.submitIntent(nino, taxYear)(fakeRequest)

            status(result) shouldBe expectedStatus
            contentAsJson(result) shouldBe Json.toJson(error)
            header("X-CorrelationId", result) shouldBe Some(correlationId)
          }
        }

        val input = Seq(
          (BadRequestError, BAD_REQUEST),
          (NinoFormatError, BAD_REQUEST),
          (TaxYearFormatError, BAD_REQUEST),
          (RuleTaxYearRangeInvalidError, BAD_REQUEST),
          (RuleTaxYearNotSupportedError, BAD_REQUEST)
        )

        input.foreach(args => (errorsFromParserTester _).tupled(args))
      }

      "service errors occur" must {
        def serviceErrors(mtdError: MtdError, expectedStatus: Int): Unit = {
          s"a $mtdError error is returned from the service" in new Test {

            MockIntentToCrystalliseRequestParser
              .parse(rawData)
              .returns(Right(requestData))

            MockIntentToCrystalliseService
              .submitIntent(requestData)
              .returns(Future.successful(Left(ErrorWrapper(Some(correlationId), mtdError, None, expectedStatus))))

            val result: Future[Result] = controller.submitIntent(nino, taxYear)(fakeRequest)

            status(result) shouldBe expectedStatus
            contentAsJson(result) shouldBe Json.toJson(mtdError)
            header("X-CorrelationId", result) shouldBe Some(correlationId)
          }
        }

        val input = Seq(
          (NinoFormatError, BAD_REQUEST),
          (TaxYearFormatError, BAD_REQUEST),
          (RuleNoSubmissionsExistError, FORBIDDEN),
          (RuleFinalDeclarationReceivedError, FORBIDDEN),
          (NotFoundError, NOT_FOUND),
          (DownstreamError, INTERNAL_SERVER_ERROR)
        )

        input.foreach(args => (serviceErrors _).tupled(args))
      }
    }
  }
}