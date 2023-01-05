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

package v3.models.audit

import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, JsValue, OWrites}
import v3.models.auth.UserDetails

case class GenericAuditDetail(userType: String,
                              agentReferenceNumber: Option[String],
                              pathParams: Map[String, String],
                              requestBody: Option[JsValue],
                              `X-CorrelationId`: String,
                              versionNumber: String,
                              response: String,
                              httpStatusCode: Int,
                              calculationId: Option[String],
                              errorCodes: Option[Seq[String]])

object GenericAuditDetail {

  implicit val writes: OWrites[GenericAuditDetail] = (
    (JsPath \ "userType").write[String] and
      (JsPath \ "agentReferenceNumber").writeNullable[String] and
      JsPath.write[Map[String, String]] and
      (JsPath \ "request").writeNullable[JsValue] and
      (JsPath \ "X-CorrelationId").write[String] and
      (JsPath \ "versionNumber").write[String] and
      (JsPath \ "response").write[String] and
      (JsPath \ "httpStatusCode").write[Int] and
      (JsPath \ "calculationId").writeNullable[String] and
      (JsPath \ "errorCodes").writeNullable[Seq[String]]
  )(unlift(GenericAuditDetail.unapply))

  def apply(userDetails: UserDetails,
            pathParams: Map[String, String],
            requestBody: Option[JsValue],
            `X-CorrelationId`: String,
            auditResponse: AuditResponse): GenericAuditDetail = {

    val resOutcome = if (auditResponse.errors.exists(x => x.nonEmpty)) { "error" }
    else { "success" }
    val resData: Option[String] = auditResponse.body match {
      case Some(value) => (value \ "calculationId").asOpt[String]
      case _           => None
    }
    val errorCodes: Option[Seq[String]] = auditResponse.errors.flatMap {
      case Nil        => None
      case err :: Nil => Some(Seq(err.errorCode))
      case errs       => Some(errs.map(err => err.errorCode))
    }

    GenericAuditDetail(
      userType = userDetails.userType,
      agentReferenceNumber = userDetails.agentReferenceNumber,
      pathParams = pathParams,
      requestBody = requestBody,
      `X-CorrelationId` = `X-CorrelationId`,
      versionNumber = "3.0",
      response = resOutcome,
      httpStatusCode = auditResponse.httpStatus,
      calculationId = resData,
      errorCodes = errorCodes
    )
  }

}
