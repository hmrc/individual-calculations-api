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

package v3.controllers

import cats.syntax.either._
import play.api.libs.json.JsValue
import v3.models.audit.{AuditEvent, AuditResponse, GenericAuditDetail}
import v3.models.auth.UserDetails
import v3.models.errors.ErrorWrapper
import v3.services.AuditService

import scala.concurrent.ExecutionContext

object AuditHandler {

  def apply(auditService: AuditService,
            auditType: String,
            transactionName: String,
            params: Map[String, String],
            requestBody: Option[JsValue] = None,
            includeResponse: Boolean = false): AuditHandler = new AuditHandler(
    auditService = auditService,
    auditType = auditType,
    transactionName = transactionName,
    params = params,
    requestBody = requestBody,
    responseBodyMap = if (includeResponse) identity else _ => None
  )

}

case class AuditHandler(auditService: AuditService,
                        auditType: String,
                        transactionName: String,
                        params: Map[String, String],
                        requestBody: Option[JsValue],
                        responseBodyMap: Option[JsValue] => Option[JsValue])
    extends RequestContextImplicits {

  def performAudit(userDetails: UserDetails, httpStatus: Int, response: Either[ErrorWrapper, Option[JsValue]])(implicit
      ctx: RequestContext,
      ec: ExecutionContext): Unit = {
    val auditEvent = {
      val auditResponse = AuditResponse(httpStatus, response.map(responseBodyMap).leftMap(ew => ew.auditErrors))

      val detail = GenericAuditDetail(
        userDetails = userDetails,
        params = params,
        requestBody = requestBody,
        `X-CorrelationId` = ctx.correlationId,
        auditResponse = auditResponse
      )

      AuditEvent(auditType, transactionName, detail)
    }

    auditService.auditEvent(auditEvent)
  }

}
