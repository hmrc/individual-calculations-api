/*
 * Copyright 2020 HM Revenue & Customs
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
import play.api.libs.json.{JsDefined, JsString, JsValue}
import play.api.mvc.{Action, AnyContent, ControllerComponents, Request}
import v1.connectors.httpparsers.StandardHttpParser
import v1.connectors.httpparsers.StandardHttpParser.SuccessCode
import v1.controllers.requestParsers.GetCalculationParser
import v1.handler.{AuditHandler, RequestDefn, RequestHandler}
import v1.hateoas.HateoasFactory
import v1.models.audit.GenericAuditDetail
import v1.models.errors._
import v1.models.hateoas.HateoasWrapper
import v1.models.request.{GetCalculationRawData, GetCalculationRequest}
import v1.models.response.getTaxableIncome.TaxableIncomeHateoasData
import v1.models.response.getTaxableIncome.TaxableIncomeResponse.LinksFactory
import v1.models.response.calculationWrappers.CalculationWrapperOrError
import v1.services.{AuditService, EnrolmentsAuthService, MtdIdLookupService, StandardService}

import scala.concurrent.ExecutionContext

class GetTaxableIncomeController @Inject()(
                                            authService: EnrolmentsAuthService,
                                            lookupService: MtdIdLookupService,
                                            parser: GetCalculationParser,
                                            service: StandardService,
                                            hateoasFactory: HateoasFactory,
                                            auditService: AuditService,
                                            cc: ControllerComponents
                                          )(implicit ec: ExecutionContext)
  extends StandardController[GetCalculationRawData,
    GetCalculationRequest,
    CalculationWrapperOrError[JsValue],
    HateoasWrapper[JsValue],
    AnyContent](authService, lookupService, parser, service, auditService, cc) with GraphQLQuery {
  controller =>

  implicit val endpointLogContext: EndpointLogContext =
    EndpointLogContext(
      controllerName = "GetTaxableIncomeController",
      endpointName = "getTaxableIncome"
    )

  override val successCode: StandardHttpParser.SuccessCode = SuccessCode(OK)

  override def requestHandlerFor(playRequest: Request[AnyContent],
                                 req: GetCalculationRequest): RequestHandler[CalculationWrapperOrError[JsValue], HateoasWrapper[JsValue]] =
    RequestHandler[CalculationWrapperOrError[JsValue]](
      RequestDefn.GraphQl(req.backendCalculationUri, query))
      .withPassThroughErrors(
        NinoFormatError,
        CalculationIdFormatError,
        NotFoundError
      )
      .mapSuccess { responseWrapper =>
        responseWrapper.mapToEither[JsValue] {
          case CalculationWrapperOrError.ErrorsInCalculation => Left(MtdErrors(FORBIDDEN, RuleCalculationErrorMessagesExist))
          case CalculationWrapperOrError.CalculationWrapper(calc) => Right(calc)
        }
      }
      .mapSuccessSimple {
        rawResponse =>
          rawResponse \ "data" match {
            case JsDefined(value) => value \ "metadata" \ "id" match {
              case JsDefined(id: JsString) =>
                hateoasFactory.wrap((value \ "taxableIncome").get, TaxableIncomeHateoasData(req.nino.nino, id.value))
            }
          }
      }

  def getTaxableIncome(nino: String, calculationId: String): Action[AnyContent] =
    authorisedAction(nino).async { implicit request =>
      val rawData = GetCalculationRawData(nino, calculationId)

      val auditHandler: AuditHandler[GenericAuditDetail] = AuditHandler.withoutBody(
        "retrieveSelfAssessmentTaxCalculationTaxableIncome",
        "retrieve-self-assessment-tax-calculation-taxable-income",
        Map("nino" -> nino, "calculationId" -> calculationId), request
      )

      doHandleRequest(rawData, Some(auditHandler))
    }

  override val query: String = TAXABLE_INCOME_QUERY
}
