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

package v2.models.response.common

import cats.Show
import play.api.libs.json._
import utils.enums.Enums

sealed trait LossType

object LossType {

  case object INCOME     extends LossType
  case object CLASS4NICS extends LossType

  implicit val show: Show[LossType]     = Show.show(_.toString.toLowerCase)
  implicit val format: Format[LossType] = Enums.format[LossType]
}
