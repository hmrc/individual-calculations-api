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

package v2.controllers

import javax.inject.Inject
import play.api.mvc.{Action, AnyContent, ControllerComponents, Request}
import utils.IdGenerator
import v2.connectors.httpparsers.StandardHttpParser
import v2.connectors.httpparsers.StandardHttpParser.SuccessCode
import v2.controllers.requestParsers.GetCalculationParser
import v2.handler.{AuditHandler, RequestDefn, RequestHandler}
import v2.hateoas.HateoasFactory
import v2.models.audit.GenericAuditDetail
import v2.models.errors._
import v2.models.hateoas.HateoasWrapper
import v2.models.request.{GetCalculationRawData, GetCalculationRequest}
import v2.models.response.calculationWrappers.EoyEstimateWrapperOrError
import v2.models.response.getEoyEstimate.{EoyEstimateHateoasData, EoyEstimateResponse}
import v2.services.{AuditService, EnrolmentsAuthService, MtdIdLookupService, StandardService}

import scala.concurrent.ExecutionContext

class GetEoyEstimateController @Inject()(authService: EnrolmentsAuthService,
                                         lookupService: MtdIdLookupService,
                                         parser: GetCalculationParser,
                                         service: StandardService,
                                         hateoasFactory: HateoasFactory,
                                         auditService: AuditService,
                                         cc: ControllerComponents,
                                         idGenerator: IdGenerator,
                                        )(implicit ec: ExecutionContext)
  extends StandardController[GetCalculationRawData,
    GetCalculationRequest,
    EoyEstimateWrapperOrError,
    HateoasWrapper[EoyEstimateResponse],
    AnyContent](authService, lookupService, parser, service, auditService, cc, idGenerator) {
  controller =>

  override implicit val endpointLogContext: EndpointLogContext =
    EndpointLogContext(
      controllerName = "GetEoyEstimateController",
      endpointName = "getEoyEstimate"
    )

  override val successCode: StandardHttpParser.SuccessCode = SuccessCode(OK)

  override def requestHandlerFor(playRequest: Request[AnyContent],
                                 req: GetCalculationRequest): RequestHandler[EoyEstimateWrapperOrError, HateoasWrapper[EoyEstimateResponse]] = {
    RequestHandler[EoyEstimateWrapperOrError](RequestDefn.Get(req.backendCalculationUri))
      .withPassThroughErrors(
        NinoFormatError,
        CalculationIdFormatError,
        NotFoundError
      )
      .mapSuccess { responseWrapper =>
        responseWrapper.mapToEither {
          case EoyEstimateWrapperOrError.EoyErrorMessages => Left(ErrorWrapper(responseWrapper.correlationId, RuleCalculationErrorMessagesExist, None, FORBIDDEN))
          case EoyEstimateWrapperOrError.EoyCrystallisedError => Left(ErrorWrapper(responseWrapper.correlationId, EndOfYearEstimateNotPresentError, None, NOT_FOUND))
          case EoyEstimateWrapperOrError.EoyEstimateWrapper(calc) => Right(calc)
        }
      }
      .mapSuccessSimple(rawResponse => hateoasFactory.wrap(rawResponse, EoyEstimateHateoasData(req.nino.nino, rawResponse.id)))
  }

  def getEoyEstimate(nino: String, calculationId: String): Action[AnyContent] =
    authorisedAction(nino).async { implicit request =>
      val rawData = GetCalculationRawData(nino, calculationId)

      val auditHandler: AuditHandler[GenericAuditDetail] = AuditHandler.withoutBody(
        "retrieveSelfAssessmentTaxCalculationEndOfYearEstimate",
        "retrieve-self-assessment-tax-calculation-end-of-year-estimate",
        Map("nino" -> nino, "calculationId" -> calculationId), request
      )

      doHandleRequest(rawData, Some(auditHandler))
    }
}