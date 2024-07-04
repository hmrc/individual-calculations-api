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

package shared.controllers

import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, Result}
import shared.models.errors._
import shared.services.{EnrolmentsAuthService, MockEnrolmentsAuthService, MockMtdIdLookupService, MtdIdLookupService}
import uk.gov.hmrc.auth.core.Enrolment
import uk.gov.hmrc.auth.core.authorise.Predicate
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class AuthorisedControllerSpec extends ControllerBaseSpec {

  private val nino = "AA123456A"

  private val mtdId     = "X123567890"
  private val someError = MtdError("SOME_CODE", "A message", IM_A_TEAPOT)

  private val predicate: Predicate = Enrolment("HMRC-MTD-IT")
    .withIdentifier("MTDITID", mtdId)
    .withDelegatedAuthRule("mtd-it-auth")

  "calling an action" when {

    "the user is authorised" should {
      "return a 200" in new Test {
        MockedMtdIdLookupService.lookup(nino) returns Future.successful(Right(mtdId))

        MockedEnrolmentsAuthService.authoriseUser()

        private val result = target.action(nino)(fakeGetRequest)
        status(result) shouldBe OK
      }
    }

    "the EnrolmentsAuthService returns an error" should {
      "return that error (with its status code)" in new Test {
        MockedMtdIdLookupService.lookup(nino) returns Future.successful(Right(mtdId))

        MockedEnrolmentsAuthService.authorised(predicate) returns Future.successful(Left(someError))

        val result: Future[Result] = target.action(nino)(fakeGetRequest)
        status(result) shouldBe someError.httpStatus
        contentAsJson(result) shouldBe Json.toJson(someError)
      }
    }

    "the MtdIdLookupService returns an error" should {
      "return that error (with its status code)" in new Test {
        MockedMtdIdLookupService.lookup(nino) returns Future.successful(Left(someError))

        val result: Future[Result] = target.action(nino)(fakeGetRequest)
        status(result) shouldBe someError.httpStatus
        contentAsJson(result) shouldBe Json.toJson(someError)
      }
    }
  }

  trait Test extends MockEnrolmentsAuthService with MockMtdIdLookupService {

    val hc: HeaderCarrier = HeaderCarrier()

    class TestController extends AuthorisedController(cc) {
      override val authService: EnrolmentsAuthService = mockEnrolmentsAuthService
      override val lookupService: MtdIdLookupService  = mockMtdIdLookupService

      def action(nino: String): Action[AnyContent] = authorisedAction(nino).async {
        Future.successful(Ok(Json.obj()))
      }

    }

    lazy val target = new TestController()
  }

}
