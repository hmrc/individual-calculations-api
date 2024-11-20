/*
 * Copyright 2024 HM Revenue & Customs
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

package v7.retrieveCalculation.downstreamUriBuilder

import shared.config.AppConfig
import shared.connectors.DownstreamUri
import shared.connectors.DownstreamUri._
import shared.models.domain._
import v7.retrieveCalculation.models.response.RetrieveCalculationResponse

sealed trait RetrieveCalculationDownstreamUriBuilder[Resp] {
  def buildUri(nino: Nino,
               taxYear: TaxYear,
               calculationId: CalculationId)(implicit appConfig: AppConfig): DownstreamUri[Resp]
}

object RetrieveCalculationDownstreamUriBuilder {

  case object Api1523 extends RetrieveCalculationDownstreamUriBuilder[RetrieveCalculationResponse] {
    override def buildUri(nino: Nino,
                          taxYear: TaxYear,
                          calculationId: CalculationId)(implicit appConfig: AppConfig): DownstreamUri[RetrieveCalculationResponse] = {
      IfsUri[RetrieveCalculationResponse](s"income-tax/view/calculations/liability/$nino/$calculationId")
    }
  }

  case object Api1885 extends RetrieveCalculationDownstreamUriBuilder[RetrieveCalculationResponse] {
    override def buildUri(nino: Nino,
                          taxYear: TaxYear,
                          calculationId: CalculationId)(implicit appConfig: AppConfig): DownstreamUri[RetrieveCalculationResponse] = {
      TaxYearSpecificIfsUri[RetrieveCalculationResponse](
        s"income-tax/view/calculations/liability/${taxYear.asTysDownstream}/$nino/$calculationId"
      )
    }
  }

  def downstreamUriFor[Resp](taxYear: TaxYear): RetrieveCalculationDownstreamUriBuilder[Resp] = {
    val downstreamUriBuilder: RetrieveCalculationDownstreamUriBuilder[RetrieveCalculationResponse] =
      if (taxYear.useTaxYearSpecificApi) Api1885 else Api1523

    downstreamUriBuilder.asInstanceOf[RetrieveCalculationDownstreamUriBuilder[Resp]]
  }
}
