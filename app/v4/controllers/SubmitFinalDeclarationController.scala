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

package v4.controllers

import shared.utils.{IdGenerator, Logging}
import shared.controllers._
import shared.services.{AuditService, EnrolmentsAuthService, MtdIdLookupService}
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import shared.routing.Version
import v4.controllers.validators.SubmitFinalDeclarationValidatorFactory
import v4.services._

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class SubmitFinalDeclarationController @Inject() (val authService: EnrolmentsAuthService,
                                                  val lookupService: MtdIdLookupService,
                                                  validatorFactory: SubmitFinalDeclarationValidatorFactory,
                                                  service: SubmitFinalDeclarationService,
                                                  cc: ControllerComponents,
                                                  auditService: AuditService,
                                                  idGenerator: IdGenerator)(implicit ec: ExecutionContext, appConfig: shared.config.AppConfig)
    extends AuthorisedController(cc)
    with Logging {

  implicit val endpointLogContext: EndpointLogContext =
    EndpointLogContext(controllerName = "SubmitFinalDeclarationController", endpointName = "submitFinalDeclaration")

  def submitFinalDeclaration(nino: String, taxYear: String, calculationId: String): Action[AnyContent] =
    authorisedAction(nino).async { implicit request =>
      implicit val ctx: RequestContext = RequestContext.from(idGenerator, endpointLogContext)

      val validator = validatorFactory.validator(nino = nino, taxYear = taxYear, calculationId = calculationId)

      val requestHandler =
        RequestHandler
          .withValidator(validator)
          .withService { parsedRequest =>
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

}
