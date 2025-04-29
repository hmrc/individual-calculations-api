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

import play.api.mvc.{Action, AnyContent, ControllerComponents}
import shared.controllers._
import shared.routing.Version
import shared.services._
import shared.utils.{IdGenerator, Logging}

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

  val endpointName = "submit-final-declaration"

  implicit val endpointLogContext: EndpointLogContext =
    EndpointLogContext(controllerName = "SubmitFinalDeclarationController", endpointName = "submitFinalDeclaration")

  def submitFinalDeclaration(nino: String, taxYear: String, calculationId: String, calculationType: String): Action[AnyContent] =
    authorisedAction(nino).async { implicit request =>
      implicit val ctx: RequestContext = RequestContext.from(idGenerator, endpointLogContext)

      val validator =
        validatorFactory.validator(nino, taxYear, calculationId, calculationType, SubmitFinalDeclarationSchema.schemaFor(taxYear))

      val requestHandler =
        RequestHandler
          .withValidator(validator)
          .withService { parsedRequest =>
            service.submitFinalDeclaration(nino, parsedRequest)
          }
          .withNoContentResult()
          .withAuditing(AuditHandler(
            auditService,
            auditType = "SubmitAFinalDeclaration",
            transactionName = "submit-a-final-declaration",
            apiVersion = Version(request),
            params = Map("nino" -> nino, "taxYear" -> taxYear, "calculationId" -> calculationId, "calculationType" -> calculationType)
          ))

      requestHandler.handleRequest()
    }

}
