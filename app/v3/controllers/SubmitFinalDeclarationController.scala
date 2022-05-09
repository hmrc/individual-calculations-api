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

import cats.data.EitherT
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import utils.{IdGenerator, Logging}
import v3.controllers.requestParsers.SubmitFinalDeclarationParser
import v3.models.errors._
import v3.models.request.SubmitFinalDeclarationRawData
import v3.services._
import javax.inject.{Inject, Singleton}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.audit.http.connector.AuditResult
import v3.models.audit.{AuditEvent, AuditResponse, GenericAuditDetail}

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class SubmitFinalDeclarationController @Inject() (val authService: EnrolmentsAuthService,
                                                  val lookupService: MtdIdLookupService,
                                                  parser: SubmitFinalDeclarationParser,
                                                  service: SubmitFinalDeclarationService,
                                                  cc: ControllerComponents,
                                                  auditService: AuditService,
                                                  idGenerator: IdGenerator)(implicit ec: ExecutionContext)
    extends AuthorisedController(cc)
    with BaseController
    with Logging {

  implicit val endpointLogContext: EndpointLogContext =
    EndpointLogContext(controllerName = "SubmitFinalDeclarationController", endpointName = "submitFinalDeclaration")

  def submitFinalDeclaration(nino: String, taxYear: String, calculationId: String): Action[AnyContent] =
    authorisedAction(nino).async { implicit request =>
      implicit val correlationId: String = idGenerator.getCorrelationId
      logger.info(
        message = s"[${endpointLogContext.controllerName}][${endpointLogContext.endpointName}] " +
          s"with correlationId : $correlationId")
      val rawData = SubmitFinalDeclarationRawData(nino, taxYear, calculationId)
      val result =
        for {
          parsedRequest   <- EitherT.fromEither[Future](parser.parseRequest(rawData))
          serviceResponse <- EitherT(service.submitFinalDeclaration(parsedRequest))
        } yield {
          logger.info(
            s"[${endpointLogContext.controllerName}][${endpointLogContext.endpointName}] - " +
              s"Success response received with CorrelationId: ${serviceResponse.correlationId}")

          auditSubmission(
            GenericAuditDetail(
              userDetails = request.userDetails,
              pathParams = Map("nino" -> nino, "taxYear" -> taxYear, "calculationId" -> calculationId),
              requestBody = None,
              `X-CorrelationId` = serviceResponse.correlationId,
              auditResponse = AuditResponse(httpStatus = NO_CONTENT, response = Right(None))
            )
          )

          NoContent.withApiHeaders(serviceResponse.correlationId)
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
            pathParams = Map("nino" -> nino, "taxYear" -> taxYear, "calculationId" -> calculationId),
            requestBody = None,
            `X-CorrelationId` = resCorrelationId,
            auditResponse = AuditResponse(httpStatus = result.header.status, response = Left(errorWrapper.auditErrors))
          )
        )

        result
      }.merge
    }

  private def errorResult(errorWrapper: ErrorWrapper) =
    errorWrapper.error match {
      case BadRequestError | NinoFormatError | TaxYearFormatError | RuleTaxYearRangeInvalidError | RuleTaxYearNotSupportedError |
          CalculationIdFormatError | RuleIncorrectGovTestScenarioError =>
        BadRequest(Json.toJson(errorWrapper))
      case RuleIncomeSourcesChangedError | RuleRecentSubmissionsExistError | RuleResidencyChangedError | RuleFinalDeclarationReceivedError |
          RuleIncomeSourcesInvalidError | RuleNoIncomeSubmissionsExistError | RuleSubmissionFailedError =>
        Forbidden(Json.toJson(errorWrapper))
      case NotFoundError   => NotFound(Json.toJson(errorWrapper))
      case DownstreamError => InternalServerError(Json.toJson(errorWrapper))
      case _               => unhandledError(errorWrapper)
    }

  private def auditSubmission(details: GenericAuditDetail)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[AuditResult] = {
    val event = AuditEvent(
      auditType = "SubmitAFinalDeclaration",
      transactionName = "Submit-A-Final-Declaration",
      detail = details
    )

    auditService.auditEvent(event)
  }

}
