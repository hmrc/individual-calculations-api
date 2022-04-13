/*
 * Copyright 2022 HM Revenue & Customs
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

import play.api.mvc._
import utils.Logging
import v3.services.{EnrolmentsAuthService, MtdIdLookupService}

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class RetrieveCalculationController @Inject() (val authService: EnrolmentsAuthService,
                                               val lookupService: MtdIdLookupService,
                                               cc: ControllerComponents)(implicit val ec: ExecutionContext)
    extends AuthorisedController(cc)
    with BaseController
    with Logging {

  implicit val endpointLogContext: EndpointLogContext =
    EndpointLogContext(
      controllerName = "RetrieveCalculationController",
      endpointName = "retrieveCalculation"
    )

  def retrieveCalculation(nino: String, taxYear: String, calculationId: String): Action[AnyContent] =
    authorisedAction(nino) { _ =>
      Ok("TODO")
    }

}
