/*
 * Copyright 2019 HM Revenue & Customs
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

package v1.handling

import play.api.http.Status._
import support.UnitSpec
import v1.connectors.httpparsers.StandardHttpParser.SuccessCode
import v1.handling.RequestHandling.{ ErrorMapping, SuccessMapping }
import v1.models.errors.MtdError
import v1.models.outcomes.ResponseWrapper

class RequestHandlingSpec extends UnitSpec {

  // WLOG
  val passThroughErrors                              = List(MtdError("CODE", "error"))
  val customErrorMapping: ErrorMapping               = { case "CODE" => (BAD_REQUEST, MtdError("CODE", "error")) }
  val successMapping: SuccessMapping[String, String] = (r: ResponseWrapper[String]) => Right(r)

  "RequestHandling" must {
    "be buildable" in {

      RequestHandling[String](RequestDefn.Get("/some/path"))
        .withRequestSuccessCode(ACCEPTED)
        .mapErrors(customErrorMapping)
        .withPassThroughErrors(passThroughErrors: _*)
        .mapSuccess(successMapping) shouldBe
        RequestHandling.Impl[String, String](
          requestDefn = RequestDefn.Get("/some/path"),
          successCode = SuccessCode(ACCEPTED),
          passThroughErrors = passThroughErrors,
          customErrorMapping = customErrorMapping,
          successMapping = successMapping
        )
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
