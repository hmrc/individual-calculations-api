package shared.services

import com.github.tomakehurst.wiremock.stubbing.StubMapping
import play.api.http.Status.{OK, UNAUTHORIZED}
import play.api.libs.json.{JsArray, JsObject, JsValue, Json}
import shared.support.WireMockMethods
import uk.gov.hmrc.auth.core.Enrolment

object AuthStub extends WireMockMethods {

  def authorised(): StubMapping = authorisedWithIndividualAffinityGroup()

  def authorisedWithIndividualAffinityGroup(): StubMapping =
    authorisedWith(affinityGroup = "Individual")

  def authorisedWithAgentAffinityGroup(): StubMapping =
    authorisedWith(affinityGroup = "Agent")

  def authorisedWithPrimaryAgentEnrolment(): StubMapping =
    when(method = POST, uri = authoriseUri)
      .withRequestBody(
        followUpAgentEnrolmentsRequest(EnrolmentsAuthService.mtdEnrolmentPredicate(mtdId = "1234567890"))
      )
      .thenReturn(status = OK, body = JsObject.empty)

  def unauthorisedForPrimaryAgentEnrolment(): StubMapping =
    when(method = POST, uri = authoriseUri)
      .withRequestBody(
        followUpAgentEnrolmentsRequest(EnrolmentsAuthService.mtdEnrolmentPredicate(mtdId = "1234567890"))
      )
      .thenReturn(status = UNAUTHORIZED, headers = Map("WWW-Authenticate" -> """MDTP detail="FailedRelationship""""))

  def authorisedWithSupportingAgentEnrolment(): StubMapping =
    when(method = POST, uri = authoriseUri)
      .withRequestBody(
        followUpAgentEnrolmentsRequest(EnrolmentsAuthService.supportingAgentAuthPredicate(mtdId = "1234567890"))
      )
      .thenReturn(status = OK, body = JsObject.empty)

  def unauthorisedForSupportingAgentEnrolment(): StubMapping =
    when(method = POST, uri = authoriseUri)
      .withRequestBody(
        followUpAgentEnrolmentsRequest(EnrolmentsAuthService.supportingAgentAuthPredicate(mtdId = "1234567890"))
      )
      .thenReturn(status = UNAUTHORIZED, headers = Map("WWW-Authenticate" -> """MDTP detail="FailedRelationship""""))

  def unauthorisedNotLoggedIn(): StubMapping =
    // Note that MissingBearerToken extends NoActiveSession
    when(method = POST, uri = authoriseUri)
      .thenReturn(status = UNAUTHORIZED, headers = Map("WWW-Authenticate" -> """MDTP detail="MissingBearerToken""""))

  def unauthorisedOther(): StubMapping =
    when(method = POST, uri = authoriseUri)
      .thenReturn(status = UNAUTHORIZED, headers = Map("WWW-Authenticate" -> """MDTP detail="InvalidBearerToken""""))

  private def authorisedWith(affinityGroup: String): StubMapping =
    when(method = POST, uri = authoriseUri)
      .withRequestBody(initialRetrievalsRequestBody)
      .thenReturn(
        status = OK,
        body = successfulAuthResponse(
          affinityGroup = affinityGroup,
          enrolments = initialAgentEnrolmentResponse
        ))

  private def successfulAuthResponse(affinityGroup: String, enrolments: Seq[JsObject]): JsObject =
    Json.obj(
      "affinityGroup"        -> affinityGroup,
      "authorisedEnrolments" -> enrolments
    )

  private def followUpAgentEnrolmentsRequest(enrolment: Enrolment): JsValue = {
    JsObject(
      Map(
        "authorise" -> JsArray(List(enrolment.toJson)),
        "retrieve"  -> JsArray.empty
      ))
  }

  private val initialAgentEnrolmentResponse = List(
    Json.obj(
      "key" -> "HMRC-AS-AGENT",
      "identifiers" -> Json.arr(
        Json.obj(
          "key"   -> "AgentReferenceNumber",
          "value" -> "1234567890"
        )
      )
    )
  )

  private val authoriseUri: String = "/auth/authorise"

  private val initialRetrievalsRequestBody =
    Json.parse("""
      |{
      |	"authorise": [
      |		{
      |		    "$or":
      |		    [
      |		        [
      |		            {
      |		                "affinityGroup": "Individual"
      |		            },
      |		            {
      |		                "confidenceLevel": 200
      |		            },
      |		            {
      |		                "identifiers":
      |		                [
      |		                    {
      |		                        "key": "MTDITID",
      |		                        "value": "1234567890"
      |		                    }
      |		                ],
      |		                "state": "Activated",
      |		                "delegatedAuthRule": "mtd-it-auth",
      |		                "enrolment": "HMRC-MTD-IT"
      |		            }
      |		        ],
      |		        [
      |		            {
      |		                "affinityGroup": "Organisation"
      |		            },
      |		            {
      |		                "identifiers":
      |		                [
      |		                    {
      |		                        "key": "MTDITID",
      |		                        "value": "1234567890"
      |		                    }
      |		                ],
      |		                "state": "Activated",
      |		                "delegatedAuthRule": "mtd-it-auth",
      |		                "enrolment": "HMRC-MTD-IT"
      |		            }
      |		        ],
      |		        [
      |		            {
      |		                "affinityGroup": "Agent"
      |		            },
      |		            {
      |		                "identifiers":
      |		                [],
      |		                "state": "Activated",
      |		                "enrolment": "HMRC-AS-AGENT"
      |		            }
      |		        ]
      |		    ]
      |		}
      |	],
      |	"retrieve": [
      |		"affinityGroup",
      |		"authorisedEnrolments"
      |	]
      |}
      |""".stripMargin)

}
