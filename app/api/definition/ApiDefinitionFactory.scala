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

package api.definition

import api.config.AppConfig
import api.definition.APIAccessType.{CONTROLLED, PUBLIC}
import api.definition.APIStatus.RETIRED
import api.routing.{Version, Version7, Version8, Version9}
import api.utils.Logging
import cats.data.Validated.Invalid

import javax.inject.{Inject, Singleton}

@Singleton
class ApiDefinitionFactory @Inject() (appConfig: AppConfig) extends Logging {

  lazy val definition: Definition =
    Definition(
      api = APIDefinition(
        name = "Individual Calculations (MTD)",
        description = "An API for providing individual calculations data",
        context = appConfig.apiGatewayContext,
        categories = List("INCOME_TAX_MTD"),
        versions = List(
          APIVersion(
            version = Version7,
            status = RETIRED,
            access = if (appConfig.controlledAccessEnabled) CONTROLLED else PUBLIC,
            endpointsEnabled = false
          ),
          APIVersion(
            version = Version8,
            status = buildAPIStatus(Version8),
            access = if (appConfig.controlledAccessEnabled) CONTROLLED else PUBLIC,
            endpointsEnabled = appConfig.endpointsEnabled(Version8)
          ),
          APIVersion(
            version = Version9,
            status = buildAPIStatus(Version9),
            access = if (appConfig.controlledAccessEnabled) CONTROLLED else PUBLIC,
            endpointsEnabled = appConfig.endpointsEnabled(Version9)
          )
        ),
        requiresTrust = None
      )
    )

  protected[definition] def buildAPIStatus(version: Version): APIStatus = {
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
