/*
 * Copyright 2021 HM Revenue & Customs
 *
 */

package v3.services

import uk.gov.hmrc.http.HeaderCarrier
import utils.Logging
import v3.connectors.{TriggerCalculationConnector, TriggerResponse}
import v3.controllers.EndpointLogContext
import v3.models.domain.{Nino, TaxYear}
import v3.models.errors.ErrorWrapper
import v3.models.outcomes.ResponseWrapper
import v3.support.DownstreamResponseMappingSupport

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

// FIXME remove when models available
case class TriggerCalculationRequest(nino: Nino, taxYear: TaxYear, finalDeclaration: Option[Boolean])

@Singleton
class TriggerCalculationService @Inject() (connector: TriggerCalculationConnector) extends DownstreamResponseMappingSupport with Logging {

  def triggerCalculation(request: TriggerCalculationRequest)(implicit
      hc: HeaderCarrier,
      ec: ExecutionContext,
      logContext: EndpointLogContext,
      correlationId: String): Future[Either[ErrorWrapper, ResponseWrapper[TriggerResponse]]] = ???

}
