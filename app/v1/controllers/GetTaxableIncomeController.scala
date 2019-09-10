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
import play.api.mvc.{Action, AnyContent, ControllerComponents, Request}
import v1.connectors.httpparsers.StandardHttpParser
import v1.connectors.httpparsers.StandardHttpParser.SuccessCode
import v1.controllers.requestParsers.GetCalculationParser
import v1.handling.RequestDefinition
import v1.models.errors._
import v1.models.request.{GetCalculationRawData, GetCalculationRequest}
import v1.models.response.CalculationWrapperOrError
import v1.models.response.getTaxableIncome.TaxableIncome
import v1.services.{EnrolmentsAuthService, MtdIdLookupService, StandardService}

import scala.concurrent.ExecutionContext

class GetTaxableIncomeController @Inject()(
                                            authService: EnrolmentsAuthService,
                                            lookupService: MtdIdLookupService,
                                            parser: GetCalculationParser,
                                            service: StandardService,
                                            cc: ControllerComponents
                                          )(implicit ec: ExecutionContext)
  extends StandardController[GetCalculationRawData,
    GetCalculationRequest,
    CalculationWrapperOrError[TaxableIncome],
    TaxableIncome,
    AnyContent](authService, lookupService, parser, service, cc) {
  controller =>

  implicit val endpointLogContext: EndpointLogContext =
    EndpointLogContext(controllerName = "GetTaxableIncomeController", endpointName = "getTaxableIncome")

  override def requestHandlingFor(playRequest: Request[AnyContent],
                                  req: GetCalculationRequest): RequestDefinition[CalculationWrapperOrError[TaxableIncome], TaxableIncome] =
    RequestDefinition.Get[CalculationWrapperOrError[TaxableIncome], TaxableIncome](
      uri = req.backendCalculationUri,
      passThroughErrors = Seq(
        NinoFormatError,
        CalculationIdFormatError,
        NotFoundError),
      successHandler = responseWrapper =>
        responseWrapper.mapToEither[TaxableIncome] {
          case CalculationWrapperOrError.ErrorsInCalculation => Left(MtdErrors(FORBIDDEN, RuleCalculationErrorMessagesExist))
          case CalculationWrapperOrError.CalculationWrapper(calc) => Right(calc)
        }
    )

  override val successCode: StandardHttpParser.SuccessCode = SuccessCode(OK)

  def getTaxableIncome(nino: String, calculationId: String): Action[AnyContent] =
    authorisedAction(nino).async { implicit request =>
      val rawData = GetCalculationRawData(nino, calculationId)
      doHandleRequest(rawData)
    }
}
