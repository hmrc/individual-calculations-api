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

package v8.listCalculationsOld

import shared.utils.{IdGenerator, Logging}
import shared.controllers._
import shared.services.{EnrolmentsAuthService, MtdIdLookupService}
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import v8.listCalculationsOld.schema.ListCalculationsSchema

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class ListCalculationsController @Inject() (val authService: EnrolmentsAuthService,
                                            val lookupService: MtdIdLookupService,
                                            validatorFactory: ListCalculationsValidatorFactory,
                                            service: ListCalculationsService,
                                            cc: ControllerComponents,
                                            val idGenerator: IdGenerator)(implicit val ec: ExecutionContext, appConfig: shared.config.AppConfig)
    extends AuthorisedController(cc)
    with Logging {

  val endpointName = "list-calculations"

  implicit val endpointLogContext: EndpointLogContext = EndpointLogContext(
    controllerName = "ListCalculationsController",
    endpointName = "list"
  )

  def list(nino: String, taxYear: Option[String]): Action[AnyContent] =
    authorisedAction(nino).async { implicit request =>
      implicit val ctx: RequestContext = RequestContext.from(idGenerator, endpointLogContext)

      val validator = validatorFactory.validator(nino, taxYear, ListCalculationsSchema.schemaFor(taxYear))

      val requestHandler =
        RequestHandler
          .withValidator(validator)
          .withService(service.list)
          .withPlainJsonResult()

      requestHandler.handleRequest()
    }

}
