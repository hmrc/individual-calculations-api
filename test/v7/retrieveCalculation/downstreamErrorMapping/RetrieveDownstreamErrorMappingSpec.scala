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
import v7.retrieveCalculation.downstreamErrorMapping.RetrieveDownstreamErrorMapping._

class RetrieveDownstreamErrorMappingSpec extends UnitSpec with ScalaCheckDrivenPropertyChecks with TaxYearPropertyCheckSupport {

  "RetrieveDownstreamErrorMapping" should {
    "return correct downstream error mapping for pre Tax Year Specific (TYS) tax years" in {
      forPreTysTaxYears { taxYear =>
        val downstreamErrorMapping: RetrieveDownstreamErrorMapping = errorMapFor(taxYear)

        downstreamErrorMapping shouldBe PreTys
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

    "return correct downstream error mapping for tax year 2023-24" in {
      val downstreamErrorMapping: RetrieveDownstreamErrorMapping = errorMapFor(TaxYear.fromMtd("2023-24"))

      downstreamErrorMapping shouldBe TaxYear2023
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

    "return correct downstream error mapping for tax year 2024-25" in {
      val downstreamErrorMapping: RetrieveDownstreamErrorMapping = errorMapFor(TaxYear.fromMtd("2024-25"))

      downstreamErrorMapping shouldBe TaxYear2024
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

    "return correct downstream error mapping for tax years 2025-26 onwards" in {
      forTaxYearsFrom(TaxYear.fromMtd("2025-26")) { taxYear =>
        val downstreamErrorMapping: RetrieveDownstreamErrorMapping = errorMapFor(taxYear)

        downstreamErrorMapping shouldBe TaxYear2025
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
