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
import api.models.audit.GenericAuditDetail
import api.services.{AuditService, EnrolmentsAuthService, MtdIdLookupService}
import config.AppConfig
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import routing.{Version, Version3}
import utils.{IdGenerator, Logging}
import v3.controllers.requestParsers.TriggerCalculationParser
import v3.hateoas.HateoasFactory
import v3.models.request.TriggerCalculationRawData
import v3.models.response.triggerCalculation.TriggerCalculationHateoasData
import v3.services.TriggerCalculationService

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class TriggerCalculationController @Inject() (val authService: EnrolmentsAuthService,
                                              val lookupService: MtdIdLookupService,
                                              parser: TriggerCalculationParser,
                                              service: TriggerCalculationService,
                                              val idGenerator: IdGenerator,
                                              hateoasFactory: HateoasFactory,
                                              auditService: AuditService,
                                              cc: ControllerComponents)(implicit ec: ExecutionContext, appConfig: AppConfig)
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
      implicit val apiVersion: Version = Version.from(request, orElse = Version3)
      implicit val ctx: RequestContext = RequestContext.from(idGenerator, endpointLogContext)

      val rawData = TriggerCalculationRawData(nino, taxYear, finalDeclaration)

      val requestHandler =
        RequestHandler
          .withParser(parser)
          .withService(service.triggerCalculation)
          .withHateoasResultFrom(hateoasFactory)(
            { (parsedRequest, response) =>
              TriggerCalculationHateoasData(
                nino = nino,
                taxYear = parsedRequest.taxYear,
                finalDeclaration = parsedRequest.finalDeclaration,
                calculationId = response.calculationId
              )
            },
            successStatus = ACCEPTED
          )
          .withAuditing(AuditHandler.custom(
            auditService,
            auditType = "TriggerASelfAssessmentTaxCalculation",
            transactionName = "trigger-a-self-assessment-tax-calculation",
            auditDetailCreator = GenericAuditDetail.auditDetailCreator(
              params = Map("nino" -> nino, "taxYear" -> taxYear, "finalDeclaration" -> s"${rawData.finalDeclaration.getOrElse(false)}")),
            requestBody = None,
            responseBodyMap = auditResponseBody
          ))

      requestHandler.handleRequest(rawData)
    }

  private def auditResponseBody(maybeBody: Option[JsValue]) =
    for {
      body   <- maybeBody
      calcId <- (body \ "calculationId").asOpt[String]
    } yield Json.obj("calculationId" -> calcId)

}
