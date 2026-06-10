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

package api.connectors.httpparsers

import api.models.errors.*
import api.utils.Logging
import play.api.libs.json.*
import uk.gov.hmrc.http.HttpResponse

import scala.util.{Failure, Success, Try}

trait HttpParser extends Logging {

  implicit class JsonResponseHelper(response: HttpResponse) {

    private lazy val jsonTry: Try[JsValue] = Try(response.json)

    def validateJson[T](implicit reads: Reads[T]): Option[T] = jsonTry.toOption.flatMap(_.asOpt)

    def validateJsonWithLogging[T](implicit reads: Reads[T]): Option[T] =
      jsonTry match {
        case Success(json) =>
          json
            .validate[T]
            .fold(
              errors => {
                logger.warn(s"[JsonResponseHelper][validateJsonWithLogging] JSON validation failed: $errors")
                None
              },
              value => Some(value)
            )
        case Failure(_) =>
          logger.warn("[JsonResponseHelper][validateJsonWithLogging] Response body is not valid JSON")
          None
      }

  }

  def retrieveCorrelationId(response: HttpResponse): String = response.header("CorrelationId").getOrElse("")

  private val multipleErrorReads: Reads[Seq[DownstreamErrorCode]] = (__ \ "failures").read[Seq[DownstreamErrorCode]]

  private val bvrErrorReads: Reads[Seq[DownstreamErrorCode]] = {
    implicit val errorIdReads: Reads[DownstreamErrorCode] = (__ \ "id").read[String].map(DownstreamErrorCode(_))
    (__ \ "bvrfailureResponseElement" \ "validationRuleFailures").read[Seq[DownstreamErrorCode]]
  }

  private val multipleTopLevelErrorCodesReads: Reads[Seq[DownstreamErrorCode]] =
    __.read[Seq[JsObject]].map(_.map(obj => DownstreamErrorCode((obj \ "errorCode").as[String])))

  private val multipleErrorCodesInResponseReads: Reads[Seq[DownstreamErrorCode]] =
    (__ \ "response").read[Seq[JsObject]].map(_.map(obj => DownstreamErrorCode((obj \ "errorCode").as[String])))

  private val multipleFailureErrorTypesReads: Reads[Seq[DownstreamErrorCode]] =
    (__ \ "response" \ "failures").read[Seq[JsObject]].map(_.map(obj => DownstreamErrorCode((obj \ "type").as[String])))

  def parseErrors(response: HttpResponse): DownstreamError = {

    val wrappedResponse: JsonResponseHelper = new JsonResponseHelper(response)

    val singleError =
      wrappedResponse
        .validateJson[DownstreamErrorCode]
        .map(err => DownstreamErrors(List(err)))

    lazy val multipleErrors =
      wrappedResponse
        .validateJson(multipleErrorReads)
        .map(errs => DownstreamErrors(errs))

    lazy val multipleTopLevelErrorCodes = wrappedResponse.validateJson(multipleTopLevelErrorCodesReads).map(errs => DownstreamErrors(errs))

    lazy val multipleErrorCodesInResponse = wrappedResponse.validateJson(multipleErrorCodesInResponseReads).map(errs => DownstreamErrors(errs))

    lazy val multipleFailureErrorTypes =
      wrappedResponse
        .validateJson(multipleFailureErrorTypesReads)
        .map(errs => DownstreamErrors(errs))

    lazy val bvrErrors =
      wrappedResponse
        .validateJson(bvrErrorReads)
        .map(errs => OutboundError(BVRError, Some(errs.map(_.toMtd(BVRError.httpStatus)))))

    lazy val unableToParseJsonError = {
      logger.warn(s"unable to parse errors from response: ${response.body}")
      OutboundError(InternalError)
    }

    singleError orElse
      multipleErrors orElse
      multipleTopLevelErrorCodes orElse
      multipleErrorCodesInResponse orElse
      multipleFailureErrorTypes orElse
      bvrErrors getOrElse unableToParseJsonError
  }

}
