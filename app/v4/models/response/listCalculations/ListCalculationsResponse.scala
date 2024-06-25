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

package v4.models.response.listCalculations

import cats.Functor
import play.api.libs.json._

case class ListCalculationsResponse[I](calculations: Seq[I])

object ListCalculationsResponse {
  type ListCalculations = ListCalculationsResponse[Calculation]

  implicit def writes[I: Writes]: OWrites[ListCalculationsResponse[I]] = Json.writes[ListCalculationsResponse[I]]
  implicit def reads[I: Reads]: Reads[ListCalculationsResponse[I]]     = JsPath.read[Seq[I]].map(ListCalculationsResponse(_))


  implicit object ResponseFunctor extends Functor[ListCalculationsResponse] {

    override def map[A, B](fa: ListCalculationsResponse[A])(f: A => B): ListCalculationsResponse[B] =
      ListCalculationsResponse(fa.calculations.map(f))

  }

}
