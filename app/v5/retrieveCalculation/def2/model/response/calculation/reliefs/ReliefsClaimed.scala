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

package v5.retrieveCalculation.def2.model.response.calculation.reliefs

import play.api.libs.json.{Json, OFormat}
import v5.retrieveCalculation.def2.model.response.common.ReliefsClaimedType

case class ReliefsClaimed(`type`: ReliefsClaimedType,
                               amountClaimed: Option[BigDecimal],
                               allowableAmount: Option[BigDecimal],
                               amountUsed: Option[BigDecimal],
                               rate: Option[BigDecimal],
                               reliefsClaimedDetail: Option[Seq[ReliefsClaimedDetail]])

object ReliefsClaimed {
  implicit val format: OFormat[ReliefsClaimed] = Json.format[ReliefsClaimed]
}
