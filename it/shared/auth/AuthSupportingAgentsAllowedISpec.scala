package shared.auth

import com.github.tomakehurst.wiremock.stubbing.StubMapping
import play.api.http.HeaderNames.ACCEPT
import play.api.http.Status.OK
import play.api.libs.json.JsValue
import play.api.libs.ws.{WSRequest, WSResponse}
import play.api.test.Helpers.AUTHORIZATION
import shared.services.{AuditStub, AuthStub, DownstreamStub, MtdIdLookupStub}
import shared.support.IntegrationBaseSpec

abstract class AuthSupportingAgentsAllowedISpec extends IntegrationBaseSpec {

  /** The API's latest version, e.g. "1.0".
    */
  protected val callingApiVersion: String

  /** As the IT supplies the "supported" config below, this can be any endpoint IF there's no actual "supporting agents allowed" endpoint in the API.
    */
  protected val supportingAgentsAllowedEndpoint: String

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
      s"api.supporting-agent-endpoints.$supportingAgentsAllowedEndpoint" -> "true"
    ) ++ super.servicesConfig

  protected val nino = "AA123456A"

  "Calling an endpoint that allows supporting agents" when {
    "the client is the primary agent" should {
      "return a success response" in new Test {
        def setupStubs(): StubMapping = {
          AuditStub.audit()
          MtdIdLookupStub.ninoFound(nino)

          AuthStub.authorisedWithAgentAffinityGroup()
          AuthStub.authorisedWithPrimaryAgentEnrolment()

          DownstreamStub
            .when(downstreamHttpMethod, downstreamUri)
            .thenReturn(downstreamSuccessStatus, maybeDownstreamResponseJson)
        }

        val response: WSResponse = sendMtdRequest(request)
        response.status shouldBe expectedMtdSuccessStatus
      }
    }

    "the client is a supporting agent" should {
      "return a success response" in new Test {
        def setupStubs(): StubMapping = {
          AuditStub.audit()
          MtdIdLookupStub.ninoFound(nino)

          AuthStub.authorisedWithAgentAffinityGroup()
          AuthStub.unauthorisedForPrimaryAgentEnrolment()
          AuthStub.authorisedWithSupportingAgentEnrolment()

          DownstreamStub
            .when(downstreamHttpMethod, downstreamUri)
            .thenReturn(downstreamSuccessStatus, maybeDownstreamResponseJson)
        }

        val response: WSResponse = sendMtdRequest(request)
        response.status shouldBe expectedMtdSuccessStatus
      }
    }
  }

  protected trait Test {

    def setupStubs(): StubMapping

    protected def request: WSRequest = {
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
