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
import scala.annotation.tailrec
import scala.concurrent.{ExecutionContext, Future}

// TODO create a v4 SubmitFinalDeclaration endpoint with controller, service, connector etc & update v4.routes
//  because it should really be calling the v4 Retrieve service & connector (which already exists in v4)
//  Then improve & finish the code in v4, and apply your improvements/changes back to v3
//  So that:
//    the v4 code calls only v4 service, connector etc
//    the v3 code calls only v3 service, connector etc

// TODO add tests - update SubmitFinalDeclarationControllerSpec (v3 & the new v4)

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

  private def updateNrs(nino: String, parsedRequest: SubmitFinalDeclarationRequest)(implicit ctx: RequestContext, ec: ExecutionContext): Unit = {

    implicit val hc: HeaderCarrier = ctx.hc

    // TODO if the details lookup fails, just send the calculationId (as before)

    // TODO maybe don't need the for-comp here, look at just mapping the result

    retrieveCalculationDetails(parsedRequest).map {
      case Left(_) =>
        // details lookup failed, so as a fallback, just send the calculation ID
        nrsProxyService.submit(nino, "itsa-crystallisation", parsedRequest.toNrsJson)

      case Right(responseWrapper) =>
        val details: RetrieveCalculationResponse = responseWrapper.responseData
        nrsProxyService.submit(nino, "itsa-crystallisation", Json.toJson(details))
    }

  }

  @tailrec
  private def retrieveCalculationDetails(parsedRequest: SubmitFinalDeclarationRequest, retry: Int = 0)(implicit
      ctx: RequestContext,
      ec: ExecutionContext): Future[ServiceOutcome[RetrieveCalculationResponse]] = {
    import parsedRequest._

    // TODO probably improve this code:

    val retrieveRequest = RetrieveCalculationRequest(nino, taxYear, calculationId)
    retrieveService.retrieveCalculation(retrieveRequest).flatMap {
      case Left(error) =>
        logger.warn(s"Error fetching Calculation details for NRS logging: ${error.correlationId}")
        if (retry >= 3) {
          error.copy(error = InternalError, errors = None)
          Future.successful(Left(error))
        } else {
          Thread.sleep(100)
          retrieveCalculationDetails(parsedRequest, retry = retry + 1)
        }

      case Right(result) =>
        Future.successful(Right(result))
    }
  }

}
