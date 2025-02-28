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

package v8.listCalculations.model.response

import play.api.libs.json.{Json, OWrites}
import v8.listCalculations.def1.model.response.Def1_Calculation
import v8.listCalculations.def2.model.response.Def2_Calculation
import v8.listCalculations.def3.model.response.Def3_Calculation

trait Calculation {

  def calculationId: String

}

object Calculation {

  implicit val writes: OWrites[Calculation] = OWrites.apply[Calculation] {
    case a: Def1_Calculation => Json.toJsObject(a)
    case b: Def2_Calculation => Json.toJsObject(b)
    case c: Def3_Calculation => Json.toJsObject(c)
    case _ => Json.obj()
  }

}
