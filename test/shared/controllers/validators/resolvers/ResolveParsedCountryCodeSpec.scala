/*
 * Copyright 2023 HM Revenue & Customs
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

package shared.controllers.validators.resolvers

import cats.data.Validated.{Invalid, Valid}
import shared.models.errors.{CountryCodeFormatError, RuleCountryCodeError}
import shared.utils.UnitSpec

class ResolveParsedCountryCodeSpec extends UnitSpec {

  "ResolveParsedCountryCode" must {
      // @formatter:off
    List("AFG", "ALB", "DZA", "ASM", "AND", "AGO", "AIA", "ATG", "ARG", "ARM", "ABW", "AUS", "AUT", "AZE", "BHS",
        "BHR", "BGD", "BRB", "BLR", "BEL", "BLZ", "BEN", "BMU", "BTN", "BOL", "BES", "BIH", "BWA", "BRA", "VGB",
        "BRN", "BGR", "BFA", "MMR", "BDI", "KHM", "CMR", "CAN", "CPV", "CYM", "CAF", "TCD", "CHL", "CHN", "CXR",
        "CCK", "COL", "COM", "COG", "COK", "CRI", "CIV", "HRV", "CUB", "CUW", "CYP", "CZE", "COD", "DNK", "DJI",
        "DMA", "DOM", "ECU", "EGY", "SLV", "GNQ", "ERI", "EST", "ETH", "FLK", "FRO", "FJI", "FIN", "FRA", "GUF",
        "PYF", "GAB", "GMB", "GEO", "DEU", "GHA", "GIB", "GRC", "GRL", "GRD", "GLP", "GUM", "GTM", "GGY", "GIN",
        "GNB", "GUY", "HTI", "HND", "HKG", "HUN", "ISL", "IND", "IDN", "IRN", "IRQ", "IRL", "IMN", "ISR", "ITA",
        "JAM", "JPN", "JEY", "JOR", "KAZ", "KEN", "KIR", "XKX", "KWT", "KGZ", "LAO", "LVA", "LBN", "LSO", "LBR",
        "LBY", "LIE", "LTU", "LUX", "MAC", "MKD", "MDG", "MWI", "MYS", "MDV", "MLI", "MLT", "MHL", "MTQ", "MRT",
        "MUS", "MYT", "MEX", "FSM", "MDA", "MCO", "MNG", "MNE", "MSR", "MAR", "MOZ", "NAM", "NRU", "NPL", "NLD",
        "NCL", "NZL", "NIC", "NER", "NGA", "NIU", "NFK", "PRK", "MNP", "NOR", "OMN", "PAK", "PLW", "PAN", "PNG",
        "PRY", "PER", "PHL", "PCN", "POL", "PRT", "PRI", "QAT", "REU", "ROU", "RUS", "RWA", "SHN", "KNA", "LCA",
        "SPM", "VCT", "WSM", "SMR", "STP", "SAU", "SEN", "SRB", "SYC", "SLE", "SGP", "SXM", "SVK", "SVN",
        "SLB", "SOM", "ZAF", "KOR", "SSD", "ESP", "LKA", "SDN", "SUR", "SJM", "SWZ", "SWE", "CHE", "SYR", "TWN",
        "TJK", "TZA", "THA", "TLS", "TGO", "TKL", "TON", "TTO", "TUN", "TUR", "TKM", "TCA", "TUV", "UGA", "UKR",
        "ARE", "USA", "VIR", "URY", "UZB", "VUT", "VAT", "VEN", "VNM", "WLF", "YEM", "ZMB", "ZWE", "ZZZ")
        .foreach {
          code =>
            s"return an empty list for valid country code $code" in {
              val result = ResolveParsedCountryCode(code, path = "path")
              result shouldBe Valid(code)
            }
        }
      // @formatter:on

    "return valid for an empty optional country code" in {
      val result = ResolveParsedCountryCode(None, path = "path")
      result shouldBe Valid(None)
    }

    "return valid for a valid optional country code" in {
      val result = ResolveParsedCountryCode(Some("VEN"), path = "path")
      result shouldBe Valid(Some("VEN"))
    }

    "return a CountryCodeFormatError for an invalid optional country code" in {
      val result = ResolveParsedCountryCode(Some("notACountryCode"), path = "path")
      result shouldBe Invalid(List(CountryCodeFormatError.withPath("path")))
    }

    "return a CountryCodeFormatError for an invalid country code" in {
      val result = ResolveParsedCountryCode("notACountryCode", path = "path")
      result shouldBe Invalid(List(CountryCodeFormatError.withPath("path")))
    }

    "return a CountryCodeFormatError for an invalid format country code" in {
      val result = ResolveParsedCountryCode("FRANCE", path = "path")
      result shouldBe Invalid(List(CountryCodeFormatError.withPath("path")))
    }

    "return a CountryCodeFormatError for an invalid rule country code" in {
      val result = ResolveParsedCountryCode("FRE", path = "path")
      result shouldBe Invalid(List(RuleCountryCodeError.withPath("path")))
    }
  }

}
