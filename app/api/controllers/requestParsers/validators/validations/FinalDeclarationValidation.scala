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

package api.controllers.requestParsers.validators.validations

import api.models.errors.{FinalDeclarationFormatError, MtdError}

import scala.util.{Failure, Success, Try}

object FinalDeclarationValidation {

  def validateOptional(finalDeclaration: Option[String]): List[MtdError] = {
    finalDeclaration match {
      case None        => NoValidationErrors
      case Some(value) => validate(value)
    }
  }

  private def validate(finalDeclaration: String): List[MtdError] = {
    Try {
      finalDeclaration.toBoolean
    } match {
      case Success(_) => List()
      case Failure(_) => List(FinalDeclarationFormatError)
    }
  }

}
