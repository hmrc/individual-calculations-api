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

package v7.retrieveCalculation.def2.model.response.calculation.dividendsIncome

import common.utils.enums.Enums
import play.api.libs.json.{Reads, Writes}

sealed trait TypeOfDividend

object TypeOfDividend {
  case object `stock-dividend`                  extends TypeOfDividend
  case object `redeemable-shares`               extends TypeOfDividend
  case object `bonus-issues-of-securities`      extends TypeOfDividend
  case object `close-company-loans-written-off` extends TypeOfDividend

  implicit val writes: Writes[TypeOfDividend] = Enums.writes[TypeOfDividend]

  implicit val reads: Reads[TypeOfDividend] = Enums.readsUsing {
    case "stockDividend"               => `stock-dividend`
    case "redeemableShares"            => `redeemable-shares`
    case "bonusIssuesOfSecurities"     => `bonus-issues-of-securities`
    case "closeCompanyLoansWrittenOff" => `close-company-loans-written-off`
  }

}
