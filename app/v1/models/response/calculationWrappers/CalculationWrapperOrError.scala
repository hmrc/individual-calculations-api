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

package v1.models.response.calculationWrappers

import play.api.libs.json.{JsPath, Reads}

/**
  * ADT representing either a wrapper for a successful calculation result, or an indication that there are
  * calculation errors (according to the error count in the metadata portion of the received JSON).
  *
  * @tparam A the type of calculation wrapped when there are no errors
  */
sealed trait CalculationWrapperOrError[+A]

object CalculationWrapperOrError {

  case class CalculationWrapper[A](a: A) extends CalculationWrapperOrError[A]
  case object ErrorsInCalculation        extends CalculationWrapperOrError[Nothing]

  // Note: the implicit Reads[A] must correctly locate the embedded calculation object
  // from within the JSON as it is received from the backend microservice.
  implicit def reads[A: Reads]: Reads[CalculationWrapperOrError[A]] = {

     (JsPath \ "metadata" \ "calculationErrorCount").readWithDefault(0).flatMap { errCount =>
      if (errCount > 0) {
        Reads.pure[CalculationWrapperOrError[A]](ErrorsInCalculation)
      } else {
        implicitly[Reads[A]].map(CalculationWrapper(_))
      }
    }
  }
}
