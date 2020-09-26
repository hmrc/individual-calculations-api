/*
 * Copyright 2020 HM Revenue & Customs
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

package v1.models.outcomes

import cats.implicits._
import v1.models.errors.{ErrorWrapper, MtdErrors}

case class ResponseWrapper[+A](correlationId: String, responseData: A) {

  def map[B](f: A => B): ResponseWrapper[B] = ResponseWrapper(correlationId, f(responseData))

  def mapToEither[B](f: A => Either[MtdErrors, B]): Either[ErrorWrapper, ResponseWrapper[B]] = {
    println(scala.Console.YELLOW + responseData + scala.Console.RESET)
    f(responseData) match {
      case Right(b) => ResponseWrapper(correlationId, b).asRight
      case Left(errs) => ErrorWrapper(Some(correlationId), errs).asLeft
    }
  }

  def toErrorWhen(f: PartialFunction[A, MtdErrors]): Either[ErrorWrapper, ResponseWrapper[A]] =
    f.lift(responseData) match {
      case Some(error) => ErrorWrapper(Some(correlationId), error).asLeft
      case None => this.asRight
    }
}
