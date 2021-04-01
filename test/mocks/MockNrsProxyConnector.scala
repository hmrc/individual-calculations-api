
package mocks

import org.scalamock.handlers.CallHandler
import org.scalamock.scalatest.MockFactory
import uk.gov.hmrc.http.HeaderCarrier
import v2.connectors.NrsProxyConnector
import v2.models.request.crystallisation.CrystallisationRequest

import scala.concurrent.{ExecutionContext, Future}

trait MockNrsProxyConnector extends MockFactory {

  val mockNrsProxyConnector: NrsProxyConnector = mock[NrsProxyConnector]

  object MockNrsProxyConnector {
    def submit(nino: String, taxYear: String, crystallisation: CrystallisationRequest): CallHandler[Future[Unit]] = {
      (mockNrsProxyConnector.submit(_: String, _:String, _: CrystallisationRequest)(_: HeaderCarrier, _: ExecutionContext))
        .expects(nino, *, *, *, *)
    }
  }

}
