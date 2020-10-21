/*
 * Copyright 2020 HM Revenue & Customs
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

package v1.connectors.httpparsers

import play.api.http.Status._
import play.api.libs.json.{JsObject, JsValue, Json, Reads}
import support.UnitSpec
import uk.gov.hmrc.http.{HttpReads, HttpResponse}
import v1.connectors.BackendOutcome
import v1.models.errors._
import v1.models.outcomes.ResponseWrapper
import v1.models.response.common.{DesResponse, DesUnit}

class StandardHttpParserSpec extends UnitSpec {

  import v1.connectors.httpparsers.StandardHttpParser._

  // WLOG if Reads tested elsewhere
  case class SomeModel(data: String)

  object SomeModel {
    implicit val reads: Reads[SomeModel] = Json.reads
  }

  // WLOG if Reads tested elsewhere
  case class SomeDesModel(data: String) extends DesResponse

  object SomeDesModel {
    implicit val reads: Reads[SomeDesModel] = Json.reads[SomeDesModel]
  }

  val method: String = "POST"
  val url: String = "test-url"
  val correlationId: String = "a1e8057e-fbbc-47a8-a8b4-78d9f015c253"
  val data: String = "someData"
  val expectedJson: JsValue = Json.obj("data" -> data)

  val mtdModel: SomeModel = SomeModel(data)
  val mtdResponse: ResponseWrapper[SomeModel] = ResponseWrapper(correlationId, mtdModel)

  val desModel: SomeDesModel = SomeDesModel(data)
  val desResponse: ResponseWrapper[SomeDesModel] = ResponseWrapper(correlationId, desModel)

  val singleErrorJson: JsValue = Json.parse(
    """
      |{
      |   "code": "CODE",
      |   "reason": "MESSAGE"
      |}
    """.stripMargin
  )

  val multipleErrorsJson: JsValue = Json.parse(
    """
      |{
      |   "code": "CODE",
      |   "reason": "MESSAGE",
      |   "errors": [
      |       {
      |           "code": "CODE 1",
      |           "reason": "MESSAGE 1"
      |       },
      |       {
      |           "code": "CODE 2",
      |           "reason": "MESSAGE 2"
      |       }
      |   ]
      |}
    """.stripMargin
  )

  val multipleDesErrorsJson: JsValue = Json.parse(
    """
      |{
      |   "failures": [
      |       {
      |           "code": "CODE 1",
      |           "reason": "MESSAGE 1"
      |       },
      |       {
      |           "code": "CODE 2",
      |           "reason": "MESSAGE 2"
      |       }
      |   ]
      |}
    """.stripMargin
  )

  val missingPrimaryErrorJson: JsValue = Json.parse(
    """
      |{
      |   "errors": [
      |       {
      |           "code": "CODE 1",
      |           "reason": "MESSAGE 1"
      |       },
      |       {
      |           "code": "CODE 2",
      |           "reason": "MESSAGE 2"
      |       }
      |   ]
      |}
    """.stripMargin
  )

  val malformedErrorJson: JsValue = Json.parse(
    """
      |{
      |   "coed": "CODE",
      |   "resaon": "MESSAGE"
      |}
    """.stripMargin
  )

  def getCorrelationIdMap(isDesResponse: Boolean): Map[String, Seq[String]] =
    if (isDesResponse) Map("CorrelationId" -> Seq(correlationId)) else Map("X-CorrelationId" -> Seq(correlationId))

  "The generic HTTP parser (from MTD)" when {
    "no status code is specified" must {
      val httpReads: HttpReads[BackendOutcome[SomeModel]] = implicitly

      "return a Right backend response containing the model object if the response json corresponds to a model object" in {
        val httpResponse = HttpResponse(OK, expectedJson.toString(), getCorrelationIdMap(isDesResponse = false))

        httpReads.read(method, url, httpResponse) shouldBe Right(mtdResponse)
      }

      "return an outbound 500 DownstreamError if a model object cannot be read from the response json" in {
        val badFieldTypeJson: JsValue = Json.obj("incomeSourceId" -> 1234, "incomeSourceName" -> 1234)
        val httpResponse = HttpResponse(OK, badFieldTypeJson.toString(), getCorrelationIdMap(isDesResponse = false))
        val expected = ResponseWrapper(correlationId, OutboundError(INTERNAL_SERVER_ERROR, DownstreamError))

        httpReads.read(method, url, httpResponse) shouldBe Left(expected)
      }

      handleErrorsCorrectly(httpReads, isDesResponse = false)
      handleInternalErrorsCorrectly(httpReads, isDesResponse = false)
      handleUnexpectedResponse(httpReads, isDesResponse = false)
    }

    "a success code is specified" must {
      "use that status code for success" in {
        implicit val successCode: SuccessCode = SuccessCode(PARTIAL_CONTENT)
        val httpReads: HttpReads[BackendOutcome[SomeModel]] = implicitly
        val httpResponse = HttpResponse(PARTIAL_CONTENT, expectedJson.toString(), Map("X-CorrelationId" -> Seq(correlationId)))

        httpReads.read(method, url, httpResponse) shouldBe Right(mtdResponse)
      }
    }
  }

  "The generic HTTP parser (from DES)" when {
    "no status code is specified" must {
      val httpReads: HttpReads[BackendOutcome[SomeDesModel]] = implicitly

      "return a Right DES response containing the model object if the response json corresponds to a model object" in {
        val httpResponse = HttpResponse(OK, expectedJson.toString(), getCorrelationIdMap(isDesResponse = true))

        httpReads.read(method, url, httpResponse) shouldBe Right(desResponse)
      }

      "return an outbound error if a model object cannot be read from the response json" in {
        val badFieldTypeJson: JsValue = Json.obj("incomeSourceId" -> 1234, "incomeSourceName" -> 1234)
        val httpResponse = HttpResponse(OK, badFieldTypeJson.toString(), getCorrelationIdMap(isDesResponse = true))
        val expected = ResponseWrapper(correlationId, OutboundError(INTERNAL_SERVER_ERROR, DownstreamError))

        httpReads.read(method, url, httpResponse) shouldBe Left(expected)
      }

      handleErrorsCorrectly(httpReads, isDesResponse = true)
      handleInternalErrorsCorrectly(httpReads, isDesResponse = true)
      handleUnexpectedResponse(httpReads, isDesResponse = true)
      handleBvrsCorrectly(httpReads)
    }

    "a success code is specified" must {
      "use that status code for success" in {
        implicit val successCode: SuccessCode = SuccessCode(PARTIAL_CONTENT)
        val httpReads: HttpReads[BackendOutcome[SomeDesModel]] = implicitly
        val httpResponse = HttpResponse(PARTIAL_CONTENT, expectedJson.toString(), getCorrelationIdMap(isDesResponse = true))

        httpReads.read(method, url, httpResponse) shouldBe Right(desResponse)
      }
    }
  }

  "The generic HTTP parser for empty response (from MTD)" when {
    "no status code is specified" must {
      val httpReads: HttpReads[BackendOutcome[Unit]] = implicitly

      "receiving a 204 response" should {
        "return a Right backend response with the correct correlationId and no responseData" in {
          val httpResponse = HttpResponse(NO_CONTENT, JsObject.empty, getCorrelationIdMap(isDesResponse = false))

          httpReads.read(method, url, httpResponse) shouldBe Right(ResponseWrapper(correlationId, ()))
        }
      }

      handleErrorsCorrectly(httpReads, isDesResponse = false)
      handleInternalErrorsCorrectly(httpReads, isDesResponse = false)
      handleUnexpectedResponse(httpReads, isDesResponse = false)
    }

    "a success code is specified" must {
      implicit val successCode: SuccessCode = SuccessCode(PARTIAL_CONTENT)
      val httpReads: HttpReads[BackendOutcome[Unit]] = implicitly

      "use that status code for success" in {
        val httpResponse = HttpResponse(PARTIAL_CONTENT, JsObject.empty, getCorrelationIdMap(isDesResponse = false))

        httpReads.read(method, url, httpResponse) shouldBe Right(ResponseWrapper(correlationId, ()))
      }
    }
  }

  "The generic HTTP parser for empty response (from DES)" when {
    "no status code is specified" must {
      val httpReads: HttpReads[BackendOutcome[DesUnit]] = implicitly

      "receiving a 204 response" should {
        "return a Right backend response with the correct correlationId and no responseData" in {
          val httpResponse = HttpResponse(NO_CONTENT, JsObject.empty, getCorrelationIdMap(isDesResponse = true))

          httpReads.read(method, url, httpResponse) shouldBe Right(ResponseWrapper(correlationId, DesUnit))
        }
      }

      handleErrorsCorrectly(httpReads, isDesResponse = true)
      handleInternalErrorsCorrectly(httpReads, isDesResponse = true)
      handleUnexpectedResponse(httpReads, isDesResponse = true)
    }

    "a success code is specified" must {
      implicit val successCode: SuccessCode = SuccessCode(PARTIAL_CONTENT)
      val httpReads: HttpReads[BackendOutcome[DesUnit]] = implicitly

      "use that status code for success" in {
        val httpResponse = HttpResponse(PARTIAL_CONTENT, JsObject.empty, getCorrelationIdMap(isDesResponse = true))

        httpReads.read(method, url, httpResponse) shouldBe Right(ResponseWrapper(correlationId, DesUnit))
      }
    }
  }

  private def handleErrorsCorrectly[A](httpReads: HttpReads[BackendOutcome[A]], isDesResponse: Boolean): Unit =
    Seq(BAD_REQUEST, NOT_FOUND, FORBIDDEN, CONFLICT).foreach(
      statusCode =>
        s"receiving a $statusCode response" should {
          "be able to parse a single error with the same status code" in {
            val httpResponse = HttpResponse(statusCode, singleErrorJson.toString(), getCorrelationIdMap(isDesResponse))

            httpReads.read(method, url, httpResponse) shouldBe Left(ResponseWrapper(correlationId, BackendErrors.single(statusCode, BackendErrorCode("CODE"))))
          }

          "be able to parse multiple errors with the same status code" in {
            val getMultipleErrorsJson: String = if (isDesResponse) multipleDesErrorsJson.toString() else multipleErrorsJson.toString()
            val httpResponse = HttpResponse(statusCode, getMultipleErrorsJson, getCorrelationIdMap(isDesResponse))

            val errors: List[BackendErrorCode] = List(BackendErrorCode("CODE"), BackendErrorCode("CODE 1"), BackendErrorCode("CODE 2"))
            val getMultipleErrorsResponse: List[BackendErrorCode] = if (isDesResponse) errors.tail else errors

            httpReads.read(method, url, httpResponse) shouldBe Left(ResponseWrapper(correlationId, BackendErrors(statusCode, getMultipleErrorsResponse)))
          }

          "return an outbound 500 DownstreamError when errors are returned without a primary error" in {
            val httpResponse = HttpResponse(statusCode, missingPrimaryErrorJson.toString(), getCorrelationIdMap(isDesResponse))

            httpReads.read(method, url, httpResponse) shouldBe Left(ResponseWrapper(correlationId, OutboundError(INTERNAL_SERVER_ERROR, DownstreamError)))
          }

          "return an outbound 500 DownstreamError when the error returned doesn't match the Error model" in {
            val httpResponse = HttpResponse(statusCode, malformedErrorJson.toString(), getCorrelationIdMap(isDesResponse))

            httpReads.read(method, url, httpResponse) shouldBe Left(ResponseWrapper(correlationId, OutboundError(INTERNAL_SERVER_ERROR, DownstreamError)))
          }
        }
    )

  private def handleInternalErrorsCorrectly[A](httpReads: HttpReads[BackendOutcome[A]], isDesResponse: Boolean): Unit =
    Seq(INTERNAL_SERVER_ERROR, SERVICE_UNAVAILABLE).foreach(responseCode =>
      s"receiving a $responseCode response" should {
        "return an outbound 500 DownstreamError when the error returned matches the Error model" in {
          val httpResponse = HttpResponse(responseCode, singleErrorJson.toString(), getCorrelationIdMap(isDesResponse))

          httpReads.read(method, url, httpResponse) shouldBe Left(ResponseWrapper(correlationId, OutboundError(INTERNAL_SERVER_ERROR, DownstreamError)))
        }

        "return an outbound 500 DownstreamError when the error returned doesn't match the Error model" in {
          val httpResponse = HttpResponse(responseCode, malformedErrorJson.toString(), getCorrelationIdMap(isDesResponse))

          httpReads.read(method, url, httpResponse) shouldBe Left(ResponseWrapper(correlationId, OutboundError(INTERNAL_SERVER_ERROR, DownstreamError)))
        }
      })

  private def handleUnexpectedResponse[A](httpReads: HttpReads[BackendOutcome[A]], isDesResponse: Boolean): Unit =
    "receiving an unexpected response" should {
      val responseCode = 499

      "return an outbound 500 DownstreamError when the error returned matches the Error model" in {
        val httpResponse = HttpResponse(responseCode, singleErrorJson.toString(), getCorrelationIdMap(isDesResponse))

        httpReads.read(method, url, httpResponse) shouldBe Left(ResponseWrapper(correlationId, OutboundError(INTERNAL_SERVER_ERROR, DownstreamError)))
      }

      "return an outbound 500 DownstreamError when the error returned doesn't match the Error model" in {
        val httpResponse = HttpResponse(responseCode, malformedErrorJson.toString(), getCorrelationIdMap(isDesResponse))

        httpReads.read(method, url, httpResponse) shouldBe Left(ResponseWrapper(correlationId, OutboundError(INTERNAL_SERVER_ERROR, DownstreamError)))
      }
    }

  private def handleBvrsCorrectly[A](httpReads: HttpReads[BackendOutcome[A]]): Unit = {

    val singleBvrJson: JsValue = Json.parse(
      """
        |{
        |   "bvrfailureResponseElement": {
        |     "validationRuleFailures": [
        |       {
        |         "id": "BVR1"
        |       },
        |       {
        |         "id": "BVR2"
        |       }
        |     ]
        |   }
        |}
      """.stripMargin)

    s"receiving a response with a bvr errors" should {
      "return an outbound BUSINESS_ERROR error containing the BVR ids" in {
        val httpResponse = HttpResponse(BAD_REQUEST, singleBvrJson.toString(), Map("CorrelationId" -> Seq(correlationId)))
        val errors: OutboundError = OutboundError(BAD_REQUEST, BVRError, Some(Seq(MtdError("BVR1", ""), MtdError("BVR2", ""))))

        httpReads.read(method, url, httpResponse) shouldBe Left(ResponseWrapper(correlationId, errors))
      }
    }
  }
}
