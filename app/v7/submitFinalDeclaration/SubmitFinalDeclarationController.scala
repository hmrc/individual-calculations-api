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

package v7.submitFinalDeclaration

import api.nrs.NrsProxyService
import play.api.libs.json.Json
import shared.utils.{IdGenerator, Logging}
import shared.controllers._
import shared.services._
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import shared.routing.Version
import shared.models.errors.InternalError
import uk.gov.hmrc.http.HeaderCarrier
import v7.retrieveCalculation.RetrieveCalculationService
import v7.retrieveCalculation.models.request.RetrieveCalculationRequestData
import v7.retrieveCalculation.models.response.RetrieveCalculationResponse
import v7.submitFinalDeclaration.model.request.SubmitFinalDeclarationRequestData

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class SubmitFinalDeclarationController @Inject() (val authService: EnrolmentsAuthService,
                                                  val lookupService: MtdIdLookupService,
                                                  validatorFactory: SubmitFinalDeclarationValidatorFactory,
                                                  service: SubmitFinalDeclarationService,
                                                  nrsProxyService: NrsProxyService,
                                                  retrieveService: RetrieveCalculationService,
                                                  cc: ControllerComponents,
                                                  auditService: AuditService,
                                                  idGenerator: IdGenerator)(implicit ec: ExecutionContext, appConfig: shared.config.AppConfig)
    extends AuthorisedController(cc)
    with Logging {

  val endpointName = "submit-final-declaration"

  implicit val endpointLogContext: EndpointLogContext =
    EndpointLogContext(controllerName = "SubmitFinalDeclarationController", endpointName = "submitFinalDeclaration")


  private val maxNrsAttempts = 3
  private val interval       = 100

  def submitFinalDeclaration(nino: String, taxYear: String, calculationId: String, calculationType: String): Action[AnyContent] =
    authorisedAction(nino).async { implicit request =>
      implicit val ctx: RequestContext = RequestContext.from(idGenerator, endpointLogContext)

      val validator =
        validatorFactory.validator(nino, taxYear, calculationId, calculationType, SubmitFinalDeclarationSchema.schemaFor(taxYear))

      val requestHandler =
        RequestHandler
          .withValidator(validator)
          .withService { parsedRequest =>
            updateNrs(nino, parsedRequest)
            service.submitFinalDeclaration(parsedRequest)
          }
          .withNoContentResult()
          .withAuditing(AuditHandler(
            auditService,
            auditType = "SubmitAFinalDeclaration",
            transactionName = "submit-a-final-declaration",
            apiVersion = Version(request),
            params = Map("nino" -> nino, "taxYear" -> taxYear, "calculationId" -> calculationId)
          ))

      requestHandler.handleRequest()
    }
  private def updateNrs(nino: String, submitRequest: SubmitFinalDeclarationRequestData)(implicit
                                                                                        ctx: RequestContext,
                                                                                        ec: ExecutionContext): Future[Unit] = {
    implicit val hc: HeaderCarrier = ctx.hc

    retrieveCalculationDetails(submitRequest.toRetrieveRequestData) map {
      case Left(_)                => nrsProxyService.submit(nino, "itsa-crystallisation", submitRequest.toNrsJson)
      case Right(responseWrapper) => nrsProxyService.submit(nino, "itsa-crystallisation", Json.toJson(responseWrapper.responseData))
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
}
