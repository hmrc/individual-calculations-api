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

package v3.models.response.retrieveCalculation.calculation.pensionSavingsTaxCharges

import play.api.libs.json.Format
import utils.enums.Enums
import v3.models.domain.PensionBandsName

sealed trait PensionBandsNameType{
  def toPensionBandsName: PensionBandsName
}

object PensionBandsNameType {

  case object `basic-rate` extends PensionBandsNameType {
    override def toPensionBandsName: PensionBandsName = PensionBandsName.`BRT`
  }

  case object `intermediate-rate` extends PensionBandsNameType {
    override def toPensionBandsName: PensionBandsName = PensionBandsName.`IRT`
  }

  case object `higher-rate` extends PensionBandsNameType {
    override def toPensionBandsName: PensionBandsName = PensionBandsName.`HRT`
  }

  case object `additional-rate` extends PensionBandsNameType {
    override def toPensionBandsName: PensionBandsName = PensionBandsName.`ART`
  }

  implicit val format: Format[PensionBandsNameType] = Enums.format[PensionBandsNameType]
}

