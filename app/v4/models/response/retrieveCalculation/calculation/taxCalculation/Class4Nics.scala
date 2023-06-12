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

package v4.models.response.retrieveCalculation.calculation.taxCalculation

import play.api.libs.json.{Format, Json}

case class Class4Nics(
    totalIncomeLiableToClass4Charge: Option[BigInt],
    totalClass4LossesAvailable: Option[BigInt],
    totalClass4LossesUsed: Option[BigInt],
    totalClass4LossesCarriedForward: Option[BigInt],
    totalIncomeChargeableToClass4: Option[BigInt],
    totalAmount: BigDecimal,
    nic4Bands: Seq[Nic4Band]
)

object Class4Nics {
  implicit val format: Format[Class4Nics] = Json.format
}
