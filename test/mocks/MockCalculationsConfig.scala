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

package mocks

import config.CalculationsConfig
import shared.config.{ConfidenceLevelConfig, MockAppConfig}
import org.scalamock.handlers.CallHandler
import org.scalamock.scalatest.MockFactory

trait MockCalculationsConfig extends MockFactory {
  implicit val mockCalculationsConfig: CalculationsConfig = mock[CalculationsConfig]

  object MockCalculationsConfig extends MockAppConfig{
    def confidenceLevelCheckEnabled: CallHandler[ConfidenceLevelConfig] =
      (() => mockAppConfig.confidenceLevelConfig: ConfidenceLevelConfig).expects()
  }

}
