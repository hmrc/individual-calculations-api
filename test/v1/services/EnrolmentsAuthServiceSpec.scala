/*
 * Copyright 2021 HM Revenue & Customs
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

package v1.services

import config.ConfidenceLevelConfig
import mocks.MockAppConfig
import org.scalamock.handlers.CallHandler
import uk.gov.hmrc.auth.core.AffinityGroup.{Agent, Individual, Organisation}
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.auth.core.authorise.{AlternatePredicate, CompositePredicate, EmptyPredicate, Predicate}
import uk.gov.hmrc.auth.core.retrieve.v2.Retrievals._
import uk.gov.hmrc.auth.core.retrieve.{Retrieval, ~}
import uk.gov.hmrc.http.HeaderCarrier
import v1.models.auth.UserDetails
import v1.models.errors.{DownstreamError, UnauthorisedError}

import scala.concurrent.{ExecutionContext, Future}

class EnrolmentsAuthServiceSpec extends ServiceSpec with MockAppConfig {

  trait Test {
    val mockAuthConnector: AuthConnector = mock[AuthConnector]

    val authRetrievals: Retrieval[Option[AffinityGroup] ~ Enrolments] = affinityGroup and authorisedEnrolments

    object MockedAuthConnector {
      def authorised[A](predicate: Predicate, retrievals: Retrieval[A]): CallHandler[Future[A]] = {
        (mockAuthConnector.authorise[A](_: Predicate, _: Retrieval[A])(_: HeaderCarrier, _: ExecutionContext))
          .expects(predicate, retrievals, *, *)
      }
    }

    lazy val target = new EnrolmentsAuthService(mockAuthConnector, mockAppConfig)
  }

  private val extraPredicatesAnd = CompositePredicate(
    _,
    AlternatePredicate(
      AlternatePredicate(
        CompositePredicate(AffinityGroup.Individual, ConfidenceLevel.L200),
        AffinityGroup.Organisation
      ),
      AffinityGroup.Agent
    )
  )

  "calling .buildPredicate" when {
    "confidence level checks are on" should {
      "return a Predicate containing confidence level 200 on top of the provided Predicate" when {
        "passed a simple Individual Predicate" in new Test {
          MockAppConfig.confidenceLevelCheckEnabled.returns(ConfidenceLevelConfig(definitionEnabled = true, authValidationEnabled = true))

          target.buildPredicate(AffinityGroup.Individual) shouldBe extraPredicatesAnd(AffinityGroup.Individual)
        }
        "passed a complex Individual Predicate" in new Test {
          MockAppConfig.confidenceLevelCheckEnabled.returns(ConfidenceLevelConfig(definitionEnabled = true, authValidationEnabled = true))

          target.buildPredicate(CompositePredicate(AffinityGroup.Individual, EmptyPredicate)) shouldBe {
            extraPredicatesAnd(CompositePredicate(AffinityGroup.Individual, EmptyPredicate))
          }
        }
      }
      "return a Predicate containing only the provided Predicate" when {
        "passed a simple Organisation Predicate" in new Test {
          MockAppConfig.confidenceLevelCheckEnabled.returns(ConfidenceLevelConfig(definitionEnabled = true, authValidationEnabled = true))

          target.buildPredicate(AffinityGroup.Organisation) shouldBe extraPredicatesAnd(AffinityGroup.Organisation)
        }
        "passed a complex Organisation Predicate" in new Test {
          MockAppConfig.confidenceLevelCheckEnabled.returns(ConfidenceLevelConfig(definitionEnabled = true, authValidationEnabled = true))

          target.buildPredicate(CompositePredicate(AffinityGroup.Organisation, EmptyPredicate)) shouldBe {
            extraPredicatesAnd(CompositePredicate(AffinityGroup.Organisation, EmptyPredicate))
          }
        }
        "passed a simple Agent Predicate" in new Test {
          MockAppConfig.confidenceLevelCheckEnabled.returns(ConfidenceLevelConfig(definitionEnabled = true, authValidationEnabled = true))

          target.buildPredicate(AffinityGroup.Agent) shouldBe extraPredicatesAnd(AffinityGroup.Agent)
        }
        "passed a complex Agent Predicate" in new Test {
          MockAppConfig.confidenceLevelCheckEnabled.returns(ConfidenceLevelConfig(definitionEnabled = true, authValidationEnabled = true))

          target.buildPredicate(CompositePredicate(AffinityGroup.Agent, EmptyPredicate)) shouldBe {
            extraPredicatesAnd(CompositePredicate(AffinityGroup.Agent, EmptyPredicate))
          }
        }
      }
    }
    "confidence level checks are off" should {
      "return a Predicate containing only the provided Predicate" when {
        "passed a simple Predicate" in new Test {
          MockAppConfig.confidenceLevelCheckEnabled.returns(ConfidenceLevelConfig(definitionEnabled = true, authValidationEnabled = false))

          target.buildPredicate(AffinityGroup.Individual) shouldBe AffinityGroup.Individual
        }
        "passed a complex Predicate" in new Test {
          MockAppConfig.confidenceLevelCheckEnabled.returns(ConfidenceLevelConfig(definitionEnabled = true, authValidationEnabled = false))

          target.buildPredicate(CompositePredicate(AffinityGroup.Agent, Enrolment("HMRC-AS-AGENT"))) shouldBe
            CompositePredicate(AffinityGroup.Agent, Enrolment("HMRC-AS-AGENT"))
        }
      }
    }
  }

  "calling .authorised" when {
    "confidence level checks are on" should {
      "return user details" in new Test {
        MockAppConfig.confidenceLevelCheckEnabled.returns(ConfidenceLevelConfig(definitionEnabled = true, authValidationEnabled = true))

        val retrievalsResult = new ~(Some(AffinityGroup.Individual), Enrolments(Set.empty))
        val expected = Right(UserDetails("", "Individual", None))

        MockedAuthConnector.authorised(extraPredicatesAnd(EmptyPredicate), authRetrievals)
          .returns(Future.successful(retrievalsResult))

        private val result = await(target.authorised(EmptyPredicate))

        result shouldBe expected
      }
    }

    "the user is an authorised individual" should {
      "return the 'Individual' user type in the user details" in new Test {

        MockAppConfig.confidenceLevelCheckEnabled.returns(ConfidenceLevelConfig(definitionEnabled = true, authValidationEnabled = false))

        val retrievalsResult = new ~(Some(Individual), Enrolments(Set.empty))
        val expected = Right(UserDetails("", "Individual", None))

        MockedAuthConnector.authorised(EmptyPredicate, authRetrievals)
          .returns(Future.successful(retrievalsResult))

        private val result = await(target.authorised(EmptyPredicate))

        result shouldBe expected
      }
    }

    "the user is an authorised organisation" should {
      "return the 'Organisation' user type in the user details" in new Test {

        MockAppConfig.confidenceLevelCheckEnabled.returns(ConfidenceLevelConfig(definitionEnabled = true, authValidationEnabled = false))

        val retrievalsResult = new ~(Some(Organisation), Enrolments(Set.empty))
        val expected = Right(UserDetails("", "Organisation", None))

        MockedAuthConnector.authorised(EmptyPredicate, authRetrievals)
          .returns(Future.successful(retrievalsResult))

        private val result = await(target.authorised(EmptyPredicate))

        result shouldBe expected
      }
    }

    "the user is an agent with missing ARN" should {
      val arn = "123567890"
      val incompleteEnrolments = Enrolments(
        Set(
          Enrolment(
            "HMRC-AS-AGENT",
            Seq(EnrolmentIdentifier("SomeOtherIdentifier", arn)),
            "Active"
          )
        )
      )

      val retrievalsResult = new ~(Some(Agent), incompleteEnrolments)

      "return an error" in new Test {

        MockAppConfig.confidenceLevelCheckEnabled.returns(ConfidenceLevelConfig(definitionEnabled = true, authValidationEnabled = false))

        val expected = Left(DownstreamError)

        MockedAuthConnector.authorised(EmptyPredicate, authRetrievals)
          .returns(Future.successful(retrievalsResult))

        private val result = await(target.authorised(EmptyPredicate))

        result shouldBe expected
      }
    }

    "the user is not logged in" should {
      "return an unauthenticated error" in new Test {

        MockAppConfig.confidenceLevelCheckEnabled.returns(ConfidenceLevelConfig(definitionEnabled = true, authValidationEnabled = false))

        val expected = Left(UnauthorisedError)

        MockedAuthConnector.authorised(EmptyPredicate, authRetrievals)
          .returns(Future.failed(MissingBearerToken()))

        private val result = await(target.authorised(EmptyPredicate))

        result shouldBe expected
      }
    }

    "the user is not authorised" should {
      "return an unauthorised error" in new Test {

        MockAppConfig.confidenceLevelCheckEnabled.returns(ConfidenceLevelConfig(definitionEnabled = true, authValidationEnabled = false))

        val expected = Left(UnauthorisedError)

        MockedAuthConnector.authorised(EmptyPredicate, authRetrievals)
          .returns(Future.failed(InsufficientEnrolments()))

        private val result = await(target.authorised(EmptyPredicate))

        result shouldBe expected
      }
    }

    "calling getAgentReferenceFromEnrolments" should {
      "return a valid AgentReferenceNumber" when {
        "a valid agent Enrolment is supplied" in new Test{
          val expectedArn = "123567890"
          val actualArn: Option[String] = target.getAgentReferenceFromEnrolments(Enrolments(
            Set(
              Enrolment(
                "HMRC-AS-AGENT",
                Seq(EnrolmentIdentifier("AgentReferenceNumber", expectedArn)),
                "Active"
              )
            )
          ))

          actualArn shouldBe Some(expectedArn)
        }
      }
    }
  }
}