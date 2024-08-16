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

package shared.services

import org.scalamock.handlers.CallHandler
import shared.config.{ConfidenceLevelConfig, MockAppConfig}
import shared.models.auth.UserDetails
import shared.models.errors.{ClientOrAgentNotAuthorisedError, InternalError}
import shared.models.outcomes.AuthOutcome
import shared.services.EnrolmentsAuthService.{
  authorisationDisabledPredicate,
  authorisationEnabledPredicate,
  mtdEnrolmentPredicate,
  supportingAgentAuthPredicate
}
import uk.gov.hmrc.auth.core.AffinityGroup.{Agent, Individual, Organisation}
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.auth.core.authorise.Predicate
import uk.gov.hmrc.auth.core.retrieve.v2.Retrievals._
import uk.gov.hmrc.auth.core.retrieve.{EmptyRetrieval, Retrieval, ~}
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class EnrolmentsAuthServiceSpec extends ServiceSpec with MockAppConfig {

  private val mtdId = "123567890"

  "calling .authorised" when {

    "confidence level checks are on" should {
      behave like authService(
        authValidationEnabled = true,
        authorisationEnabledPredicate(mtdId),
        mtdEnrolmentPredicate(mtdId),
        supportingAgentAuthPredicate(mtdId)
      )
    }

    "confidence level checks are off" should {
      behave like authService(
        authValidationEnabled = false,
        authorisationDisabledPredicate(mtdId),
        mtdEnrolmentPredicate(mtdId),
        supportingAgentAuthPredicate(mtdId)
      )
    }

    def authService(
        authValidationEnabled: Boolean,
        initialPredicate: Predicate,
        primaryAgentPredicate: Predicate,
        supportingAgentPredicate: Predicate
    ): Unit = {
      behave like authorisedIndividual(authValidationEnabled, initialPredicate)
      behave like authorisedOrganisation(authValidationEnabled, initialPredicate)

      behave like authorisedAgentsMissingArn(authValidationEnabled, initialPredicate)
      behave like authorisedPrimaryAgent(authValidationEnabled, initialPredicate, primaryAgentPredicate)
      behave like authorisedSupportingAgent(authValidationEnabled, initialPredicate, primaryAgentPredicate, supportingAgentPredicate)

      behave like disallowSupportingAgentForPrimaryOnlyEndpoint(authValidationEnabled, initialPredicate, primaryAgentPredicate)

      behave like disallowUsersWithoutEnrolments(authValidationEnabled, initialPredicate)
      behave like disallowWhenNotLoggedIn(authValidationEnabled, initialPredicate)
    }

    def authorisedIndividual(authValidationEnabled: Boolean, initialPredicate: Predicate): Unit =
      "allow authorised individuals" in new Test {
        mockConfidenceLevelCheckConfig(authValidationEnabled = authValidationEnabled)

        val retrievalsResult = new ~(Some(Individual), Enrolments(Set.empty))

        MockedAuthConnector
          .authorised(initialPredicate, affinityGroup and authorisedEnrolments)
          .once()
          .returns(Future.successful(retrievalsResult))

        val result: AuthOutcome = await(enrolmentsAuthService.authorised(mtdId))
        result shouldBe Right(UserDetails("", "Individual", None))
      }

    def authorisedOrganisation(authValidationEnabled: Boolean, initialPredicate: Predicate): Unit =
      "allow authorised organisations" in new Test {
        val retrievalsResult = new ~(Some(Organisation), Enrolments(Set.empty))
        mockConfidenceLevelCheckConfig(authValidationEnabled = authValidationEnabled)

        MockedAuthConnector
          .authorised(initialPredicate, affinityGroup and authorisedEnrolments)
          .once()
          .returns(Future.successful(retrievalsResult))

        val result: AuthOutcome = await(enrolmentsAuthService.authorised(mtdId))
        result shouldBe Right(UserDetails("", "Organisation", None))
      }

    def authorisedAgentsMissingArn(authValidationEnabled: Boolean, initialPredicate: Predicate): Unit = {
      "disallow agents that are missing an ARN" in new Test {
        val retrievalsResult = new ~(
          Some(Agent),
          Enrolments(
            Set(Enrolment("HMRC-AS-AGENT", List(EnrolmentIdentifier("SomeOtherIdentifier", "123567890")), "Active"))
          )
        )

        mockConfidenceLevelCheckConfig(authValidationEnabled = authValidationEnabled)

        MockedAuthConnector
          .authorised(initialPredicate, affinityGroup and authorisedEnrolments)
          .once()
          .returns(Future.successful(retrievalsResult))

        val result: AuthOutcome = await(enrolmentsAuthService.authorised(mtdId))
        result shouldBe Left(InternalError)
      }
    }

    def authorisedPrimaryAgent(
        authValidationEnabled: Boolean,
        initialPredicate: Predicate,
        primaryAgentPredicate: Predicate
    ): Unit =
      "allow authorised Primary agents with ARN" in new Test {
        val arn = "123567890"
        val enrolments: Enrolments = Enrolments(
          Set(
            Enrolment(
              "HMRC-AS-AGENT",
              List(EnrolmentIdentifier("AgentReferenceNumber", arn)),
              "Active"
            ))
        )

        val initialRetrievalsResult = new ~(Some(Agent), enrolments)

        MockedAuthConnector
          .authorised(initialPredicate, affinityGroup and authorisedEnrolments)
          .once()
          .returns(Future.successful(initialRetrievalsResult))

        MockedAuthConnector
          .authorised(primaryAgentPredicate, EmptyRetrieval)
          .once()
          .returns(Future.successful(EmptyRetrieval))

        mockConfidenceLevelCheckConfig(authValidationEnabled = authValidationEnabled)

        val result: AuthOutcome = await(enrolmentsAuthService.authorised(mtdId))
        result shouldBe Right(UserDetails("", "Agent", Some(arn)))
      }

    def authorisedSupportingAgent(
        authValidationEnabled: Boolean,
        initialPredicate: Predicate,
        primaryAgentPredicate: Predicate,
        supportingAgentPredicate: Predicate
    ): Unit =
      "allow authorised Supporting agents with ARN" in new Test {
        val arn = "123567890"
        val enrolments: Enrolments = Enrolments(
          Set(
            Enrolment(
              "HMRC-AS-AGENT",
              List(EnrolmentIdentifier("AgentReferenceNumber", arn)),
              "Active"
            ))
        )

        val initialRetrievalsResult = new ~(Some(Agent), enrolments)

        MockedAuthConnector
          .authorised(initialPredicate, affinityGroup and authorisedEnrolments)
          .once()
          .returns(Future.successful(initialRetrievalsResult))

        MockedAuthConnector
          .authorised(primaryAgentPredicate, EmptyRetrieval)
          .once()
          .returns(Future.failed(InsufficientEnrolments()))

        MockedAuthConnector
          .authorised(supportingAgentPredicate, EmptyRetrieval)
          .once()
          .returns(Future.successful(EmptyRetrieval))

        mockConfidenceLevelCheckConfig(authValidationEnabled = authValidationEnabled)

        val result: AuthOutcome = await(enrolmentsAuthService.authorised(mtdId, endpointAllowsSupportingAgents = true))
        result shouldBe Right(UserDetails("", "Agent", Some(arn)))
      }

    def disallowSupportingAgentForPrimaryOnlyEndpoint(
        authValidationEnabled: Boolean,
        initialPredicate: Predicate,
        primaryAgentPredicate: Predicate
    ): Unit =
      "disallow Supporting agents for a primary-only endpoint" in new Test {
        val arn = "123567890"
        val enrolments: Enrolments = Enrolments(
          Set(
            Enrolment(
              "HMRC-AS-AGENT",
              List(EnrolmentIdentifier("AgentReferenceNumber", arn)),
              "Active"
            ))
        )

        val initialRetrievalsResult = new ~(Some(Agent), enrolments)

        MockedAuthConnector
          .authorised(initialPredicate, affinityGroup and authorisedEnrolments)
          .once()
          .returns(Future.successful(initialRetrievalsResult))

        MockedAuthConnector
          .authorised(primaryAgentPredicate, EmptyRetrieval)
          .once()
          .returns(Future.failed(InsufficientEnrolments()))

        mockConfidenceLevelCheckConfig(authValidationEnabled = authValidationEnabled)

        val result: AuthOutcome = await(enrolmentsAuthService.authorised(mtdId))
        result shouldBe Left(ClientOrAgentNotAuthorisedError)
      }

    def disallowWhenNotLoggedIn(authValidationEnabled: Boolean, initialPredicate: Predicate): Unit =
      "disallow users that are not logged in" in new Test {
        mockConfidenceLevelCheckConfig(authValidationEnabled = authValidationEnabled)

        MockedAuthConnector
          .authorised(initialPredicate, affinityGroup and authorisedEnrolments)
          .once()
          .returns(Future.failed(MissingBearerToken()))

        val result: AuthOutcome = await(enrolmentsAuthService.authorised(mtdId))
        result shouldBe Left(ClientOrAgentNotAuthorisedError)
      }

    def disallowUsersWithoutEnrolments(authValidationEnabled: Boolean, initialPredicate: Predicate): Unit =
      "disallow users without enrolments" in new Test {
        mockConfidenceLevelCheckConfig(authValidationEnabled = authValidationEnabled)

        MockedAuthConnector
          .authorised(initialPredicate, affinityGroup and authorisedEnrolments)
          .once()
          .returns(Future.failed(InsufficientEnrolments()))

        val result: AuthOutcome = await(enrolmentsAuthService.authorised(mtdId))
        result shouldBe Left(ClientOrAgentNotAuthorisedError)
      }
  }

  trait Test {
    val mockAuthConnector: AuthConnector = mock[AuthConnector]

    lazy val enrolmentsAuthService = new EnrolmentsAuthService(mockAuthConnector, mockAppConfig)

    object MockedAuthConnector {

      def authorised[A](predicate: Predicate, retrievals: Retrieval[A]): CallHandler[Future[A]] = {
        (mockAuthConnector
          .authorise[A](_: Predicate, _: Retrieval[A])(_: HeaderCarrier, _: ExecutionContext))
          .expects(predicate, retrievals, *, *)
      }

    }

    def mockConfidenceLevelCheckConfig(authValidationEnabled: Boolean): Unit = {
      MockedAppConfig.confidenceLevelConfig
        .anyNumberOfTimes()
        .returns(
          ConfidenceLevelConfig(
            confidenceLevel = ConfidenceLevel.L200,
            definitionEnabled = true,
            authValidationEnabled = authValidationEnabled
          )
        )
    }

  }

}
