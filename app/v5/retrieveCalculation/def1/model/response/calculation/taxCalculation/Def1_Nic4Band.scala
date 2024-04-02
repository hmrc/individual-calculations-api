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

package v5.retrieveCalculation.def1.model.response.calculation.taxCalculation

import play.api.libs.json.{Format, Json}

case class Def1_Nic4Band(
                          name: Def1_Nic4BandName,
                          rate: BigDecimal,
                          threshold: Option[BigInt],
                          apportionedThreshold: Option[BigInt],
                          income: BigInt,
                          amount: BigDecimal
)

object Def1_Nic4Band {
  implicit val format: Format[Def1_Nic4Band] = Json.format
}
