/*
 * Copyright 2021 HM Revenue & Customs
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

package v1r2.controllers

import play.api.libs.json.Json
import play.api.mvc._
import uk.gov.hmrc.auth.core.Enrolment
import uk.gov.hmrc.auth.core.authorise.Predicate
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.controller.BackendController
import v1r2.models.auth.UserDetails
import v1r2.models.errors._
import v1r2.services.{EnrolmentsAuthService, MtdIdLookupService}

import scala.concurrent.{ ExecutionContext, Future }

case class UserRequest[A](userDetails: UserDetails, request: Request[A]) extends WrappedRequest[A](request)

abstract class AuthorisedController(cc: ControllerComponents)(implicit ec: ExecutionContext) extends BackendController(cc) {

  val authService: EnrolmentsAuthService
  val lookupService: MtdIdLookupService

  def authorisedAction(nino: String): ActionBuilder[UserRequest, AnyContent] = new ActionBuilder[UserRequest, AnyContent] {

    override def parser: BodyParser[AnyContent] = cc.parsers.defaultBodyParser

    override protected def executionContext: ExecutionContext = cc.executionContext

    def predicate(mtdId: String): Predicate =
      Enrolment("HMRC-MTD-IT")
        .withIdentifier("MTDITID", mtdId)
        .withDelegatedAuthRule("mtd-it-auth")

    def invokeBlockWithAuthCheck[A](mtdId: String, request: Request[A], block: UserRequest[A] => Future[Result])(
        implicit headerCarrier: HeaderCarrier): Future[Result] = {
      authService.authorised(predicate(mtdId)).flatMap[Result] {
        case Right(userDetails)      => block(UserRequest(userDetails.copy(mtdId = mtdId), request))
        case Left(UnauthorisedError) => Future.successful(Forbidden(Json.toJson(UnauthorisedError)))
        case Left(_)                 => Future.successful(InternalServerError(Json.toJson(DownstreamError)))
      }
    }

    override def invokeBlock[A](request: Request[A], block: UserRequest[A] => Future[Result]): Future[Result] = {

      implicit val headerCarrier: HeaderCarrier = hc(request)

      lookupService.lookup(nino).flatMap[Result] {
        case Right(mtdId)                  => invokeBlockWithAuthCheck(mtdId, request, block)
        case Left(NinoFormatError)         => Future.successful(BadRequest(Json.toJson(NinoFormatError)))
        case Left(UnauthorisedError)       => Future.successful(Forbidden(Json.toJson(UnauthorisedError)))
        case Left(InvalidBearerTokenError) => Future.successful(Unauthorized(Json.toJson(InvalidBearerTokenError)))
        case Left(_)                       => Future.successful(InternalServerError(Json.toJson(DownstreamError)))
      }
    }
  }
}