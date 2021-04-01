
package v2.services

import mocks.MockNrsProxyConnector
import v2.models.domain.DesTaxYear
import v2.models.request.crystallisation.CrystallisationRequest

class NrsProxyServiceSpec extends ServiceSpec {

  trait Test extends MockNrsProxyConnector {
    lazy val service = new NrsProxyService(mockNrsProxyConnector)
  }

  "NrsProxyService" should {
    "call the Nrs Proxy connector" when {
      "the connector is valid" in new Test {

        // should not call the connector
        MockNrsProxyConnector.submit(nino.toString(), "2021-22", CrystallisationRequest(nino, DesTaxYear("2021-22"), "4557ecb5-fd32-48cc-81f5-e6acd1099f3c"))
          .never()

        service.submit(nino.toString(), "2021-22", CrystallisationRequest(nino, DesTaxYear("2021-22"), "4557ecb5-fd32-48cc-81f5-e6acd1099f3c"))

      }
    }

  }

}
