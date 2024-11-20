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

package v7.retrieveCalculation.downstreamErrorMapping

import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks
import shared.models.domain.{TaxYear, TaxYearPropertyCheckSupport}
import shared.utils.UnitSpec
import v7.retrieveCalculation.downstreamErrorMapping.RetrieveCalculationDownstreamErrorMapping._

class RetrieveCalculationDownstreamErrorMappingSpec extends UnitSpec with ScalaCheckDrivenPropertyChecks with TaxYearPropertyCheckSupport {

  "RetrieveCalculationDownstreamErrorMapping" should {
    "return correct downstream error mapping for Non TYS tax years" in {
      forPreTysTaxYears { taxYear =>
        val downstreamErrorMapping: RetrieveCalculationDownstreamErrorMapping = errorMapFor(taxYear)

        downstreamErrorMapping shouldBe Api1523
        downstreamErrorMapping.errorMap.keys should contain.allOf(
          "INVALID_TAXABLE_ENTITY_ID",
          "INVALID_CALCULATION_ID",
          "INVALID_CORRELATIONID",
          "INVALID_CONSUMERID",
          "NO_DATA_FOUND",
          "SERVER_ERROR",
          "SERVICE_UNAVAILABLE"
        )
      }
    }

    "return correct downstream error mapping for TYS tax years" in {
      forTaxYearsFrom(TaxYear.fromMtd("2023-24")) { taxYear =>
        val downstreamErrorMapping: RetrieveCalculationDownstreamErrorMapping = errorMapFor(taxYear)

        downstreamErrorMapping shouldBe Api1885
        downstreamErrorMapping.errorMap.keys should contain.allOf(
          "INVALID_TAXABLE_ENTITY_ID",
          "INVALID_CALCULATION_ID",
          "INVALID_TAX_YEAR",
          "INVALID_CORRELATION_ID",
          "INVALID_CONSUMER_ID",
          "NOT_FOUND",
          "TAX_YEAR_NOT_SUPPORTED",
          "SERVER_ERROR",
          "SERVICE_UNAVAILABLE"
        )
      }
    }
  }
}
