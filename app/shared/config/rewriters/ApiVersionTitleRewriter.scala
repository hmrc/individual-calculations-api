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

package shared.config.rewriters

import shared.config.AppConfig
import shared.config.rewriters.DocumentationRewriters.CheckAndRewrite

import javax.inject.{Inject, Singleton}

@Singleton class ApiVersionTitleRewriter @Inject() (appConfig: AppConfig) {

  private val rewriteTitleRegex = ".*(title: [\"]?)(.*)".r

  val rewriteApiVersionTitle: CheckAndRewrite = CheckAndRewrite(
    check = (version, filename) => {
      filename == "application.yaml" &&
      !appConfig.apiVersionReleasedInProduction(version)
    },
    rewrite = (_, _, yaml) => {
      val maybeLine = rewriteTitleRegex.findFirstIn(yaml)
      maybeLine
        .collect {
          case line if !line.toLowerCase.contains("[test only]") =>
            val title = line
              .split("title: ")(1)
              .replace("\"", "")

            val replacement = s"""  title: "$title [test only]""""
            rewriteTitleRegex.replaceFirstIn(yaml, replacement)
        }
        .getOrElse(yaml)
    }
  )

}
