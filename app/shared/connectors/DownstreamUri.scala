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

package shared.connectors

import shared.config.{AppConfig, DownstreamConfig}

case class DownstreamUri[+Resp](
    path: String,
    strategy: DownstreamStrategy
)

object DownstreamUri {

  private def withStandardStrategy[Resp](path: String, config: DownstreamConfig) =
    DownstreamUri(path, DownstreamStrategy.standardStrategy(config))

  def DesUri[Resp](value: String)(implicit appConfig: AppConfig): DownstreamUri[Resp] =
    withStandardStrategy(value, appConfig.desDownstreamConfig)

  def IfsUri[Resp](value: String)(implicit appConfig: AppConfig): DownstreamUri[Resp] =
    withStandardStrategy(value, appConfig.ifsDownstreamConfig)

  def TaxYearSpecificIfsUri[Resp](value: String)(implicit appConfig: AppConfig): DownstreamUri[Resp] =
    withStandardStrategy(value, appConfig.tysIfsDownstreamConfig)

  def HipUri[Resp](path: String)(implicit appConfig: AppConfig): DownstreamUri[Resp] =
    DownstreamUri(path, DownstreamStrategy.basicAuthStrategy(appConfig.hipDownstreamConfig))

  def DesToHipMigrationUri[Resp](path: String, switchName: String)(implicit appConfig: AppConfig): DownstreamUri[Resp] = {
    lazy val desStrategy = DownstreamStrategy.standardStrategy(appConfig.desDownstreamConfig)
    lazy val hipStategy  = DownstreamStrategy.basicAuthStrategy(appConfig.hipDownstreamConfig)

    DownstreamUri(path, DownstreamStrategy.switchedStrategy(onStrategy = hipStategy, offStrategy = desStrategy, switchName))
  }

}
