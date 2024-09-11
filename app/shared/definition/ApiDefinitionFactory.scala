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

import cats.data.Validated.Invalid
import shared.config.AppConfig
import shared.routing.Version
import shared.utils.Logging

trait ApiDefinitionFactory extends Logging {

  protected val mtdCategory = "INCOME_TAX_MTD"

  protected val appConfig: AppConfig

  val definition: Definition

  protected def buildAPIStatus(version: Version): APIStatus = {
    checkDeprecationConfigFor(version)

    APIStatus.parser
      .lift(appConfig.apiStatus(version))
      .getOrElse {
        logger.error("[ApiDefinition][buildApiStatus] no API Status found in config. Reverting to Alpha")
        APIStatus.ALPHA
      }
  }

  private def checkDeprecationConfigFor(version: Version): Unit =
    appConfig.deprecationFor(version) match {
      case Invalid(error) => throw new Exception(error)
      case _              => ()
    }

}
