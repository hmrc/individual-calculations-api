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

import com.github.jknack.handlebars.Options
import shared.config.AppConfig
import shared.config.rewriters.DocumentationRewriters.CheckAndRewrite

import javax.inject.{Inject, Singleton}

/** For the OAS workaround where the "grouped endpoints" yaml file (e.g. employment_expenses.yaml) must include the matching summary text for each
  * endpoint. This rewriter uses handlebars and config to add [test only] if necessary.
  */
@Singleton class EndpointSummaryGroupRewriter @Inject() (val appConfig: AppConfig) extends HandlebarsRewriter {

  hb.registerHelper(
    "maybeTestOnly",
    (endpointDetails: String, _: Options) => {
      val parts = endpointDetails.split(' ')
      val (version, name) = parts match {
        case Array(v, n) => (v, n)
        case _ =>
          throw new IllegalArgumentException(
            s"Invalid endpoint details format: '$endpointDetails'. The endpoint details should consist of two space-separated parts: version and name.")
      }

      if (appConfig.endpointReleasedInProduction(version, name)) "" else " [test only]"
    }
  )

  val rewriteGroupedEndpointSummaries: CheckAndRewrite = CheckAndRewrite(
    check = (_, filename) => {
      filename.endsWith(".yaml") && filename != "application.yaml"
    },
    rewrite = (_, _, yaml) => {
      if (yaml.contains("#maybeTestOnly")) rewrite(yaml, Nil) else yaml
    }
  )

}
