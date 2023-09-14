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

package v5.models.response.retrieveCalculation.inputs

import play.api.libs.json.{Json, OFormat}

case class IncomeSources(businessIncomeSources: Option[Seq[BusinessIncomeSource]], nonBusinessIncomeSources: Option[Seq[NonBusinessIncomeSource]]) {

  val isDefined: Boolean =
    !(businessIncomeSources.isEmpty && nonBusinessIncomeSources.isEmpty)

  def withoutCessationDate: IncomeSources =
    businessIncomeSources match {
      case Some(det) =>
        val details = (for (d <- det) yield d.withoutCessationDate).filter(_.isDefined)
        if (details.nonEmpty) { copy(businessIncomeSources = Some(details)) }
        else { copy(businessIncomeSources = None) }
      case None => copy(businessIncomeSources = None)
    }

  def withoutCommencementDate: IncomeSources = businessIncomeSources match {
    case Some(det) =>
      val details = (for (d <- det) yield d.withoutCommencementDate).filter(_.isDefined)
      if (details.nonEmpty) {
        copy(businessIncomeSources = Some(details))
      } else {
        copy(businessIncomeSources = None)
      }
    case None => copy(businessIncomeSources = None)
  }

}

object IncomeSources {
  implicit val format: OFormat[IncomeSources] = Json.format[IncomeSources]
}
