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
import api.services.{EnrolmentsAuthService, MtdIdLookupService}
import config.AppConfig
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import routing.{Version, Version3}
import utils.{IdGenerator, Logging}
import v3.controllers.requestParsers.ListCalculationsParser
import v3.hateoas.HateoasFactory
import v3.models.request.ListCalculationsRawData
import v3.models.response.listCalculations.ListCalculationsHateoasData
import v3.services.ListCalculationsService

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class ListCalculationsController @Inject() (
    val authService: EnrolmentsAuthService,
    val lookupService: MtdIdLookupService,
    parser: ListCalculationsParser,
    service: ListCalculationsService,
    hateoasFactory: HateoasFactory,
    cc: ControllerComponents,
    val idGenerator: IdGenerator)(implicit val ec: ExecutionContext, appConfig: AppConfig)
    extends AuthorisedController(cc)
    with BaseController
    with Logging {

  implicit val endpointLogContext: EndpointLogContext = EndpointLogContext(
    controllerName = "ListCalculationsController",
    endpointName = "list"
  )

  def list(nino: String, taxYear: Option[String]): Action[AnyContent] =
    authorisedAction(nino).async { implicit request =>
      implicit val apiVersion: Version = Version.from(request, orElse = Version3)
      implicit val ctx: RequestContext = RequestContext.from(idGenerator, endpointLogContext)

      val rawData = ListCalculationsRawData(nino, taxYear)

      val requestHandler =
        RequestHandler
          .withParser(parser)
          .withService(service.list)
          .withResultCreator(ResultCreator.hateoasListWrapping(hateoasFactory)((parsedRequest, _) =>
            ListCalculationsHateoasData(nino, parsedRequest.taxYear)))

      requestHandler.handleRequest(rawData)
    }

}
