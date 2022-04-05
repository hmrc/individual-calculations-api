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

package v3.models.response.enums

import play.api.libs.json.Reads
import utils.enums.Enums

sealed trait CalculationType {
  def toMtdString: String
}

object CalculationType {

  case object inYear extends CalculationType {
    override def toMtdString: String = "inYear"
  }
  case object crystallisation extends CalculationType {
    override def toMtdString: String = "endOfYear"
  }
  case object biss extends CalculationType {
    override def toMtdString: String = "biss"
  }
  case object POA extends CalculationType {
    override def toMtdString: String = "POA"
  }

  implicit val reads: Reads[CalculationType] = Enums.reads[CalculationType]
}