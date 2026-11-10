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

package v9.createAmendProvisionalFiguresInformation

import api.config.{AppConfig, ConfigFeatureSwitches}
import api.controllers.*
import api.routing.Version
import api.services.{AuditService, EnrolmentsAuthService, MtdIdLookupService}
import api.utils.IdGenerator
import play.api.libs.json.JsValue
import play.api.mvc.{Action, ControllerComponents}

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class CreateAmendProvisionalFiguresInformationController @Inject() (val authService: EnrolmentsAuthService,
                                                                    val lookupService: MtdIdLookupService,
                                                                    service: CreateAmendProvisionalFiguresInformationService,
                                                                    validatorFactory: CreateAmendProvisionalFiguresInformationValidatorFactory,
                                                                    auditService: AuditService,
                                                                    cc: ControllerComponents,
                                                                    idGenerator: IdGenerator)(implicit ec: ExecutionContext, appConfig: AppConfig)
    extends AuthorisedController(cc) {

  override val endpointName: String = "create-amend-provisional-figures-information"

  implicit val endpointLogContext: EndpointLogContext =
    EndpointLogContext(controllerName = "CreateAmendProvisionalFiguresInformationController", endpointName = endpointName)

  def createAmendProvisionalFiguresInformation(nino: String, taxYear: String): Action[JsValue] =
    authorisedAction(nino).async(parse.json) { implicit request =>

      implicit val ctx: RequestContext = RequestContext.from(idGenerator, endpointLogContext)

      val validator = validatorFactory.validator(nino, taxYear, request.body, ConfigFeatureSwitches().isTemporalValidationEnabled)

      val requestHandler =
        RequestHandler
          .withValidator(validator)
          .withService(service.createAmendProvisionalFiguresInformation)
          .withAuditing(AuditHandler(
            auditService = auditService,
            auditType = "CreateAmendProvisionalFiguresInformation",
            transactionName = "create-amend-provisional-figures-information",
            apiVersion = Version(request),
            params = Map("nino" -> nino, "taxYear" -> taxYear),
            requestBody = Some(request.body)
          ))

      requestHandler.handleRequest()
    }

}
