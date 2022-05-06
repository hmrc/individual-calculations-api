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

package v3.hateoas

import config.AppConfig
import v3.models.hateoas.Link
import v3.models.hateoas.Method._
import v3.models.hateoas.RelType._

trait HateoasLinks {

  private def baseSaUri(appConfig: AppConfig, nino: String): String =
    s"/${appConfig.apiGatewayContext}/$nino/self-assessment"

  private def baseSaUriWithTaxYear(appConfig: AppConfig, nino: String, taxYear: String): String =
    s"/${appConfig.apiGatewayContext}/$nino/self-assessment/$taxYear"

  // API resource links

  def trigger(appConfig: AppConfig, nino: String, taxYear: String): Link = Link(
    href = baseSaUriWithTaxYear(appConfig, nino, taxYear),
    method = POST,
    rel = TRIGGER
  )

  def list(appConfig: AppConfig, nino: String, isSelf: Boolean): Link = Link(
    href = baseSaUri(appConfig, nino),
    method = GET,
    rel = if (isSelf) SELF else LIST
  )

  def retrieve(appConfig: AppConfig, nino: String, taxYear: String, calculationId: String): Link = Link(
    href = baseSaUriWithTaxYear(appConfig, nino, taxYear) + s"/$calculationId",
    method = GET,
    rel = SELF
  )

  def submitFinalDeclaration(appConfig: AppConfig, nino: String, taxYear: String, calculationId: String): Link = Link(
    href = s"${baseSaUri(appConfig, nino)}/$taxYear/$calculationId/final-declaration",
    method = POST,
    rel = SUBMIT_FINAL_DECLARATION
  )
}
