/*
 * Copyright 2021 HM Revenue & Customs
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

package v1r2.models.outcomes

import cats.implicits._
import v1r2.models.errors.ErrorWrapper

case class ResponseWrapper[+A](correlationId: String, responseData: A) {

  def map[B](f: A => B): ResponseWrapper[B] = ResponseWrapper(correlationId, f(responseData))

  def mapToEither[B](f: A => Either[ErrorWrapper, B]): Either[ErrorWrapper, ResponseWrapper[B]] =
    f(responseData) match {
      case Right(b) => ResponseWrapper(correlationId, b).asRight
      case Left(errorWrapper) => errorWrapper.asLeft
    }

  def toErrorWhen(f: PartialFunction[A, ErrorWrapper]): Either[ErrorWrapper, ResponseWrapper[A]] =
    f.lift(responseData) match {
      case Some(errorWrapper) => errorWrapper.asLeft
      case None => this.asRight
    }
}