package shared.auth

import com.github.tomakehurst.wiremock.stubbing.StubMapping
import play.api.http.HeaderNames.ACCEPT
import play.api.http.Status.{FORBIDDEN, INTERNAL_SERVER_ERROR, OK}
import play.api.libs.json.JsValue
import play.api.libs.ws.{WSRequest, WSResponse}
import play.api.test.Helpers.AUTHORIZATION
import shared.models.errors.{ClientOrAgentNotAuthorisedError, InternalError}
import shared.services.{AuditStub, AuthStub, DownstreamStub, MtdIdLookupStub}
import shared.support.IntegrationBaseSpec

abstract class AuthMainAgentsOnlyISpec extends IntegrationBaseSpec {

  /** The API's latest version, e.g. "1.0".
    */
  protected val callingApiVersion: String

  /** As the IT supplies the "supported" config below, this can be any endpoint IF there's no actual "main agents only" endpoint in the API.
    */
  protected val supportingAgentsNotAllowedEndpoint: String

  protected def sendMtdRequest(request: WSRequest): WSResponse

  protected val mtdUrl: String

  protected val downstreamUri: String

  protected val maybeDownstreamResponseJson: Option[JsValue]

  protected val downstreamHttpMethod: DownstreamStub.HTTPMethod = DownstreamStub.POST

  protected val downstreamSuccessStatus: Int = OK

  protected val expectedMtdSuccessStatus: Int = OK

  /** One endpoint where supporting agents are allowed.
    */
  override def servicesConfig: Map[String, Any] =
    Map(
      s"api.supporting-agent-endpoints.$supportingAgentsNotAllowedEndpoint" -> "false"
    ) ++ super.servicesConfig

  protected val nino = "AA123456A"

  "Calling an endpoint that only allows primary agents" when {
    "the client is the primary agent" should {
      "return a success response" in new Test {
        override def setupStubs(): StubMapping = {
          AuditStub.audit()
          MtdIdLookupStub.ninoFound(nino)

          AuthStub.authorisedWithAgentAffinityGroup()
          AuthStub.authorisedWithPrimaryAgentEnrolment()

          DownstreamStub
            .when(downstreamHttpMethod, downstreamUri)
            .thenReturn(downstreamSuccessStatus, maybeDownstreamResponseJson)
        }

        val response: WSResponse = sendMtdRequest(request())
        response.status shouldBe expectedMtdSuccessStatus
      }
    }

    "the client is a supporting agent" should {
      "return a 403 response" in new Test {

        override def setupStubs(): StubMapping = {
          AuditStub.audit()
          MtdIdLookupStub.ninoFound(nino)

          AuthStub.authorisedWithAgentAffinityGroup()
          AuthStub.unauthorisedForPrimaryAgentEnrolment()
        }

        val response: WSResponse = sendMtdRequest(request())

        response.status shouldBe FORBIDDEN
        response.body should include(ClientOrAgentNotAuthorisedError.message)
      }
    }
  }

  "Calling an endpoint" when {

    "MTD ID lookup succeeds but the user isn't logged in" should {

      "return a 403 response" in new Test {
        override def setupStubs(): StubMapping = {
          AuditStub.audit()
          MtdIdLookupStub.ninoFound(nino)
          AuthStub.unauthorisedNotLoggedIn()
        }

        val response: WSResponse = sendMtdRequest(request())
        response.status shouldBe FORBIDDEN
      }
    }

    "MTD ID lookup succeeds but the user isn't authorised to access it" should {

      "return a 403 response" in new Test {
        override def setupStubs(): StubMapping = {
          AuditStub.audit()
          MtdIdLookupStub.ninoFound(nino)
          AuthStub.unauthorisedOther()
        }

        val response: WSResponse = sendMtdRequest(request())
        response.status shouldBe FORBIDDEN
        response.body should include(ClientOrAgentNotAuthorisedError.message)
      }
    }

    "MTD ID lookup fails with a 500" should {

      "return a 500 response" in new Test {
        override def setupStubs(): StubMapping = {
          AuditStub.audit()
          MtdIdLookupStub.error(nino, INTERNAL_SERVER_ERROR)
        }

        val response: WSResponse = sendMtdRequest(request())
        response.status shouldBe INTERNAL_SERVER_ERROR
        response.body should include(InternalError.message)
      }
    }

    "MTD ID lookup fails with a 403" should {

      "return a 403 response" in new Test {
        override def setupStubs(): StubMapping = {
          AuditStub.audit()
          MtdIdLookupStub.error(nino, FORBIDDEN)
        }

        val response: WSResponse = sendMtdRequest(request())
        response.status shouldBe FORBIDDEN
        response.body should include(ClientOrAgentNotAuthorisedError.message)
      }
    }
  }

  protected trait Test {

    def setupStubs(): StubMapping

    protected def request(): WSRequest = {
      AuthStub.resetAll()
      setupStubs()
      buildRequest(mtdUrl)
        .withHttpHeaders(
          (ACCEPT, s"application/vnd.hmrc.$callingApiVersion+json"),
          (AUTHORIZATION, "Bearer 123")
        )
    }

  }

}
