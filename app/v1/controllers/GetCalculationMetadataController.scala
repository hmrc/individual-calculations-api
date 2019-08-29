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
import v1.controllers.requestParsers.GetCalculationMetadataParser
import v1.handling.{RequestDefn, RequestHandling}
import v1.models.errors.{CalculationIdFormatError, NinoFormatError, NotFoundError}
import v1.models.request.{GetCalculationMetadataRawData, GetCalculationMetadataRequest}
import v1.models.response.getCalculationMetadata.CalculationMetadata
import v1.services.{EnrolmentsAuthService, MtdIdLookupService, StandardService}

import scala.concurrent.ExecutionContext

class GetCalculationMetadataController @Inject()(
                                                  authService: EnrolmentsAuthService,
                                                  lookupService: MtdIdLookupService,
                                                  parser: GetCalculationMetadataParser,
                                                  service: StandardService,
                                                  cc: ControllerComponents
)(implicit ec: ExecutionContext)
    extends StandardController[GetCalculationMetadataRawData, GetCalculationMetadataRequest, CalculationMetadata, CalculationMetadata, AnyContent](
      authService,
      lookupService,
      parser,
      service,
      cc) {
  controller =>

  implicit val endpointLogContext: EndpointLogContext =
    EndpointLogContext(controllerName = "GetCalculationMetadataController", endpointName = "getMetadata")

  override def requestHandlingFor(playRequest: Request[AnyContent],
                                  req: GetCalculationMetadataRequest): RequestHandling[CalculationMetadata,CalculationMetadata] =
    RequestHandling[CalculationMetadata](
      RequestDefn.Get(playRequest.path))
      .withPassThroughErrors(
        NinoFormatError,
        CalculationIdFormatError,
        NotFoundError
      )

  override val successCode: StandardHttpParser.SuccessCode = SuccessCode(OK)

  def getMetadata(nino: String, calculationId: String): Action[AnyContent] =
    authorisedAction(nino).async { implicit request =>
      val rawData = GetCalculationMetadataRawData(nino, calculationId)

      doHandleRequest(rawData)
    }
}
