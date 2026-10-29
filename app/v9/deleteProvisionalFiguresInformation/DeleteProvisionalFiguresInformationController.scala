/*
 * Copyright 2026 HM Revenue & Customs
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

package v9.deleteProvisionalFiguresInformation

import api.config.AppConfig
import api.controllers.*
import api.routing.Version
import api.services.{AuditService, EnrolmentsAuthService, MtdIdLookupService}
import api.utils.IdGenerator
import play.api.mvc.{Action, AnyContent, ControllerComponents}

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class DeleteProvisionalFiguresInformationController @Inject() (val authService: EnrolmentsAuthService,
                                                               val lookupService: MtdIdLookupService,
                                                               service: DeleteProvisionalFiguresInformationService,
                                                               validatorFactory: DeleteProvisionalFiguresInformationValidatorFactory,
                                                               auditService: AuditService,
                                                               cc: ControllerComponents,
                                                               idGenerator: IdGenerator)(implicit ec: ExecutionContext, appConfig: AppConfig)
    extends AuthorisedController(cc) {

  override val endpointName: String = "delete-provisional-figures-information"

  implicit val endpointLogContext: EndpointLogContext =
    EndpointLogContext(controllerName = "DeleteProvisionalFiguresInformationController", endpointName = endpointName)

  def deleteProvisionalFiguresInformation(nino: String, taxYear: String): Action[AnyContent] =
    authorisedAction(nino).async { implicit request =>
      implicit val ctx: RequestContext = RequestContext.from(idGenerator, endpointLogContext)

      val validator = validatorFactory.validator(nino, taxYear)

      val requestHandler =
        RequestHandler
          .withValidator(validator)
          .withService(service.deleteProvisionalFiguresInformation)
          .withAuditing(AuditHandler(
            auditService = auditService,
            auditType = "DeleteProvisionalFiguresInformation",
            transactionName = "delete-provisional-figures-information",
            apiVersion = Version(request),
            params = Map("nino" -> nino, "taxYear" -> taxYear)
          ))

      requestHandler.handleRequest()
    }

}
