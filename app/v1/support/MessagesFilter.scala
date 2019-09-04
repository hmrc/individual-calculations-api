/*
 * Copyright 2019 HM Revenue & Customs
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

package v1.support

import v1.models.response.getCalculationMessages.CalculationMessages

trait MessagesFilter {
  def filter(calculationMessages: CalculationMessages, typeQueries: Seq[String]): CalculationMessages = {

    def filterMessages(filteredMessages: CalculationMessages, typeQuery: String) : CalculationMessages = {
      typeQuery match {
        case "error" => filteredMessages.copy(errors = calculationMessages.errors)
        case "warning" => filteredMessages.copy(warnings = calculationMessages.warnings)
        case "info" => filteredMessages.copy(info = calculationMessages.info)
        case _ => filteredMessages
      }
    }

    def filterLoop(filteredMessages: CalculationMessages, typeQueries: Seq[String]): CalculationMessages = {
      typeQueries match {
        case Nil => calculationMessages
        case query :: Nil => filterMessages(filteredMessages, query)
        case query :: tail => filterLoop(filterMessages(filteredMessages, query), tail)
      }
    }

    filterLoop(CalculationMessages(None, None, None), typeQueries)
  }
}
