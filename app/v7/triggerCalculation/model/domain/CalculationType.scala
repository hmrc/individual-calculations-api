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

package v7.triggerCalculation.model.domain

sealed trait CalculationType {
  def toDownstream: String
}

case object `in-year` extends CalculationType {
  def toDownstream: String = "IY"
}

case object `intent-to-finalise` extends CalculationType {
  def toDownstream: String = "IF"
}

case object `intent-to-amend` extends CalculationType {
  def toDownstream: String = "IA"
}