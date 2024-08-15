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

package shared.controllers

import cats.data.Validated
import cats.data.Validated.{Invalid, Valid}
import cats.implicits.catsSyntaxValidatedId
import org.scalamock.handlers.CallHandler
import play.api.http.{HeaderNames, Status}
import play.api.libs.json.{JsString, Json, OWrites}
import play.api.mvc.{AnyContent, AnyContentAsEmpty}
import play.api.test.{FakeRequest, ResultExtractors}
import shared.config.Deprecation.{Deprecated, NotDeprecated}
import shared.config.{AppConfig, Deprecation, MockAppConfig}
import shared.controllers.validators.Validator
import shared.hateoas._
import shared.models.audit.{AuditError, AuditEvent, AuditResponse, GenericAuditDetail}
import shared.models.auth.UserDetails
import shared.models.errors.{ErrorWrapper, InternalError, MtdError, NinoFormatError}
import shared.models.outcomes.ResponseWrapper
import shared.routing.{Version, Version3}
import shared.services.{MockAuditService, ServiceOutcome}
import shared.utils.{MockIdGenerator, UnitSpec}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.audit.http.connector.AuditResult

import java.time.LocalDateTime
import scala.concurrent.{ExecutionContext, ExecutionContextExecutor, Future}

class RequestHandlerSpec
    extends UnitSpec
    with MockAuditService
    with MockHateoasFactory
    with MockIdGenerator
    with Status
    with HeaderNames
    with ResultExtractors
    with ControllerSpecHateoasSupport
    with MockAppConfig {

  private implicit val ec: ExecutionContextExecutor = ExecutionContext.global

  private val successResponseJson = Json.obj("result" -> "SUCCESS!")
  private val successCode         = ACCEPTED

  private val generatedCorrelationId = "generatedCorrelationId"
  private val serviceCorrelationId   = "serviceCorrelationId"

  MockedIdGenerator.generateCorrelationId.returns(generatedCorrelationId).anyNumberOfTimes()

  implicit val endpointLogContext: EndpointLogContext =
    EndpointLogContext(controllerName = "SomeController", endpointName = "someEndpoint")

  private val versionHeader = HeaderNames.ACCEPT -> "application/vnd.hmrc.3.0+json"

  implicit val hc: HeaderCarrier   = HeaderCarrier()
  implicit val ctx: RequestContext = RequestContext.from(mockIdGenerator, endpointLogContext)

  private val userDetails = UserDetails("mtdId", "Individual", Some("agentReferenceNumber"))

  implicit val userRequest: UserRequest[AnyContent] = {
    val fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest().withHeaders(versionHeader)
    UserRequest[AnyContent](userDetails, fakeRequest)
  }

  implicit val appConfig: AppConfig = mockAppConfig
  private val mockService           = mock[DummyService]

  private def service =
    (mockService.service(_: Input.type)(_: RequestContext, _: ExecutionContext)).expects(Input, *, *).anyNumberOfTimes()

  case object Input

  case object Output {
    implicit val writes: OWrites[Output.type] = _ => successResponseJson
  }

  case object HData extends HateoasData

  implicit object HLinksFactory extends HateoasLinksFactory[Output.type, HData.type] {
    override def links(appConfig: AppConfig, data: HData.type): Seq[Link] = hateoaslinks
  }

  trait DummyService {
    def service(input: Input.type)(implicit ctx: RequestContext, ec: ExecutionContext): Future[ServiceOutcome[Output.type]]
  }

  def mockDeprecation(deprecationStatus: Deprecation): CallHandler[Validated[String, Deprecation]] =
    MockedAppConfig
      .deprecationFor(Version(userRequest))
      .returns(deprecationStatus.valid)
      .anyNumberOfTimes()

  private val successValidatorForRequest = new Validator[Input.type] {
    def validate: Validated[Seq[MtdError], Input.type] = Valid(Input)
  }

  private val singleErrorValidatorForRequest = new Validator[Input.type] {
    def validate: Validated[Seq[MtdError], Input.type] = Invalid(List(NinoFormatError))
  }

  private val successRequestHandler =
    RequestHandler
      .withValidator(successValidatorForRequest)
      .withService(mockService.service)

  "RequestHandler" when {

    "given a request" must {
      "return the correct response" in {
        val requestHandler = successRequestHandler.withPlainJsonResult(successCode)

        mockDeprecation(NotDeprecated)

        service returns Future.successful(Right(ResponseWrapper(serviceCorrelationId, Output)))

        val result = requestHandler.handleRequest()

        contentAsJson(result) shouldBe successResponseJson
        header("X-CorrelationId", result) shouldBe Some(serviceCorrelationId)
        status(result) shouldBe successCode
      }

      "return no content if required" in {
        val requestHandler = successRequestHandler.withNoContentResult()

        mockDeprecation(NotDeprecated)
        service returns Future.successful(Right(ResponseWrapper(serviceCorrelationId, Output)))

        val result = requestHandler.handleRequest()

        contentAsString(result) shouldBe ""
        header("X-CorrelationId", result) shouldBe Some(serviceCorrelationId)
        status(result) shouldBe NO_CONTENT
      }

      "wrap the response with hateoas links if required§" in {
        val requestHandler = successRequestHandler.withHateoasResult(mockHateoasFactory)(HData, successCode)

        mockDeprecation(NotDeprecated)
        service returns Future.successful(Right(ResponseWrapper(serviceCorrelationId, Output)))

        MockHateoasFactory.wrap(Output, HData) returns HateoasWrapper(Output, hateoaslinks)

        val result = requestHandler.handleRequest()

        contentAsJson(result) shouldBe successResponseJson ++ hateoaslinksJson
        header("X-CorrelationId", result) shouldBe Some(serviceCorrelationId)
        status(result) shouldBe successCode
      }
    }

    "given a request with a RequestCannotBeFulfilled gov-test-scenario header" when {
      val gtsHeaders = List(
        "gov-test-scenario" -> "REQUEST_CANNOT_BE_FULFILLED",
        "Gov-Test-Scenario" -> "REQUEST_CANNOT_BE_FULFILLED",
        "GOV-TEST-SCENARIO" -> "REQUEST_CANNOT_BE_FULFILLED"
      )

      "allowed in config" should {
        "return RuleRequestCannotBeFulfilled error" in {
          val requestHandler = successRequestHandler.withNoContentResult()

          MockedAppConfig.allowRequestCannotBeFulfilledHeader(Version3).returns(true).anyNumberOfTimes()
          mockDeprecation(NotDeprecated)

          val expectedContent = Json.parse(
            """
              |{
              |  "code":"RULE_REQUEST_CANNOT_BE_FULFILLED",
              |  "message":"Custom (will vary in production depending on the actual error)"
              |}
              |""".stripMargin
          )

          for (gtsHeader <- gtsHeaders) {

            val userRequest2 = UserRequest[AnyContent](userDetails, FakeRequest().withHeaders(versionHeader, gtsHeader))
            val result       = requestHandler.handleRequest()(ctx, userRequest2, implicitly[ExecutionContext], mockAppConfig)

            status(result) shouldBe 422
            header("X-CorrelationId", result) shouldBe Some(generatedCorrelationId)
            contentAsJson(result) shouldBe expectedContent
          }
        }
      }

      "not allowed in config" should {
        "return success response, as the Gov-Test-Scenario should be ignored" in {
          val requestHandler = successRequestHandler.withPlainJsonResult(successCode)

          service returns Future.successful(Right(ResponseWrapper(serviceCorrelationId, Output)))

          MockedAppConfig.allowRequestCannotBeFulfilledHeader(Version3).returns(false).anyNumberOfTimes()
          mockDeprecation(NotDeprecated)

          val ctx2: RequestContext = ctx.copy(hc = hc.copy(otherHeaders = List("gov-test-scenario" -> "REQUEST_CANNOT_BE_FULFILLED")))

          val result = requestHandler.handleRequest()(ctx2, userRequest, ec, mockAppConfig)

          contentAsJson(result) shouldBe successResponseJson
          header("X-CorrelationId", result) shouldBe Some(serviceCorrelationId)
          status(result) shouldBe successCode
        }
      }

      "a request is made to a deprecated version" must {
        "return the correct response" when {
          "deprecatedOn and sunsetDate exists" in {

            val requestHandler = successRequestHandler.withPlainJsonResult(successCode)

            service returns Future.successful(Right(ResponseWrapper(serviceCorrelationId, Output)))

            mockDeprecation(
              Deprecated(
                deprecatedOn = LocalDateTime.of(2023, 1, 17, 12, 0),
                sunsetDate = Some(LocalDateTime.of(2024, 1, 17, 12, 0))
              )
            )

            MockedAppConfig.apiDocumentationUrl().returns("http://someUrl").anyNumberOfTimes()

            val result = requestHandler.handleRequest()

            contentAsJson(result) shouldBe successResponseJson
            header("X-CorrelationId", result) shouldBe Some(serviceCorrelationId)
            header("Deprecation", result) shouldBe Some("Tue, 17 Jan 2023 12:00:00 GMT")
            header("Sunset", result) shouldBe Some("Wed, 17 Jan 2024 12:00:00 GMT")
            header("Link", result) shouldBe Some("http://someUrl")

            status(result) shouldBe successCode
          }

          "only deprecatedOn exists" in {
            val requestHandler = successRequestHandler.withPlainJsonResult(successCode)

            service returns Future.successful(Right(ResponseWrapper(serviceCorrelationId, Output)))

            mockDeprecation(
              Deprecated(
                deprecatedOn = LocalDateTime.of(2023, 1, 17, 12, 0),
                None
              )
            )
            MockedAppConfig.apiDocumentationUrl().returns("http://someUrl").anyNumberOfTimes()

            val result = requestHandler.handleRequest()

            contentAsJson(result) shouldBe successResponseJson
            header("X-CorrelationId", result) shouldBe Some(serviceCorrelationId)
            header("Deprecation", result) shouldBe Some("Tue, 17 Jan 2023 12:00:00 GMT")
            header("Sunset", result) shouldBe None
            header("Link", result) shouldBe Some("http://someUrl")
            status(result) shouldBe successCode
          }
        }
      }
    }

    "a request fails with validation errors" must {
      "return the errors" in {
        val requestHandler = RequestHandler
          .withValidator(singleErrorValidatorForRequest)
          .withService(mockService.service)
          .withPlainJsonResult(successCode)

        mockDeprecation(NotDeprecated)

        val result = requestHandler.handleRequest()

        contentAsJson(result) shouldBe NinoFormatError.asJson
        header("X-CorrelationId", result) shouldBe Some(generatedCorrelationId)
        status(result) shouldBe NinoFormatError.httpStatus
      }
    }

    "a request fails with service errors" must {
      "return the errors" in {
        val requestHandler = successRequestHandler.withPlainJsonResult(successCode)

        mockDeprecation(NotDeprecated)
        service returns Future.successful(Left(ErrorWrapper(serviceCorrelationId, NinoFormatError)))

        val result = requestHandler.handleRequest()

        contentAsJson(result) shouldBe NinoFormatError.asJson
        header("X-CorrelationId", result) shouldBe Some(serviceCorrelationId)
        status(result) shouldBe NinoFormatError.httpStatus
      }
    }

    "auditing is configured" when {
      val params    = Map("param" -> "value")
      val auditType = "type"
      val txName    = "txName"

      mockDeprecation(NotDeprecated)

      val requestBody = Some(JsString("REQUEST BODY"))

      def auditHandler(includeResponse: Boolean = false): AuditHandler = AuditHandler(
        mockAuditService,
        auditType = auditType,
        transactionName = txName,
        apiVersion = Version3,
        params = params,
        requestBody = requestBody,
        includeResponse = includeResponse
      )

      val basicRequestHandler = successRequestHandler.withPlainJsonResult(successCode)

      val basicErrorRequestHandler = RequestHandler
        .withValidator(singleErrorValidatorForRequest)
        .withService(mockService.service)
        .withPlainJsonResult(BAD_REQUEST)

      def verifyAudit(correlationId: String, auditResponse: AuditResponse): CallHandler[Future[AuditResult]] =
        MockedAuditService.verifyAuditEvent(
          AuditEvent(
            auditType = auditType,
            transactionName = txName,
            GenericAuditDetail(
              userDetails,
              params = params,
              apiVersion = Version3.name,
              requestBody = requestBody,
              `X-CorrelationId` = correlationId,
              auditResponse = auditResponse)
          ))

      "a request is successful" when {
        "no response is to be audited" must {
          "audit without the response" in {
            val requestHandler = basicRequestHandler.withAuditing(auditHandler())

            mockDeprecation(NotDeprecated)
            service returns Future.successful(Right(ResponseWrapper(serviceCorrelationId, Output)))

            val result = requestHandler.handleRequest()

            contentAsJson(result) shouldBe successResponseJson
            header("X-CorrelationId", result) shouldBe Some(serviceCorrelationId)
            status(result) shouldBe successCode

            verifyAudit(serviceCorrelationId, AuditResponse(successCode, Right(None)))
          }
        }

        "the response is to be audited" must {
          "audit with the response" in {
            val requestHandler = basicRequestHandler.withAuditing(auditHandler(includeResponse = true))

            mockDeprecation(NotDeprecated)
            service returns Future.successful(Right(ResponseWrapper(serviceCorrelationId, Output)))

            val result = requestHandler.handleRequest()

            contentAsJson(result) shouldBe successResponseJson
            header("X-CorrelationId", result) shouldBe Some(serviceCorrelationId)
            status(result) shouldBe successCode

            verifyAudit(serviceCorrelationId, AuditResponse(successCode, Right(Some(successResponseJson))))
          }
        }
      }

      "a request fails with validation errors" must {
        "audit the failure" in {
          val requestHandler = basicErrorRequestHandler.withAuditing(auditHandler())

          mockDeprecation(NotDeprecated)

          val result = requestHandler.handleRequest()

          contentAsJson(result) shouldBe NinoFormatError.asJson
          header("X-CorrelationId", result) shouldBe Some(generatedCorrelationId)
          status(result) shouldBe NinoFormatError.httpStatus

          verifyAudit(generatedCorrelationId, AuditResponse(NinoFormatError.httpStatus, Left(List(AuditError(NinoFormatError.code)))))
        }
      }

      "a request fails with service errors" must {
        "audit the failure" in {
          val requestHandler = basicRequestHandler.withAuditing(auditHandler())

          mockDeprecation(NotDeprecated)

          service returns Future.successful(Left(ErrorWrapper(serviceCorrelationId, NinoFormatError)))

          val result = requestHandler.handleRequest()

          contentAsJson(result) shouldBe NinoFormatError.asJson
          header("X-CorrelationId", result) shouldBe Some(serviceCorrelationId)
          status(result) shouldBe NinoFormatError.httpStatus

          verifyAudit(
            serviceCorrelationId,
            AuditResponse(NinoFormatError.httpStatus, Left(List(AuditError(NinoFormatError.code))))
          )
        }
      }
    }

    "given an error handler that doesn't handle an error" should {
      "return an InternalServerError" in {
        mockDeprecation(NotDeprecated)
        service returns Future.successful(Left(ErrorWrapper(serviceCorrelationId, NinoFormatError)))

        val errorHandler = ErrorHandling {
          case _: ErrorWrapper if false =>
            throw new Exception("Should not have been matched")
        }

        val requestHandler = successRequestHandler.withErrorHandling(errorHandler)

        val result = requestHandler.handleRequest()
        status(result) shouldBe InternalError.httpStatus
        contentAsJson(result) shouldBe InternalError.asJson
      }
    }

  }

  "withErrorHandling()" should {
    "return a new RequestHandlerBuilder with the expected error handling" in {
      class CustomErrorHandling extends ErrorHandling(null)

      val result = successRequestHandler.withErrorHandling(new CustomErrorHandling)
      result.errorHandling shouldBe a[CustomErrorHandling]
    }
  }

}
