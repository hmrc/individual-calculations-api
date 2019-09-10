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

package v1.services

import javax.inject.Inject
import play.api.libs.json.Reads
import uk.gov.hmrc.http.HeaderCarrier
import utils.Logging
import v1.connectors.StandardConnector
import v1.connectors.httpparsers.StandardHttpParser.SuccessCode
import v1.controllers.EndpointLogContext
import v1.handling.RequestDefinition
import v1.models.errors._
import v1.models.outcomes.ResponseWrapper
import v1.support.BackendResponseMappingSupport

import scala.concurrent.{ExecutionContext, Future}

class StandardService @Inject()(connector: StandardConnector) extends BackendResponseMappingSupport with Logging {

  def doService[Req, Resp](requestDefinition: RequestDefinition[Resp, _])(implicit logContext: EndpointLogContext,
                                                                          reads: Reads[Resp],
                                                                          ec: ExecutionContext,
                                                                          hc: HeaderCarrier): Future[Either[ErrorWrapper, ResponseWrapper[Resp]]] = {
    implicit val successCode: SuccessCode = SuccessCode(requestDefinition.expectedSuccessCode)
    connector.doRequest(requestDefinition).map(directMap(requestDefinition.passThroughErrors, requestDefinition.customErrorHandler))
  }
}
