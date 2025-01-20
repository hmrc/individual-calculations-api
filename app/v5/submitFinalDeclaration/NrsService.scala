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
import shared.models.errors.{ErrorWrapper, InternalError}
import shared.services.{BaseService, ServiceOutcome}
import v5.retrieveCalculation.RetrieveCalculationService
import v5.retrieveCalculation.models.request.RetrieveCalculationRequestData
import v5.retrieveCalculation.models.response.RetrieveCalculationResponse
import v5.submitFinalDeclaration.model.request.SubmitFinalDeclarationRequestData

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Success, Try}

@Singleton
class NrsService @Inject()(val connector: NrsProxyConnector,
                           val retrieveService: RetrieveCalculationService,
                           val config: CalculationsConfig)(implicit val scheduler: Scheduler, val ec: ExecutionContext)
    extends BaseService
    with Retrying
    with Delayer {

//  def updateNrs(nino: String, submitRequest: SubmitFinalDeclarationRequestData)(implicit ctx: RequestContext, ec: ExecutionContext): Future[Unit] = {
//
//    retrieveCalculationDetails(submitRequest.toRetrieveRequestData) map { repsonse =>
//      val json = repsonse match {
//        case Left(_)                => submitRequest.toNrsJson
//        case Right(responseWrapper) => Json.toJson(responseWrapper.responseData)
//      }
//
//      println(s"\n******\nJSON:\n\n$json\n\n******")
//
//      connector.submitAsync(nino, "itsa-crystallisation", json)
//    }
//  }

  def updateNrs(nino: String, submitRequest: SubmitFinalDeclarationRequestData)(implicit ctx: RequestContext, ec: ExecutionContext): Future[Unit] = {

    retrieveCalculationDetails(submitRequest.toRetrieveRequestData) map {
      case Left(_)                => connector.submitAsync(nino, "itsa-crystallisation", submitRequest.toNrsJson)
      case Right(responseWrapper) => connector.submitAsync(nino, "itsa-crystallisation", Json.toJson(responseWrapper.responseData))
    }
  }

  private def retrieveCalculationDetails(retrieveRequest: RetrieveCalculationRequestData)(implicit
      ctx: RequestContext,
      ec: ExecutionContext): Future[ServiceOutcome[RetrieveCalculationResponse]] = {

    val retryCondition: Try[ServiceOutcome[RetrieveCalculationResponse]] => Boolean = {
      case Success(Left(_)) => true
      case _                => false
    }

    retry(config.retrieveCalcRetries, retryCondition) { attemptNumber =>
      logger.info(s"Attempt $attemptNumber calculation retrieval for NRS logging")
      retrieveService.retrieveCalculation(retrieveRequest)
    }.recover { case _: Throwable =>
      logger.warn(s"Error fetching Calculation details for NRS logging. Correlation ID: ${ctx.correlationId}")
      Left(ErrorWrapper(ctx.correlationId, InternalError, None))
    }
  }

}
