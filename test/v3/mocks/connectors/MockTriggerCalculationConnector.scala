/*
 * Copyright 2021 HM Revenue & Customs
 *
 */

package v3.mocks.connectors

import org.scalamock.handlers.CallHandler
import org.scalamock.scalatest.MockFactory
import uk.gov.hmrc.http.HeaderCarrier
import v3.connectors.{DownstreamOutcome, TriggerCalculationConnector, TriggerResponse}
import v3.models.domain.{Nino, TaxYear}

import scala.concurrent.{ExecutionContext, Future}

trait MockTriggerCalculationConnector extends MockFactory {

  val mockConnector: TriggerCalculationConnector = mock[TriggerCalculationConnector]

  object MockTriggerCalculationConnector {

    def triggerCalculation(nino: Nino, taxYear: TaxYear, finalDeclaration: Boolean): CallHandler[Future[DownstreamOutcome[TriggerResponse]]] = {
      (mockConnector
        .triggerCalculation(_: Nino, _: TaxYear, _: Boolean)(_: HeaderCarrier, _: ExecutionContext, _: String))
        .expects(nino, taxYear, finalDeclaration, *, *, *)
    }

  }

}
