/*
 * Copyright 2021 HM Revenue & Customs
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

import cats.data.EitherT
import cats.implicits._
import javax.inject.Inject
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import play.mvc.Http.MimeTypes
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.audit.http.connector.AuditResult
import utils.{IdGenerator, Logging}
import v2.controllers.requestParsers.IntentToCrystalliseRequestParser
import v2.hateoas.HateoasFactory
import v2.models.audit.{AuditEvent, AuditResponse, GenericAuditDetail}
import v2.models.errors._
import v2.models.request.intentToCrystallise.IntentToCrystalliseRawData
import v2.models.response.intentToCrystallise.IntentToCrystalliseHateaosData
import v2.services.{AuditService, EnrolmentsAuthService, IntentToCrystalliseService, MtdIdLookupService}

import scala.concurrent.{ExecutionContext, Future}

class IntentToCrystalliseController @Inject()(val authService: EnrolmentsAuthService,
                                              val lookupService: MtdIdLookupService,
                                              requestParser: IntentToCrystalliseRequestParser,
                                              service: IntentToCrystalliseService,
                                              auditService: AuditService,
                                              hateoasFactory: HateoasFactory,
                                              cc: ControllerComponents,
                                              val idGenerator: IdGenerator)(implicit ec: ExecutionContext)
  extends AuthorisedController(cc) with BaseController with Logging {

  implicit val endpointLogContext: EndpointLogContext =
    EndpointLogContext(
      controllerName = "IntentToCrystalliseController",
      endpointName = "intentToCrystallise"
    )

  def submitIntentToCrystallise(nino: String, taxYear: String): Action[AnyContent] =
    authorisedAction(nino).async { implicit request =>

      implicit val correlationId: String = idGenerator.getCorrelationId
      logger.info(
        s"[${endpointLogContext.controllerName}][${endpointLogContext.endpointName}] " +
          s"with CorrelationId: $correlationId")

      val rawData: IntentToCrystalliseRawData = IntentToCrystalliseRawData(
        nino = nino,
        taxYear = taxYear
      )

      val result =
        for {
          parsedRequest <- EitherT.fromEither[Future](requestParser.parseRequest(rawData))
          serviceResponse <- EitherT(service.submitIntentToCrystallise(parsedRequest))
          hateoasResponse <- EitherT.fromEither[Future](
            hateoasFactory.wrap(
              serviceResponse.responseData,
              IntentToCrystalliseHateaosData(nino, taxYear, serviceResponse.responseData.calculationId)
            ).asRight[ErrorWrapper])
        } yield {
          logger.info(
            s"[${endpointLogContext.controllerName}][${endpointLogContext.endpointName}] - " +
              s"Success response received with CorrelationId: ${serviceResponse.correlationId}"
          )

          auditSubmission(
            GenericAuditDetail(request.userDetails, Map("nino" -> nino, "taxYear" -> taxYear), None,
              serviceResponse.correlationId, AuditResponse(httpStatus = OK, response = Right(Some(Json.toJson(hateoasResponse))))
            )
          )

          Ok(Json.toJson(hateoasResponse))
            .withApiHeaders(serviceResponse.correlationId)
            .as(MimeTypes.JSON)
        }

      result.leftMap { errorWrapper =>
        val resCorrelationId = errorWrapper.correlationId
        val result = errorResult(errorWrapper).withApiHeaders(resCorrelationId)
        logger.warn(
          s"[${endpointLogContext.controllerName}][${endpointLogContext.endpointName}] - " +
            s"Error response received with CorrelationId: $resCorrelationId")

        auditSubmission(
          GenericAuditDetail(request.userDetails, Map("nino" -> nino, "taxYear" -> taxYear), None,
            resCorrelationId, AuditResponse(httpStatus = result.header.status, response = Left(errorWrapper.auditErrors))
          )
        )

        result
      }.merge
    }

  private def errorResult(errorWrapper: ErrorWrapper) = {
    (errorWrapper.error: @unchecked) match {
      case BadRequestError | NinoFormatError | TaxYearFormatError |
           RuleTaxYearRangeInvalidError | RuleTaxYearNotSupportedError
      => BadRequest(Json.toJson(errorWrapper))
      case RuleNoSubmissionsExistError | RuleFinalDeclarationReceivedError => Forbidden(Json.toJson(errorWrapper))
      case DownstreamError => InternalServerError(Json.toJson(errorWrapper))
    }
  }

  private def auditSubmission(details: GenericAuditDetail)
                             (implicit hc: HeaderCarrier,
                              ec: ExecutionContext): Future[AuditResult] = {

    val event: AuditEvent[GenericAuditDetail] = AuditEvent(
      auditType = "TriggerIntentToCrystalliseSelfAssessmentCalculation",
      transactionName = "intent-to-crystallise",
      detail = details
    )

    auditService.auditEvent(event)
  }
}