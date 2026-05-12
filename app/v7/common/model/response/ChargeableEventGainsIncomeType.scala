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

package v7.common.model.response

import common.utils.enums.Enums
import play.api.libs.json.{Reads, Writes}

sealed trait ChargeableEventGainsIncomeType

object ChargeableEventGainsIncomeType {
  case object `life-insurance`     extends ChargeableEventGainsIncomeType
  case object `capital-redemption` extends ChargeableEventGainsIncomeType
  case object `life-annuity`       extends ChargeableEventGainsIncomeType
  case object `voided-isa`         extends ChargeableEventGainsIncomeType

  implicit val writes: Writes[ChargeableEventGainsIncomeType] = Enums.writes[ChargeableEventGainsIncomeType]

  implicit val reads: Reads[ChargeableEventGainsIncomeType] = Enums.readsUsing {
    case "lifeInsurance"     => `life-insurance`
    case "capitalRedemption" => `capital-redemption`
    case "lifeAnnuity"       => `life-annuity`
    case "voidedIsa"         => `voided-isa`
  }

}
