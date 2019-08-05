/*
 * Copyright 2019 HM Revenue & Customs
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

import org.scalamock.handlers.CallHandler
import uk.gov.hmrc.auth.core.AffinityGroup.{Agent, Individual, Organisation}
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.auth.core.authorise.{EmptyPredicate, Predicate}
import uk.gov.hmrc.auth.core.retrieve.Retrievals._
import uk.gov.hmrc.auth.core.retrieve._
import uk.gov.hmrc.http.HeaderCarrier
import v1.models.auth.UserDetails
import v1.models.errors.{DownstreamError, UnauthorisedError}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ExecutionContext, Future}

class EnrolmentsAuthServiceSpec extends ServiceSpec {

  trait Test {
    val mockAuthConnector: AuthConnector = mock[AuthConnector]

    val authRetrievals: Retrieval[Option[AffinityGroup] ~ Enrolments] = affinityGroup and authorisedEnrolments

    object MockedAuthConnector {
      def authorised[A](predicate: Predicate, retrievals: Retrieval[A]): CallHandler[Future[A]] = {
        (mockAuthConnector.authorise[A](_: Predicate, _: Retrieval[A])(_: HeaderCarrier, _: ExecutionContext))
          .expects(predicate, retrievals, *, *)
      }
    }

    lazy val target = new EnrolmentsAuthService(mockAuthConnector)
  }

  "calling .authorised" when {

    "the user is an authorised individual" should {
      "return the 'Individual' user type in the user details" in new Test {

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

        val expected = Left(DownstreamError)

        MockedAuthConnector.authorised(EmptyPredicate, authRetrievals)
          .returns(Future.successful(retrievalsResult))

        private val result = await(target.authorised(EmptyPredicate))

        result shouldBe expected
      }
    }

    "the user is not logged in" should {
      "return an unauthenticated error" in new Test {

        val expected = Left(UnauthorisedError)

        MockedAuthConnector.authorised(EmptyPredicate, authRetrievals)
          .returns(Future.failed(MissingBearerToken()))

        private val result = await(target.authorised(EmptyPredicate))

        result shouldBe expected
      }
    }

    "the user is not authorised" should {
      "return an unauthorised error" in new Test {

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
          val actualArn = target.getAgentReferenceFromEnrolments(Enrolments(
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
