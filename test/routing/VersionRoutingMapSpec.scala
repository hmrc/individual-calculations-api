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

package routing

import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.routing.Router
import support.UnitSpec

class VersionRoutingMapSpec extends UnitSpec with GuiceOneAppPerSuite {

  val defaultRouter: Router = mock[Router]
  val v4Routes: v4.Routes   = app.injector.instanceOf[v4.Routes]
  val v5Routes: v5.Routes   = app.injector.instanceOf[v5.Routes]

  "map" when {
    "routing to v4 or v5" should {

      s"route to ${v4Routes.toString}" in {

        val versionRoutingMap: VersionRoutingMapImpl = VersionRoutingMapImpl(
          defaultRouter = defaultRouter,
          v4Router = v4Routes,
          v5Router = v5Routes
        )

        versionRoutingMap.map(Version4) shouldBe v4Routes
      }

      s"route to ${v5Routes.toString}" in {

        val versionRoutingMap: VersionRoutingMapImpl = VersionRoutingMapImpl(
          defaultRouter = defaultRouter,
          v4Router = v4Routes,
          v5Router = v5Routes
        )

        versionRoutingMap.map(Version5) shouldBe v5Routes
      }
    }
  }

}
