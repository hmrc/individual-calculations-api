/*
 * Copyright 2022 HM Revenue & Customs
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

import play.api.mvc._
import utils.IdGenerator
import v3.connectors.httpparsers.StandardHttpParser.SuccessCode
import v3.controllers.requestParsers.RetrieveCalculationParser
import v3.handler.{RequestDefn, RequestHandler}
import v3.hateoas.HateoasFactory
import v3.models.errors._
import v3.models.hateoas.HateoasWrapper
import v3.models.request.{RetrieveCalculationRawData, RetrieveCalculationRequest}
import v3.models.response.retrieveCalculation.{RetrieveCalculationHateoasData, RetrieveCalculationResponse}
import v3.services.{AuditService, EnrolmentsAuthService, MtdIdLookupService, StandardService}

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class RetrieveCalculationController @Inject() (authService: EnrolmentsAuthService,
                                               lookupService: MtdIdLookupService,
                                               retrieveCalculationParser: RetrieveCalculationParser,
                                               service: StandardService,
                                               hateoasFactory: HateoasFactory,
                                               auditService: AuditService,
                                               cc: ControllerComponents,
                                               idGenerator: IdGenerator)(implicit val ec: ExecutionContext)
    extends StandardController[
      RetrieveCalculationRawData,
      RetrieveCalculationRequest,
      RetrieveCalculationResponse,
      HateoasWrapper[RetrieveCalculationResponse],
      AnyContent](authService, lookupService, retrieveCalculationParser, service, auditService, cc, idGenerator) {
  controller =>

  implicit val endpointLogContext: EndpointLogContext =
    EndpointLogContext(
      controllerName = "RetrieveCalculationController",
      endpointName = "retrieveCalculation"
    )

  override val successCode: SuccessCode = SuccessCode(OK)

  override def requestHandlerFor(
      playRequest: Request[AnyContent],
      req: RetrieveCalculationRequest): RequestHandler[RetrieveCalculationResponse, HateoasWrapper[RetrieveCalculationResponse]] = {
    RequestHandler[RetrieveCalculationResponse](RequestDefn.Get(req.backendCalculationUri))
      .mapErrors {
        case "INVALID_TAXABLE_ENTITY_ID" => (BAD_REQUEST, NinoFormatError)
        case "INVALID_CALCULATION_ID"    => (BAD_REQUEST, CalculationIdFormatError)
        case "INVALID_CORRELATIONID"     => (INTERNAL_SERVER_ERROR, DownstreamError)
        case "INVALID_CONSUMERID"        => (INTERNAL_SERVER_ERROR, DownstreamError)
        case "NO_DATA_FOUND"             => (NOT_FOUND, NotFoundError)
        case "SERVER_ERROR"              => (INTERNAL_SERVER_ERROR, DownstreamError)
        case "SERVICE_UNAVAILABLE"       => (INTERNAL_SERVER_ERROR, DownstreamError)
      }
      .withRequestSuccessCode(OK)
      .mapSuccessSimple(rawResponse =>
        hateoasFactory.wrap(rawResponse, RetrieveCalculationHateoasData(req.nino.nino, req.taxYear, req.calculationId)))

  }

  def retrieveCalculation(nino: String, taxYear: String, calculationId: String): Action[AnyContent] =
    authorisedAction(nino).async { implicit request =>
      val rawData = RetrieveCalculationRawData(nino, taxYear, calculationId)

      doHandleRequest(rawData)
    }

}
