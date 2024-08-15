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

import shared.config.AppConfig
import shared.models.auth.UserDetails
import shared.models.errors.{InternalError, _}
import shared.models.outcomes.AuthOutcome
import shared.services.EnrolmentsAuthService.{
  authorisationDisabledPredicate,
  authorisationEnabledPredicate,
  mtdEnrolmentPredicate,
  supportingAgentAuthPredicate
}
import shared.utils.Logging
import uk.gov.hmrc.auth.core.AffinityGroup.{Agent, Individual, Organisation}
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.auth.core.authorise.Predicate
import uk.gov.hmrc.auth.core.retrieve.v2.Retrievals._
import uk.gov.hmrc.auth.core.retrieve.~
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class EnrolmentsAuthService @Inject() (val connector: AuthConnector, val appConfig: AppConfig) extends Logging {

  private lazy val authorisationEnabled = appConfig.confidenceLevelConfig.authValidationEnabled

  private val authFunction: AuthorisedFunctions = new AuthorisedFunctions {
    override def authConnector: AuthConnector = connector
  }

  private def initialPredicate(mtdId: String): Predicate =
    if (authorisationEnabled)
      authorisationEnabledPredicate(mtdId)
    else
      authorisationDisabledPredicate(mtdId)

  def authorised(
      mtdId: String,
      endpointAllowsSupportingAgents: Boolean = false
  )(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[AuthOutcome] = {

    authFunction
      .authorised(initialPredicate(mtdId))
      .retrieve(affinityGroup and authorisedEnrolments) {
        case Some(Individual) ~ _ =>
          Future.successful(Right(UserDetails("", "Individual", None)))

        case Some(Organisation) ~ _ =>
          Future.successful(Right(UserDetails("", "Organisation", None)))

        case Some(Agent) ~ authorisedEnrolments =>
          authFunction
            .authorised(mtdEnrolmentPredicate(mtdId)) {
              Future.successful(agentDetails(authorisedEnrolments))
            }
            .recoverWith { case _: AuthorisationException =>
              if (endpointAllowsSupportingAgents) {
                authFunction
                  .authorised(supportingAgentAuthPredicate(mtdId)) {
                    Future.successful(agentDetails(authorisedEnrolments))
                  }
              } else {
                Future.successful(Left(ClientOrAgentNotAuthorisedError))
              }
                .recoverWith { case _: AuthorisationException =>
                  Future.successful(Left(ClientOrAgentNotAuthorisedError))
                }
            }

        case _ =>
          logger.warn(s"[EnrolmentsAuthService][authorised] Invalid AffinityGroup.")
          Future.successful(Left(ClientOrAgentNotAuthorisedError))
      }
      .recoverWith {
        case _: MissingBearerToken =>
          Future.successful(Left(ClientOrAgentNotAuthorisedError))
        case _: AuthorisationException =>
          Future.successful(Left(ClientOrAgentNotAuthorisedError))
        case error =>
          logger.warn(s"[EnrolmentsAuthService][authorised] An unexpected error occurred: $error")
          Future.successful(Left(InternalError))
      }
  }

  private def agentDetails(authorisedEnrolments: Enrolments): Either[MtdError, UserDetails] =
    (
      for {
        enrolment  <- authorisedEnrolments.getEnrolment("HMRC-AS-AGENT")
        identifier <- enrolment.getIdentifier("AgentReferenceNumber")
        arn = identifier.value
      } yield UserDetails("", "Agent", Some(arn))
    ).toRight(left = {
      logger.warn(s"[EnrolmentsAuthService][authorised] No AgentReferenceNumber defined on agent enrolment.")
      InternalError
    })

}

object EnrolmentsAuthService {

  private[services] def authorisationEnabledPredicate(mtdId: String): Predicate =
    (Individual and ConfidenceLevel.L200 and mtdEnrolmentPredicate(mtdId)) or
      (Organisation and mtdEnrolmentPredicate(mtdId)) or
      (Agent and Enrolment("HMRC-AS-AGENT"))

  private[services] def authorisationDisabledPredicate(mtdId: String): Predicate =
    mtdEnrolmentPredicate(mtdId) or (Agent and Enrolment("HMRC-AS-AGENT"))

  private[services] def mtdEnrolmentPredicate(mtdId: String): Enrolment = {
    Enrolment("HMRC-MTD-IT")
      .withIdentifier("MTDITID", mtdId)
      .withDelegatedAuthRule("mtd-it-auth")
  }

  private[services] def supportingAgentAuthPredicate(mtdId: String): Enrolment = {
    Enrolment("HMRC-MTD-IT-SUPP")
      .withIdentifier("MTDITID", mtdId)
      .withDelegatedAuthRule("mtd-it-auth-supp")
  }

}
