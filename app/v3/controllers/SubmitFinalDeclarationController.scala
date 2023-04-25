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

import api.controllers.{AuditHandler, AuthorisedController, EndpointLogContext, RequestContext, RequestHandler}
import api.services.{EnrolmentsAuthService, MtdIdLookupService}
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import utils.{IdGenerator, Logging}
import v3.controllers.requestParsers.SubmitFinalDeclarationParser
import v3.models.request.SubmitFinalDeclarationRawData
import v3.services._

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

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
      implicit val ctx: RequestContext = RequestContext.from(idGenerator, endpointLogContext)

      val rawData = SubmitFinalDeclarationRawData(nino = nino, taxYear = taxYear, calculationId = calculationId)

      val requestHandler =
        RequestHandler
          .withParser(parser)
          .withService(service.submitFinalDeclaration)
          .withNoContentResult()
          .withAuditing(AuditHandler(
            auditService,
            auditType = "SubmitAFinalDeclaration",
            transactionName = "submit-a-final-declaration",
            params = Map("nino" -> nino, "taxYear" -> taxYear, "calculationId" -> calculationId)
          ))

      requestHandler.handleRequest(rawData)
    }

}
