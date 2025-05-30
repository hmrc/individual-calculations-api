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

package v6.triggerCalculation

import shared.utils.{IdGenerator, Logging}
import shared.controllers._
import shared.models.audit.GenericAuditDetail
import shared.services.{AuditService, EnrolmentsAuthService, MtdIdLookupService}
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Action, AnyContent, BaseController, ControllerComponents}
import shared.routing.Version
import v6.triggerCalculation.schema.TriggerCalculationSchema

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class TriggerCalculationController @Inject() (val authService: EnrolmentsAuthService,
                                              val lookupService: MtdIdLookupService,
                                              validatorFactory: TriggerCalculationValidatorFactory,
                                              service: TriggerCalculationService,
                                              val idGenerator: IdGenerator,
                                              auditService: AuditService,
                                              cc: ControllerComponents)(implicit ec: ExecutionContext, appConfig: shared.config.AppConfig)
    extends AuthorisedController(cc)
    with BaseController
    with Logging {

  val endpointName = "trigger-calculation"

  implicit val endpointLogContext: EndpointLogContext =
    EndpointLogContext(
      controllerName = "TriggerCalculationController",
      endpointName = "triggerCalculation"
    )

  def triggerCalculation(nino: String, taxYear: String, finalDeclaration: Option[String]): Action[AnyContent] =
    authorisedAction(nino).async { implicit request =>
      implicit val ctx: RequestContext = RequestContext.from(idGenerator, endpointLogContext)

      val validator = validatorFactory.validator(nino, taxYear, finalDeclaration, TriggerCalculationSchema.schemaFor(taxYear))

      val requestHandler =
        RequestHandler
          .withValidator(validator)
          .withService(service.triggerCalculation)
          .withPlainJsonResult(ACCEPTED)
          .withAuditing(AuditHandler.custom(
            auditService,
            auditType = "TriggerASelfAssessmentTaxCalculation",
            transactionName = "trigger-a-self-assessment-tax-calculation",
            auditDetailCreator = GenericAuditDetail.auditDetailCreator(
              apiVersion = Version(request),
              params = Map("nino" -> nino, "taxYear" -> taxYear, "finalDeclaration" -> s"${finalDeclaration.getOrElse(false)}")),
            requestBody = None,
            responseBodyMap = auditResponseBody
          ))

      requestHandler.handleRequest()
    }

  private def auditResponseBody(maybeBody: Option[JsValue]) =
    for {
      body   <- maybeBody
      calcId <- (body \ "calculationId").asOpt[String]
    } yield Json.obj("calculationId" -> calcId)

}
