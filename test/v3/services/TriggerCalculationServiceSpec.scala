/*
 * Copyright 2021 HM Revenue & Customs
 *
 */

package v3.services

import v3.connectors.TriggerResponse
import v3.mocks.connectors.MockTriggerCalculationConnector
import v3.models.domain.TaxYear
import v3.models.errors._
import v3.models.outcomes.ResponseWrapper

import scala.concurrent.Future

class TriggerCalculationServiceSpec extends ServiceSpec {

  val taxYear: TaxYear = TaxYear.fromDownstream("2020")

  def request(finalDeclaration: Option[Boolean]): TriggerCalculationRequest = TriggerCalculationRequest(nino, taxYear, finalDeclaration)

  val response: TriggerResponse = TriggerResponse("someCalcId")

  trait Test extends MockTriggerCalculationConnector {
    val service = new TriggerCalculationService(mockConnector)
  }

  "triggerCalculation" when {
    "finalDeclaration parameter specified" when {
      "successful" must {
        "use the parameters and return the response" in new Test {

          MockTriggerCalculationConnector
            .triggerCalculation(nino, taxYear, finalDeclaration = true)
            .returns(Future.successful(Right(ResponseWrapper(correlationId, response))))

          await(service.triggerCalculation(request(Some(true)))) shouldBe Right(ResponseWrapper(correlationId, response))
        }
      }
    }

    "finalDeclaration parameter not specified" when {
      "successful" must {
        "default finalDeclaration to false and return the response" in new Test {

          MockTriggerCalculationConnector
            .triggerCalculation(nino, taxYear, finalDeclaration = false)
            .returns(Future.successful(Right(ResponseWrapper(correlationId, response))))

          await(service.triggerCalculation(request(None))) shouldBe Right(ResponseWrapper(correlationId, response))
        }
      }
    }

    "unsuccessful" should {
      "map errors according to spec" when {
        def serviceError(downstreamErrorCode: String, error: MtdError): Unit =
          s"a $downstreamErrorCode error is returned from the service" in new Test {

            MockTriggerCalculationConnector
              .triggerCalculation(nino, taxYear, finalDeclaration = false)
              .returns(Future.successful(Left(ResponseWrapper(correlationId, DesErrors.single(DesErrorCode(downstreamErrorCode))))))

            await(service.triggerCalculation(request(None))) shouldBe Left(ErrorWrapper(correlationId, error))
          }

        val input = Seq(
          ("INVALID_NINO", NinoFormatError),
          ("INVALID_TAX_YEAR", TaxYearFormatError),
          ("INVALID_TAX_CRYSTALLISE", FormatFinalDeclaration),
          ("INVALID_REQUEST", DownstreamError),
          ("NO_SUBMISSION_EXIST", RuleNoSubmissionsExistError),
          ("CONFLICT", RuleFinalDeclarationReceivedError),
          ("SERVER_ERROR", DownstreamError),
          ("SERVICE_UNAVAILABLE", DownstreamError)
        )

        input.foreach(args => (serviceError _).tupled(args))
      }
    }

  }

}
