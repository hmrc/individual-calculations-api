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

import cats.Functor
import play.api.libs.json._

sealed trait ListCalculationsResponse[+I] {
  def mapItems[B](f: I => B): ListCalculationsResponse[B]
}

object ListCalculationsResponse {
  
  implicit def writes[I: Writes]: OWrites[ListCalculationsResponse[I]] = {
    case def1: Def1_ListCalculationsResponse[_] =>
      Json.toJsObject(def1.asInstanceOf[Def1_ListCalculationsResponse[I]])
    case def2: Def2_ListCalculationsResponse[_] =>
      Json.toJsObject(def2.asInstanceOf[Def2_ListCalculationsResponse[I]])
    case def3: Def3_ListCalculationsResponse[_] =>
      Json.toJsObject(def3.asInstanceOf[Def3_ListCalculationsResponse[I]])
  }

  implicit object ResponseFunctor extends Functor[ListCalculationsResponse] {

    override def map[A, B](fa: ListCalculationsResponse[A])(f: A => B): ListCalculationsResponse[B] =
      fa.mapItems(f)

  }

}

case class Def1_ListCalculationsResponse[I](calculations: Seq[I]) extends ListCalculationsResponse[I] {

  override def mapItems[B](f: I => B): ListCalculationsResponse[B] =
    Def1_ListCalculationsResponse(calculations.map(f))

}

object Def1_ListCalculationsResponse {

  implicit def writes[I: Writes]: OWrites[Def1_ListCalculationsResponse[I]] = Json.writes[Def1_ListCalculationsResponse[I]]
  implicit def reads[I: Reads]: Reads[Def1_ListCalculationsResponse[I]]     = JsPath.read[Seq[I]].map(Def1_ListCalculationsResponse(_))

}

case class Def2_ListCalculationsResponse[I](calculations: Seq[I]) extends ListCalculationsResponse[I] {

  override def mapItems[B](f: I => B): ListCalculationsResponse[B] =
    Def2_ListCalculationsResponse(calculations.map(f))

}

object Def2_ListCalculationsResponse {

  implicit def writes[I: Writes]: OWrites[Def2_ListCalculationsResponse[I]] = Json.writes[Def2_ListCalculationsResponse[I]]

  implicit def reads[I: Reads]: Reads[Def2_ListCalculationsResponse[I]] =
    (JsPath \ "calculationsSummary").read[Seq[I]].map(Def2_ListCalculationsResponse(_))

}

case class Def3_ListCalculationsResponse[I](calculations: Seq[I]) extends ListCalculationsResponse[I] {

  override def mapItems[B](f: I => B): ListCalculationsResponse[B] =
    Def3_ListCalculationsResponse(calculations.map(f))

}

object Def3_ListCalculationsResponse {

  implicit def writes[I: Writes]: OWrites[Def3_ListCalculationsResponse[I]] = Json.writes[Def3_ListCalculationsResponse[I]]

  implicit def reads[I: Reads]: Reads[Def3_ListCalculationsResponse[I]] =
    (JsPath \ "calculationsSummary").read[Seq[I]].map(Def3_ListCalculationsResponse(_))

}
