
package v2.connectors

import config.AppConfig
import javax.inject.{Inject, Singleton}
import uk.gov.hmrc.http.{HeaderCarrier, HttpReads, HttpResponse}
import uk.gov.hmrc.play.bootstrap.http.HttpClient
import v2.models.request.crystallisation.CrystallisationRequest

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class NrsProxyConnector @Inject()(http: HttpClient,
                                  appConfig: AppConfig) {

  def submit[T](nino: String, taxYear:String, crystallisation: CrystallisationRequest)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Unit] = {
    import v2.models.request.crystallisation.CrystallisationRequest
    implicit val readsEmpty: HttpReads[Unit] =
      (method: String, url: String, response: HttpResponse) => ()
    http.POST[CrystallisationRequest, Unit](s"${appConfig.mtdNrsProxyBaseUrl}/crystallisation/$nino/$taxYear/crystallise", crystallisation)
  }
}