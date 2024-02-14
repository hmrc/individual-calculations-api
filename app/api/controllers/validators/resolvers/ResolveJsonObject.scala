/*
 * Copyright 2023 HM Revenue & Customs
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

package api.controllers.validators.resolvers

import api.models.errors.{MtdError, RuleIncorrectOrEmptyBodyError}
import cats.data.Validated
import cats.data.Validated.{Invalid, Valid}
import play.api.libs.json._
import utils.Logging

class ResolveJsonObject[T](implicit val reads: Reads[T]) extends ResolverSupport with Logging {

  val resolver: Resolver[JsValue, T] = {
    case jsObj: JsObject if jsObj.fields.isEmpty =>
      Invalid(List(RuleIncorrectOrEmptyBodyError))

    case jsObj: JsObject =>
      jsObj.validate[T] match {
        case JsSuccess(parsed, _) => Valid(parsed)
        case JsError(errors) =>
          val immutableErrors = errors.map { case (path, errors) => (path, errors.toList) }.toList
          Invalid(handleErrors(immutableErrors))
      }

    case _ =>
      Invalid(List(RuleIncorrectOrEmptyBodyError))
  }

  def apply(data: JsValue): Validated[Seq[MtdError], T] = resolver(data)

  private def handleErrors(errors: Seq[(JsPath, Seq[JsonValidationError])]): Seq[MtdError] = {
    val failures = errors.map {

      case (path: JsPath, Seq(JsonValidationError(Seq("error.path.missing")))) =>
        MissingMandatoryField(path)

      case (path: JsPath, Seq(JsonValidationError(Seq(error: String)))) if error.contains("error.expected") =>
        WrongFieldType(path)

      case (path: JsPath, _) =>
        OtherFailure(path)
    }

    val logString = failures
      .groupBy(_.getClass)
      .values
      .map(failure => s"${failure.head.failureReason}: " + s"${failure.map(_.fromJsPath)}")
      .toString()
      .dropRight(1)
      .drop(5)

    logger.warn(s"Request body failed validation with errors - $logString")
    List(RuleIncorrectOrEmptyBodyError.withPaths(failures.map(_.fromJsPath).sorted))
  }

  private class JsonFormatValidationFailure(path: JsPath, failure: String) {
    val failureReason: String = failure

    def fromJsPath: String =
      path.toString
        .replace("(", "/")
        .replace(")", "")

  }

  private case class MissingMandatoryField(path: JsPath) extends JsonFormatValidationFailure(path, "Missing mandatory field")
  private case class WrongFieldType(path: JsPath)        extends JsonFormatValidationFailure(path, "Wrong field type")
  private case class OtherFailure(path: JsPath)          extends JsonFormatValidationFailure(path, "Other failure")
}
