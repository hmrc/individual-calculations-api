/*
 * Copyright 2026 HM Revenue & Customs
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

package v9.createAmendProvisionalFiguresInformation

import api.models.domain.TaxYear
import api.models.errors.*
import api.services.{AuditStub, AuthStub, DownstreamStub, MtdIdLookupStub}
import api.support.IntegrationBaseSpec
import com.github.tomakehurst.wiremock.stubbing.StubMapping
import play.api.libs.json.{JsObject, JsValue, Json}
import play.api.libs.ws.{WSRequest, WSResponse, writeableOf_JsValue}
import play.api.test.Helpers.*
import v9.createAmendProvisionalFiguresInformation.CreateAmendProvisionalFiguresInformationFixtures.{
  expectedDate,
  nino as fixtureNino,
  taxYear as fixtureTaxYear,
  validMtdRequestJson
}

import scala.math.Ordering.Implicits.infixOrderingOps

class CreateAmendProvisionalFiguresInformationISpec extends IntegrationBaseSpec {

  private val parsedTaxYear: TaxYear   = fixtureTaxYear
  private val notEndedTaxYear: TaxYear = parsedTaxYear.max(TaxYear.currentTaxYear)

  private val invalidIndicatorJson: JsValue = Json.parse(s"""
      |{
      |  "provisionalFiguresIndicator": false,
      |  "expectedDate": "$expectedDate",
      |  "supplementaryInformation": "Provisional figures used pending final confirmation."
      |}
    """.stripMargin)

  private val invalidExpectedDateFormatJson: JsValue = Json.parse("""
      |{
      |  "provisionalFiguresIndicator": true,
      |  "expectedDate": "not-a-date",
      |  "supplementaryInformation": "Provisional figures used pending final confirmation."
      |}
    """.stripMargin)

  private val invalidSupplementaryInformationJson: JsValue = Json.parse(s"""
      |{
      |  "provisionalFiguresIndicator": true,
      |  "expectedDate": "$expectedDate",
      |  "supplementaryInformation": ""
      |}
    """.stripMargin)

  private val expectedDateNotInFutureJson: JsValue = Json.parse("""
      |{
      |  "provisionalFiguresIndicator": true,
      |  "expectedDate": "2020-01-01",
      |  "supplementaryInformation": "Provisional figures used pending final confirmation."
      |}
    """.stripMargin)

  "Calling the Create or Amend Provisional Figures Information endpoint" should {
    "return a 204 status code" when {
      "a valid request is made" in new Test {
        override def setupStubs(): StubMapping = {
          AuditStub.audit()
          MtdIdLookupStub.ninoFound(nino)
          AuthStub.authorised()

          DownstreamStub.onSuccess(
            method = DownstreamStub.PUT,
            uri = downstreamUri,
            queryParams = downstreamQueryParams,
            status = NO_CONTENT,
            body = JsObject.empty
          )
        }

        val response: WSResponse = await(request().put(mtdJson))
        response.status shouldBe NO_CONTENT
        response.body shouldBe ""
        response.header("Content-Type") shouldBe None
        response.header("X-CorrelationId").nonEmpty shouldBe true
      }
    }

    "return error according to spec" when {
      "validation error" when {
        def validationErrorTest(requestNino: String,
                                requestTaxYear: String,
                                requestBody: JsValue,
                                expectedStatus: Int,
                                expectedBody: MtdError): Unit = {
          s"validation fails with ${expectedBody.code} error" in new Test {

            override val nino: String                       = requestNino
            override val taxYear: String                    = requestTaxYear
            override val suspendTemporalValidations: String = (expectedBody != RuleTaxYearNotEndedError).toString

            override def setupStubs(): StubMapping = {
              AuditStub.audit()
              MtdIdLookupStub.ninoFound(nino)
              AuthStub.authorised()
            }

            val response: WSResponse = await(request().put(requestBody))
            response.status shouldBe expectedStatus
            response.json shouldBe Json.toJson(expectedBody)
          }
        }

        val input = List(
          ("AA1123A", "2026-27", mtdJson, BAD_REQUEST, NinoFormatError),
          ("AA123456A", "invalid", mtdJson, BAD_REQUEST, TaxYearFormatError),
          ("AA123456A", "2025-27", mtdJson, BAD_REQUEST, RuleTaxYearRangeInvalidError),
          ("AA123456A", "2025-26", mtdJson, BAD_REQUEST, RuleTaxYearNotSupportedError),
          ("AA123456A", notEndedTaxYear.asMtd, mtdJson, BAD_REQUEST, RuleTaxYearNotEndedError),
          ("AA123456A", "2026-27", JsObject.empty, BAD_REQUEST, RuleIncorrectOrEmptyBodySubmittedError),
          (
            "AA123456A",
            "2026-27",
            invalidIndicatorJson,
            BAD_REQUEST,
            RuleProvisionalFiguresIndicatorInvalidError.withPath("/provisionalFiguresIndicator")
          ),
          ("AA123456A", "2026-27", invalidExpectedDateFormatJson, BAD_REQUEST, FormatExpectedDateError.withPath("/expectedDate")),
          (
            "AA123456A",
            "2026-27",
            invalidSupplementaryInformationJson,
            BAD_REQUEST,
            FormatSupplementaryInformationError.withPath("/supplementaryInformation")
          ),
          ("AA123456A", "2026-27", expectedDateNotInFutureJson, BAD_REQUEST, RuleExpectedDateNotInFutureError.withPath("/expectedDate"))
        )

        input.foreach(validationErrorTest.tupled)
      }

      "downstream service error" when {
        def serviceErrorTest(downstreamStatus: Int, downstreamCode: String, expectedStatus: Int, expectedBody: MtdError): Unit = {
          s"downstream returns an $downstreamCode error and status $downstreamStatus" in new Test {
            override def setupStubs(): StubMapping = {
              AuditStub.audit()
              MtdIdLookupStub.ninoFound(nino)
              AuthStub.authorised()

              DownstreamStub.onError(
                method = DownstreamStub.PUT,
                uri = downstreamUri,
                queryParams = downstreamQueryParams,
                errorStatus = downstreamStatus,
                errorBody = errorBody(downstreamCode)
              )
            }

            val response: WSResponse = await(request().put(mtdJson))
            response.status shouldBe expectedStatus
            response.json shouldBe Json.toJson(expectedBody)
          }
        }

        val errors = List(
          (BAD_REQUEST, "1215", BAD_REQUEST, NinoFormatError),
          (BAD_REQUEST, "1117", BAD_REQUEST, TaxYearFormatError),
          (BAD_REQUEST, "1216", INTERNAL_SERVER_ERROR, InternalError),
          (BAD_REQUEST, "1000", INTERNAL_SERVER_ERROR, InternalError),
          (UNPROCESSABLE_ENTITY, "1285", BAD_REQUEST, RuleExpectedDateNotInFutureError),
          (UNPROCESSABLE_ENTITY, "4200", BAD_REQUEST, RuleOutsideAmendmentWindowError)
        )

        errors.foreach(serviceErrorTest.tupled)
      }
    }
  }

  private trait Test {
    val nino: String                       = fixtureNino.nino
    val taxYear: String                    = parsedTaxYear.asMtd
    val suspendTemporalValidations: String = "true"

    def setupStubs(): StubMapping

    def downstreamUri: String = s"/itsd/provisional-figures/$nino"

    def downstreamQueryParams: Map[String, String] = Map("taxYear" -> TaxYear.fromMtd(taxYear).asTysDownstream)

    def request(): WSRequest = {
      AuthStub.resetAll()
      setupStubs()

      buildRequest(s"/$nino/provisional-figures/$taxYear")
        .withHttpHeaders(
          (ACCEPT, "application/vnd.hmrc.9.0+json"),
          ("suspend-temporal-validations", suspendTemporalValidations),
          (AUTHORIZATION, "******")
        )
    }

    def errorBody(code: String): String =
      s"""
         |[
         |  {
         |    "errorCode": "$code",
         |    "errorDescription": "error description"
         |  }
         |]
           """.stripMargin

    val mtdJson: JsValue = validMtdRequestJson

  }

}
