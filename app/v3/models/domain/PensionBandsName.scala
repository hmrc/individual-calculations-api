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

package v3.models.domain

import play.api.libs.json
import utils.enums.Enums
import v3.models.response.retrieveCalculation.calculation.pensionSavingsTaxCharges.PensionBandsNameType

sealed trait PensionBandsName {
  def toPensionBandsNameType: PensionBandsNameType
}

object PensionBandsName {

  case object `BRT` extends PensionBandsName {
    override def toPensionBandsNameType: PensionBandsNameType = PensionBandsNameType.`basic-rate`
  }

  case object `IRT` extends PensionBandsName {
    override def toPensionBandsNameType: PensionBandsNameType = PensionBandsNameType.`intermediate-rate`
  }

  case object `HRT` extends PensionBandsName {
    override def toPensionBandsNameType: PensionBandsNameType = PensionBandsNameType.`higher-rate`
  }

  case object `ART` extends PensionBandsName {
    override def toPensionBandsNameType: PensionBandsNameType = PensionBandsNameType.`additional-rate`
  }

  implicit val format: json.Format[PensionBandsName] = Enums.format[PensionBandsName]
}
