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

sealed trait PensionBandsName

object PensionBandsName {

  case object `basic-rate` extends PensionBandsName
  case object `intermediate-rate` extends PensionBandsName
  case object `higher-rate` extends PensionBandsName
  case object `additional-rate` extends PensionBandsName

  implicit val formats: Format[PensionBandsName] = Enums.format[PensionBandsName]
}

