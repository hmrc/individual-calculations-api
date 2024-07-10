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

package shared.services

import play.api.http.Status._
import shared.connectors.MtdIdLookupConnector
import shared.controllers.validators.resolvers.ResolveNino
import shared.models.errors.{InvalidBearerTokenError, NinoFormatError, _}
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

object MtdIdLookupService {
  type Outcome = Either[MtdError, String]
}

@Singleton
class MtdIdLookupService @Inject() (val connector: MtdIdLookupConnector) {

  def lookup(nino: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[MtdIdLookupService.Outcome] = {
    if (!ResolveNino.isValid(nino)) {
      Future.successful(Left(NinoFormatError))
    } else {
      connector.getMtdId(nino) map {
        case Right(mtdId) => Right(mtdId)
        case Left(MtdIdLookupConnector.Error(statusCode)) =>
          statusCode match {
            case FORBIDDEN    => Left(ClientOrAgentNotAuthorisedError)
            case UNAUTHORIZED => Left(InvalidBearerTokenError)
            case _            => Left(InternalError)
          }
      }
    }
  }

}
