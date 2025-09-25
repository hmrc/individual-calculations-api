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

package auth

import play.api.http.Status.OK
import play.api.libs.json.JsValue
import play.api.libs.ws.{WSRequest, WSResponse}
import shared.auth.AuthMainAgentsOnlyISpec
import shared.services.DownstreamStub
import v6.retrieveCalculation.def1.model.Def1_CalculationFixture

class CalculationsAuthMainAgentsOnlyISpec extends AuthMainAgentsOnlyISpec with Def1_CalculationFixture {

  val callingApiVersion = "6.0"

  val supportingAgentsNotAllowedEndpoint = "retrieve-calculation"
  val calculationId: String              = "f2fb30e5-4ab6-4a29-b3c1-c7264259ff1c"
  def taxYear: String                    = "2017-18"

  val mtdUrl = s"/$nino/self-assessment/$taxYear/$calculationId"

  def sendMtdRequest(request: WSRequest): WSResponse = await(request.get())

  val downstreamUri: String = s"/income-tax/view/calculations/liability/$nino/$calculationId"

  val maybeDownstreamResponseJson: Option[JsValue] = Some(calculationDownstreamJson)

  override val downstreamHttpMethod: DownstreamStub.HTTPMethod = DownstreamStub.GET

  override val downstreamSuccessStatus: Int = OK

}
