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

import cats.implicits._
import play.api.libs.json.Json
import play.api.mvc._
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.audit.http.connector.AuditResult
import utils.{IdGenerator, Logging}
import v3.controllers.requestParsers.TriggerCalculationParser
import v3.hateoas.HateoasFactory
import v3.models.audit.{AuditEvent, AuditResponse, GenericAuditDetail}
import v3.models.errors._
import v3.models.request.TriggerCalculationRawData
import v3.models.response.triggerCalculation.TriggerCalculationHateoasData
import v3.services.{AuditService, EnrolmentsAuthService, MtdIdLookupService, TriggerCalculationService}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class TriggerCalculationController @Inject() (val authService: EnrolmentsAuthService,
                                              val lookupService: MtdIdLookupService,
                                              parser: TriggerCalculationParser,
                                              service: TriggerCalculationService,
                                              val idGenerator: IdGenerator,
                                              hateoasFactory: HateoasFactory,
                                              auditService: AuditService,
                                              cc: ControllerComponents)(implicit ec: ExecutionContext)
    extends AuthorisedController(cc)
    with BaseController
    with Logging {

  implicit val endpointLogContext: EndpointLogContext =
    EndpointLogContext(
      controllerName = "TriggerCalculationController",
      endpointName = "triggerCalculation"
    )

  def triggerCalculation(nino: String, taxYear: String, finalDeclaration: Option[String]): Action[AnyContent] =
    authorisedAction(nino).async { implicit request =>
      implicit val correlationId: String = idGenerator.getCorrelationId
      logger.info(message = s"[${endpointLogContext.controllerName}][${endpointLogContext.endpointName}] " +
        s"with correlationId : $correlationId")

      val rawData = TriggerCalculationRawData(nino, taxYear, finalDeclaration)
      val result = {
        for {
          parsedRequest   <- wrap(parser.parseRequest(rawData))
          serviceResponse <- wrap(service.triggerCalculation(parsedRequest))
        } yield {
          val hateoasData = TriggerCalculationHateoasData(nino, taxYear, parsedRequest.finalDeclaration, serviceResponse.responseData.calculationId)
          val vendorResponse = hateoasFactory.wrap(serviceResponse.responseData, hateoasData)

          logger.info(
            s"[${endpointLogContext.controllerName}][${endpointLogContext.endpointName}] - " +
              s"Success response received with CorrelationId: ${serviceResponse.correlationId}")

          auditSubmission(
            GenericAuditDetail(
              userDetails = request.userDetails,
              pathParams = Map("nino" -> nino, "taxYear" -> taxYear, "finalDeclaration" -> parsedRequest.finalDeclaration.toString),
              requestBody = None,
              `X-CorrelationId` = serviceResponse.correlationId,
              auditResponse = AuditResponse(httpStatus = ACCEPTED, response = Right(Some(Json.toJson(vendorResponse))))
            )
          )
          Accepted(Json.toJson(vendorResponse)).withApiHeaders(serviceResponse.correlationId)
        }
      }
      result.leftMap { errorWrapper =>
        val resCorrelationId = errorWrapper.correlationId
        val result           = errorResult(errorWrapper).withApiHeaders(resCorrelationId)
        logger.warn(
          s"[${endpointLogContext.controllerName}][${endpointLogContext.endpointName}] - " +
            s"Error response received with CorrelationId: $resCorrelationId")

        auditSubmission(
          GenericAuditDetail(
            userDetails = request.userDetails,
            pathParams = Map("nino" -> nino, "taxYear" -> taxYear, "finalDeclaration" -> s"${rawData.finalDeclaration.getOrElse(false)}"),
            requestBody = None,
            `X-CorrelationId` = resCorrelationId,
            auditResponse = AuditResponse(httpStatus = result.header.status, response = Left(errorWrapper.auditErrors))
          ))

        result
      }.merge
    }

  private def errorResult(errorWrapper: ErrorWrapper) = {
    errorWrapper.error match {
      case _
          if errorWrapper.containsAnyOf(
            NinoFormatError,
            TaxYearFormatError,
            RuleTaxYearNotSupportedError,
            RuleTaxYearRangeInvalidError,
            FinalDeclarationFormatError,
            BadRequestError,
            RuleIncorrectGovTestScenarioError,
            RuleIncomeSourcesChangedError,
            RuleResidencyChangedError,
            RuleTaxYearNotEndedError,
            RuleRecentSubmissionsExistError,
            RuleCalculationInProgressError,
            RuleBusinessValidationFailureError
          ) =>
        BadRequest(Json.toJson(errorWrapper))
      case RuleNoIncomeSubmissionsExistError | RuleFinalDeclarationReceivedError => Forbidden(Json.toJson(errorWrapper))
      case InternalError                                                         => InternalServerError(Json.toJson(errorWrapper))
      case _                                                                     => unhandledError(errorWrapper)
    }
  }

  private def auditSubmission(details: GenericAuditDetail)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[AuditResult] = {
    val event = AuditEvent(
      auditType = "TriggerASelfAssessmentTaxCalculation",
      transactionName = "trigger-a-self-assessment-tax-calculation",
      detail = details
    )

    auditService.auditEvent(event)
  }

}
