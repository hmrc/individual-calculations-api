
package v2.services

import javax.inject.{Inject, Singleton}
import uk.gov.hmrc.http.HeaderCarrier
import v2.connectors.NrsProxyConnector
import v2.models.request.crystallisation.CrystallisationRequest

import scala.concurrent.ExecutionContext

@Singleton
class NrsProxyService @Inject()(val connector: NrsProxyConnector) {

  def submit(nino: String, taxYear: String, crystallisation: CrystallisationRequest)(implicit hc: HeaderCarrier, ec: ExecutionContext): Unit = {

    connector.submit(nino, taxYear, crystallisation)
  }

}
