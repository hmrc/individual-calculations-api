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

package shared.definition

import play.api.libs.json.{Json, OFormat}

case class PublishingException(message: String) extends Exception(message)

case class Access(`type`: String, whitelistedApplicationIds: Seq[String])

object Access {
  implicit val formatAccess: OFormat[Access] = Json.format[Access]
}

case class Parameter(name: String, required: Boolean = false)

object Parameter {
  implicit val formatParameter: OFormat[Parameter] = Json.format[Parameter]
}

case class Definition(api: APIDefinition)

object Definition {
  implicit val formatDefinition: OFormat[Definition] = Json.format[Definition]
}
