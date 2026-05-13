/*
 * Copyright 2026 HM Revenue & Customs
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

package routing

import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.routing.Router
import shared.routing.*
import shared.utils.UnitSpec

class VersionRoutingMapSpec extends UnitSpec with GuiceOneAppPerSuite {

  val defaultRouter: Router = mock[Router]
  val v7Routes: v7.Routes   = app.injector.instanceOf[v7.Routes]
  val v8Routes: v8.Routes   = app.injector.instanceOf[v8.Routes]

  "map" when {
    "routing to v8" should {

      val versionRoutingMap: CalculationsVersionRoutingMap = CalculationsVersionRoutingMap(
        defaultRouter = defaultRouter,
        v7Router = v7Routes,
        v8Router = v8Routes
      )

      s"route to ${v7Routes.toString}" in {
        versionRoutingMap.map(Version7) shouldBe v7Routes
      }

      s"route to ${v8Routes.toString}" in {
        versionRoutingMap.map(Version8) shouldBe v8Routes
      }
    }

  }

}
