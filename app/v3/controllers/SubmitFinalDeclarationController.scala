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

package v3.controllers

import api.controllers._
import api.models.errors.InternalError
import api.services.{AuditService, EnrolmentsAuthService, MtdIdLookupService, ServiceOutcome}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import uk.gov.hmrc.http.HeaderCarrier
import utils.{IdGenerator, Logging}
import v3.controllers.requestParsers.SubmitFinalDeclarationParser
import v3.models.request.{RetrieveCalculationRequest, SubmitFinalDeclarationRawData, SubmitFinalDeclarationRequest}
import v3.models.response.retrieveCalculation.RetrieveCalculationResponse
import v3.services._

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class SubmitFinalDeclarationController @Inject() (val authService: EnrolmentsAuthService,
                                                  val lookupService: MtdIdLookupService,
                                                  parser: SubmitFinalDeclarationParser,
                                                  service: SubmitFinalDeclarationService,
                                                  retrieveService: RetrieveCalculationService,
                                                  cc: ControllerComponents,
                                                  nrsProxyService: NrsProxyService,
                                                  auditService: AuditService,
                                                  idGenerator: IdGenerator)(implicit ec: ExecutionContext)
    extends AuthorisedController(cc)
    with Logging {

  implicit val endpointLogContext: EndpointLogContext =
    EndpointLogContext(controllerName = "SubmitFinalDeclarationController", endpointName = "submitFinalDeclaration")

  private val maxNrsAttempts = 3
  private val interval      = 100

  def submitFinalDeclaration(nino: String, taxYear: String, calculationId: String): Action[AnyContent] =
    authorisedAction(nino).async { implicit request =>
      implicit val ctx: RequestContext = RequestContext.from(idGenerator, endpointLogContext)

      val rawData = SubmitFinalDeclarationRawData(nino = nino, taxYear = taxYear, calculationId = calculationId)

      val requestHandler =
        RequestHandler
          .withParser(parser)
          .withService { parsedRequest =>
            updateNrs(nino, parsedRequest)
            service.submitFinalDeclaration(parsedRequest)
          }
          .withNoContentResult()
          .withAuditing(AuditHandler(
            auditService,
            auditType = "SubmitAFinalDeclaration",
            transactionName = "submit-a-final-declaration",
            params = Map("nino" -> nino, "taxYear" -> taxYear, "calculationId" -> calculationId)
          ))

      requestHandler.handleRequest(rawData)
    }

  private def updateNrs(nino: String, parsedRequest: SubmitFinalDeclarationRequest)(implicit
      ctx: RequestContext,
      ec: ExecutionContext): Future[Unit] = {
    implicit val hc: HeaderCarrier = ctx.hc

    retrieveCalculationDetails(parsedRequest) map {
      case Left(_) =>
        nrsProxyService.submit(nino, "itsa-crystallisation", parsedRequest.toNrsJson)

      case Right(responseWrapper) =>
        nrsProxyService.submit(nino, "itsa-crystallisation", Json.toJson(responseWrapper.responseData))
    }
  }

  private def retrieveCalculationDetails(parsedRequest: SubmitFinalDeclarationRequest, attempt: Int = 1)(implicit
      ctx: RequestContext,
      ec: ExecutionContext): Future[ServiceOutcome[RetrieveCalculationResponse]] = {
    import parsedRequest._

    val retrieveRequest = RetrieveCalculationRequest(nino, taxYear, calculationId)
    retrieveService.retrieveCalculation(retrieveRequest).flatMap {
      case Right(result) =>
        Future.successful(Right(result))

      case Left(error) =>
        if (attempt < maxNrsAttempts) {
          Thread.sleep(interval)
          retrieveCalculationDetails(parsedRequest, attempt + 1)
        } else {
          logger.warn(s"Error fetching Calculation details for NRS logging: ${error.correlationId}")
          Future.successful(Left(error.copy(error = InternalError, errors = None)))
        }
    }
  }

}
