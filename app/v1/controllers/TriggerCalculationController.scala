/*
 * Copyright 2019 HM Revenue & Customs
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

package v1.controllers

import javax.inject.Inject
import play.api.libs.json.JsValue
import play.api.mvc._
import v1.connectors.httpparsers.StandardHttpParser.SuccessCode
import v1.controllers.requestParsers.TriggerCalculationParser
import v1.handling.{RequestDefn, RequestHandling}
import v1.models.errors._
import v1.models.request.{TriggerCalculationRawData, TriggerCalculationRequest}
import v1.models.response.triggerCalculation.TriggerCalculationResponse
import v1.services.{EnrolmentsAuthService, MtdIdLookupService, StandardService}

import scala.concurrent.ExecutionContext

class TriggerCalculationController @Inject()(authService: EnrolmentsAuthService,
                                             lookupService: MtdIdLookupService,
                                             triggerCalculationParser: TriggerCalculationParser,
                                             service: StandardService,
                                             cc: ControllerComponents)(
                                             implicit val ec: ExecutionContext) extends StandardController[TriggerCalculationRawData, TriggerCalculationRequest, TriggerCalculationResponse, TriggerCalculationResponse, JsValue](
  authService,
  lookupService,
  triggerCalculationParser,
  service,
  cc) {
  controller =>

  implicit val endpointLogContext: EndpointLogContext =
    EndpointLogContext(
      controllerName = "TriggerCalculationController",
      endpointName = "triggerCalculation"
    )

  override val successCode: SuccessCode = SuccessCode(ACCEPTED)

  override def requestHandlingFor(playRequest: Request[JsValue],
                                  req: TriggerCalculationRequest): RequestHandling[TriggerCalculationResponse, TriggerCalculationResponse] = {
    RequestHandling[TriggerCalculationResponse](
      RequestDefn.Post(playRequest.path, playRequest.body))
      .withPassThroughErrors(
        NinoFormatError,
        TaxYearFormatError,
        RuleTaxYearNotSupportedError,
        RuleTaxYearRangeExceededError,
        DownstreamError,
        RuleNoIncomeSubmissionsExistError,
        RuleIncorrectOrEmptyBodyError
      )
      .withRequestSuccessCode(ACCEPTED)

  }

  def triggerCalculation(nino: String): Action[JsValue] = authorisedAction(nino).async(parse.json) { implicit request =>
    val rawData = TriggerCalculationRawData(nino, AnyContentAsJson(request.body))

    doHandleRequest(rawData)
  }
}
