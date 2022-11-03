/*
 * Copyright 2022 HM Revenue & Customs
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

package v3.handler

import cats.implicits._
import org.scalatest.Inside
import play.api.http.Status._
import support.UnitSpec
import v3.connectors.httpparsers.StandardDownstreamHttpParser.SuccessCode
import v3.handler.RequestHandler.ErrorMapping
import v3.models.errors.{ErrorWrapper, MtdError}
import v3.models.outcomes.ResponseWrapper

class RequestHandlerSpec extends UnitSpec with Inside {

  // WLOG
  val passThroughErrors                = List(MtdError("CODE", "error"))
  val customErrorMapping: ErrorMapping = { case "CODE" => (BAD_REQUEST, MtdError("CODE", "error")) }

  "RequestHandler" must {
    "be buildable" in {

      val handling =
        RequestHandler[String](RequestDefn.Get("/some/path"))
          .withRequestSuccessCode(ACCEPTED)
          .mapErrors(customErrorMapping)
          .withPassThroughErrors(passThroughErrors: _*)

      inside(handling) { case RequestHandler.Impl(requestDefn, successCode, passThroughErrors, customErrorMapping, _) =>
        requestDefn shouldBe RequestDefn.Get("/some/path")
        successCode shouldBe SuccessCode(ACCEPTED)
        passThroughErrors shouldBe passThroughErrors
        customErrorMapping shouldBe customErrorMapping
      }
    }
  }

  "successMapping" when {
    "single mapping" must {
      "map" in {
        val handling =
          RequestHandler[String](RequestDefn.Get("/some/path"))
            .mapSuccess { case ResponseWrapper(_, s) =>
              Right(ResponseWrapper("corrId1", s.length))
            }

        handling.successMapping(ResponseWrapper("corrId", "foo")).value shouldBe ResponseWrapper("corrId1", 3)
      }
    }

    "multiple mappings and all success" must {
      "map in sequence" in {
        val handling =
          RequestHandler[String](RequestDefn.Get("/some/path"))
            .mapSuccess { case ResponseWrapper(_, s) =>
              Right(ResponseWrapper("corrId1", s.length))
            }
            .mapSuccess { case ResponseWrapper(_, i) =>
              Right(ResponseWrapper("corrId2", String.valueOf(i)))
            }

        handling.successMapping(ResponseWrapper("corrId", "foo")).value shouldBe ResponseWrapper("corrId2", "3")
      }
    }

    "multiple mappings and some map to error" must {
      "map to the first error" in {
        val handling =
          RequestHandler[String](RequestDefn.Get("/some/path"))
            .mapSuccess { case ResponseWrapper(_, s) =>
              Right(ResponseWrapper("corrId1", s.length))
            }
            .mapSuccess { case ResponseWrapper(_, _) =>
              ErrorWrapper("corrId1", MtdError("code1", "error1"), None).asLeft[ResponseWrapper[Int]]
            }
            .mapSuccess { case ResponseWrapper(_, _) =>
              ErrorWrapper("corrId2", MtdError("code2", "error2"), None).asLeft[ResponseWrapper[Int]]
            }

        handling.successMapping(ResponseWrapper("corrId", "foo")).left.value shouldBe
          ErrorWrapper("corrId1", MtdError("code1", "error1"), None)
      }
    }

    "simple mappings" must {
      "map the payload" in {
        val handling =
          RequestHandler[String](RequestDefn.Get("/some/path"))
            .mapSuccessSimple(_.length)
            .mapSuccessSimple(String.valueOf)

        handling.successMapping(ResponseWrapper("corrId", "foo")).value shouldBe ResponseWrapper("corrId", "3")
      }
    }
  }

  "Get" must {
    "allow parameters to be added from options" in {
      RequestDefn.Get("/some/path").withOptionalParams("a" -> None, "b" -> Some("value")) shouldBe
        RequestDefn.Get("/some/path", Seq("b" -> "value"))
    }

    "allow parameters to be added directly" in {
      RequestDefn.Get("/some/path").withParams("b" -> "value") shouldBe
        RequestDefn.Get("/some/path", Seq("b" -> "value"))
    }
  }

}
