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

package shared.routing

import org.apache.pekko.actor.ActorSystem
import org.scalatest.Inside
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.http.HeaderNames.ACCEPT
import play.api.http.{HttpConfiguration, HttpErrorHandler, HttpFilters}
import play.api.mvc._
import play.api.routing.Router
import play.api.test.FakeRequest
import play.api.test.Helpers._
import shared.config.MockAppConfig
import shared.models.errors.{InvalidAcceptHeaderError, UnsupportedVersionError}
import shared.utils.UnitSpec

class VersionRoutingRequestHandlerSpec extends UnitSpec with Inside with MockAppConfig with GuiceOneAppPerSuite {
  test =>

  implicit private val actorSystem: ActorSystem = ActorSystem("test")

  val action: DefaultActionBuilder = app.injector.instanceOf[DefaultActionBuilder]

  import play.api.mvc.Handler
  import play.api.routing.sird._

  object DefaultHandler extends Handler
  object V3Handler      extends Handler
  object V4Handler      extends Handler

  private val defaultRouter = Router.from { case GET(p"") =>
    DefaultHandler
  }

  private val v3Router = Router.from { case GET(p"/v3") =>
    V3Handler
  }

  private val v4Router = Router.from { case GET(p"/v4") =>
    V4Handler
  }

  private val routingMap = new VersionRoutingMap {
    override val defaultRouter: Router     = test.defaultRouter
    override val map: Map[Version, Router] = Map(Version3 -> v3Router, Version4 -> v4Router)
  }

  "Given a request that end with a trailing slash, and no version header" when {

    "the handler is found" should {
      "use it" in new Test {
        val maybeAcceptHeader: Option[String] = None

        MockedAppConfig
          .endpointsEnabled(Version3)
          .returns(true)
          .anyNumberOfTimes()

        val result: Option[Handler] = requestHandler.routeRequest(buildRequest("/"))
        result shouldBe Some(DefaultHandler)
      }
    }

    "the handler isn't found" should {
      "try without the trailing slash" in new Test {
        val maybeAcceptHeader: Option[String] = None
        MockedAppConfig.endpointsEnabled(Version3).returns(true).anyNumberOfTimes()

        val result: Option[Handler] = requestHandler.routeRequest(buildRequest(""))
        result shouldBe Some(DefaultHandler)
      }
    }
  }

  "Routing request with a valid version header" should {
    handleWithVersionRoutes("/v3", V3Handler, Version3)
  }

  "Routing request with another valid version header" should {
    handleWithVersionRoutes("/v4", V4Handler, Version4)
  }

  private def handleWithVersionRoutes(path: String, handler: Handler, version: Version): Unit = {

    withClue("request ends with a trailing slash...") {
      new Test {
        val maybeAcceptHeader: Option[String] = Some(s"application/vnd.hmrc.$version+json")
        MockedAppConfig.endpointsEnabled(version).returns(true).anyNumberOfTimes()

        val result: Option[Handler] = requestHandler.routeRequest(buildRequest(s"$path/"))
        result shouldBe Some(handler)
      }
    }
    withClue("request doesn't end with a trailing slash...") {
      new Test {
        val maybeAcceptHeader: Option[String] = Some(s"application/vnd.hmrc.$version+json")
        MockedAppConfig.endpointsEnabled(version).returns(true).anyNumberOfTimes()

        val result: Option[Handler] = requestHandler.routeRequest(buildRequest(s"$path"))
        result shouldBe Some(handler)
      }
    }
  }

  "Routing requests to non-default router with no version" should {

    "return 406" in new Test {
      val maybeAcceptHeader: Option[String] = None

      private val request = buildRequest("/v1")

      inside(requestHandler.routeRequest(request)) { case Some(a: EssentialAction) =>
        val result = a.apply(request)

        status(result) shouldBe NOT_ACCEPTABLE
        contentAsJson(result) shouldBe InvalidAcceptHeaderError.asJson
      }
    }
  }

  "Routing requests with unsupported version" should {

    "return 404" in new Test {
      val maybeAcceptHeader: Option[String] = Some("application/vnd.hmrc.5.0+json")

      private val request = buildRequest("/v1")

      inside(requestHandler.routeRequest(request)) { case Some(a: EssentialAction) =>
        val result = a.apply(request)

        status(result) shouldBe NOT_FOUND
        contentAsJson(result) shouldBe UnsupportedVersionError.asJson
      }
    }
  }

  "Routing requests with retired v2 version" when {

    "return 404 Not Found" in new Test {
      val maybeAcceptHeader: Option[String] = Some("application/vnd.hmrc.5.0+json")

      private val request = buildRequest("/v2")
      inside(requestHandler.routeRequest(request)) { case Some(a: EssentialAction) =>
        val result = a.apply(request)
        status(result) shouldBe NOT_FOUND
        contentAsJson(result) shouldBe UnsupportedVersionError.asJson
      }
    }
  }

  private abstract class Test {

    protected def maybeAcceptHeader: Option[String]

    private val httpConfiguration = HttpConfiguration("context")
    private val errorHandler      = mock[HttpErrorHandler]
    private val filters           = mock[HttpFilters]

    (() => filters.filters).stubs().returns(Nil)

    protected val requestHandler: VersionRoutingRequestHandler =
      new VersionRoutingRequestHandler(routingMap, errorHandler, httpConfiguration, mockAppConfig, filters, action)

    protected def buildRequest(path: String): RequestHeader =
      maybeAcceptHeader
        .foldLeft(FakeRequest("GET", path)) { (req, accept) =>
          req.withHeaders((ACCEPT, accept))
        }

  }

}
