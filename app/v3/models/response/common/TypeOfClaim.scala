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

package v3.models.response.common

import play.api.libs.json._
import utils.enums.Enums

sealed trait TypeOfClaim

object TypeOfClaim {

  case object `carry-forward`                                  extends TypeOfClaim
  case object `carry-sideways`                                 extends TypeOfClaim
  case object `carry-forward-to-carry-sideways-general-income` extends TypeOfClaim
  case object `carry-sideways-fhl`                             extends TypeOfClaim
  case object `carry-backwards`                                extends TypeOfClaim
  case object `carry-backwards-general-income`                 extends TypeOfClaim

  implicit val typeOfClaimWrites: Writes[TypeOfClaim] = Enums.writes[TypeOfClaim]

  implicit val reads: Reads[TypeOfClaim] = Enums.readsUsing {
    case "CF"     => `carry-forward`
    case "CSGI"   => `carry-sideways`
    case "CFCSGI" => `carry-forward-to-carry-sideways-general-income`
    case "CSFHL"  => `carry-sideways-fhl`
    case "CB"     => `carry-backwards`
    case "CBGI"   => `carry-backwards-general-income`
  }

  def formatRestricted(types: TypeOfClaim*): Format[TypeOfClaim] = new Format[TypeOfClaim] {
    override def writes(o: TypeOfClaim): JsString            = Json.toJson(o)(typeOfClaimWrites).as[JsString]
    override def reads(json: JsValue): JsResult[TypeOfClaim] = json.validate[TypeOfClaim](Enums.readsRestricted(types: _*))
  }

}
