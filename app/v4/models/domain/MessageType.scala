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

package v4.models.domain

sealed trait MessageType

object MessageType {

  case object info    extends MessageType
  case object warning extends MessageType
  case object error   extends MessageType
  case object none    extends MessageType

  def toTypeClass(`type`: String): MessageType = `type`.toLowerCase() match {
    case "info"    => info
    case "warning" => warning
    case "error"   => error
    case _         => none
  }

}
