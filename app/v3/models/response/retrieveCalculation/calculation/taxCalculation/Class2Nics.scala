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

package v3.models.response.retrieveCalculation.calculation.taxCalculation

import play.api.libs.json.{Format, Json}

case class Class2Nics(
    amount: Option[BigDecimal],
    weeklyRate: Option[BigDecimal],
    weeks: Option[BigInt],
    limit: Option[BigInt],
    apportionedLimit: Option[BigInt],
    underSmallProfitThreshold: Boolean,
    actualClass2Nic: Option[Boolean]
)

object Class2Nics {
  implicit val format: Format[Class2Nics] = Json.format
}
