/*
 * Copyright 2025 HM Revenue & Customs
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

package v5.submitFinalDeclaration

import api.nrs.NrsProxyConnector
import common.utils.{Delayer, Retrying}
import config.CalculationsConfig
import org.apache.pekko.actor.Scheduler
import play.api.libs.json.Json
import shared.controllers.RequestContext
import shared.services.ServiceOutcome
import shared.utils.Logging
import uk.gov.hmrc.http.HeaderCarrier
import v5.retrieveCalculation.RetrieveCalculationService
import v5.retrieveCalculation.models.request.RetrieveCalculationRequestData
import v5.retrieveCalculation.models.response.RetrieveCalculationResponse
import v5.submitFinalDeclaration.model.request.SubmitFinalDeclarationRequestData
import shared.models.errors.InternalError

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Success, Try}

class SubmitFinalDeclarationNrsService @Inject() (connector: NrsProxyConnector,
                                                  retrieveService: RetrieveCalculationService,
                                                  config: CalculationsConfig)(implicit val scheduler: Scheduler, val ec: ExecutionContext)
    extends Retrying
    with Delayer
    with Logging {

  private val maxNrsAttempts = 3
  private val interval       = 100

  private def updateNrs(nino: String, submitRequest: SubmitFinalDeclarationRequestData)(implicit
      ctx: RequestContext,
      ec: ExecutionContext): Future[Unit] = {
    implicit val hc: HeaderCarrier = ctx.hc

    retrieveCalculationDetails(submitRequest.toRetrieveRequestData) map {
      case Left(_)                => connector.submitAsync(nino, "itsa-crystallisation", submitRequest.toNrsJson)
      case Right(responseWrapper) => connector.submitAsync(nino, "itsa-crystallisation", Json.toJson(responseWrapper.responseData))
    }
  }

  private def retrieveCalculationDetails(retrieveRequest: RetrieveCalculationRequestData, attempt: Int = 1)(implicit
      ctx: RequestContext,
      ec: ExecutionContext): Future[ServiceOutcome[RetrieveCalculationResponse]] = {
    retrieveService.retrieveCalculation(retrieveRequest).flatMap {
      case Right(result) =>
        Future.successful(Right(result))

      case Left(error) =>
        if (attempt <= maxNrsAttempts) {
          Thread.sleep(interval)
          retrieveCalculationDetails(retrieveRequest, attempt + 1)
        } else {
          logger.warn(s"Error fetching Calculation details for NRS logging. Correlation ID: ${error.correlationId}")
          Future.successful(Left(error.copy(error = InternalError, errors = None)))
        }
    }
  }

  private def retrieveCalculationDetails2(retrieveRequest: RetrieveCalculationRequestData, attempt: Int = 1)(implicit
      ctx: RequestContext,
      ec: ExecutionContext): Future[ServiceOutcome[RetrieveCalculationResponse]] = {

    val retryCondition: Try[ServiceOutcome[RetrieveCalculationResponse]] => Boolean = {
      case Success(Left(error)) => error.error.httpStatus == 500
      case _                    => false
    }

    retry(config.retrieveCalcRetries, retryCondition) { attemptNumber =>
      logger.info(s"Attempt $attemptNumber calculation retrieval")

      retrieveService.retrieveCalculation(retrieveRequest).flatMap {
        case Right(result) => Future.successful(Right(result))
        case Left(error) =>
          logger.warn(s"Error fetching Calculation details for NRS logging. Correlation ID: ${error.correlationId}")
          Future.successful(Left(error.copy(error = InternalError, errors = None)))
      }
    }
  }

}
