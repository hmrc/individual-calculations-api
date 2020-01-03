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

package v1.models.response.calculationWrappers

import play.api.libs.json.{JsPath, Reads}
import v1.models.response.common.CalculationType
import v1.models.response.getEoyEstimate.EoyEstimateResponse

sealed trait EoyEstimateWrapperOrError

object EoyEstimateWrapperOrError {

  case class EoyEstimateWrapper(eoyEstimate: EoyEstimateResponse) extends EoyEstimateWrapperOrError
  case object EoyErrorMessages        extends EoyEstimateWrapperOrError
  case object EoyCrystallisedError    extends EoyEstimateWrapperOrError

  // Note: the implicit Reads[A] must correctly locate the embedded calculation object
  // from within the JSON as it is received from the backend microservice.
  implicit def reads[A]: Reads[EoyEstimateWrapperOrError] = {
    for {
      errCount <- (JsPath \ "metadata" \ "calculationErrorCount").readWithDefault[Int](0)
      calcType <- (JsPath \ "metadata" \ "calculationType").readWithDefault[CalculationType](CalculationType.inYear)
      result <- readsErrorChecker(errCount, calcType)
    } yield result
  }

  def readsErrorChecker[A](errCount: Int, calcType: CalculationType)(implicit rds: Reads[A]): Reads[EoyEstimateWrapperOrError] = {
    if (errCount > 0) {
      Reads.pure[EoyEstimateWrapperOrError](EoyErrorMessages)
    }
    else if (calcType == CalculationType.crystallisation) {
      Reads.pure[EoyEstimateWrapperOrError](EoyCrystallisedError)
    }
    else {
      JsPath.read[EoyEstimateResponse].map(EoyEstimateWrapper)
    }
  }
}
