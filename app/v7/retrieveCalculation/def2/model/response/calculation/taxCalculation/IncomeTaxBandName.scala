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

package v7.retrieveCalculation.def2.model.response.calculation.taxCalculation

import common.utils.enums.Enums
import play.api.libs.json.{Reads, Writes}

sealed trait IncomeTaxBandName

object IncomeTaxBandName {
  case object `savings-starter-rate` extends IncomeTaxBandName

  case object `allowance-awarded-at-basic-rate` extends IncomeTaxBandName

  case object `allowance-awarded-at-higher-rate` extends IncomeTaxBandName

  case object `allowance-awarded-at-additional-rate` extends IncomeTaxBandName

  case object `starter-rate` extends IncomeTaxBandName

  case object `basic-rate` extends IncomeTaxBandName

  case object `intermediate-rate` extends IncomeTaxBandName

  case object `higher-rate` extends IncomeTaxBandName

  case object `additional-rate` extends IncomeTaxBandName

  case object `advanced-rate` extends IncomeTaxBandName

  implicit val writes: Writes[IncomeTaxBandName] = Enums.writes[IncomeTaxBandName]

  implicit val reads: Reads[IncomeTaxBandName] = Enums.readsUsing {
    case "SSR"   => `savings-starter-rate`
    case "ZRTBR" => `allowance-awarded-at-basic-rate`
    case "ZRTHR" => `allowance-awarded-at-higher-rate`
    case "ZRTAR" => `allowance-awarded-at-additional-rate`
    case "SRT"   => `starter-rate`
    case "BRT"   => `basic-rate`
    case "IRT"   => `intermediate-rate`
    case "HRT"   => `higher-rate`
    case "ART"   => `additional-rate`
    case "AVRT"  => `advanced-rate`
  }

}
