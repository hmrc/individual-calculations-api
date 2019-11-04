/*
 * Copyright 2019 HM Revenue & Customs
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

package v1.handler

import play.api.libs.json.{JsValue, Writes}
import play.api.mvc.AnyContent
import v1.controllers.UserRequest
import v1.models.audit.{AuditEvent, AuditResponse, GenericAuditDetail}

case class AuditHandler[Details](auditType: String,
                                 transactionName: String,
                                 detailFactory: (String, AuditResponse) => Details)(implicit val writes: Writes[Details]) {

  def event(correlationId: String, auditResponse: AuditResponse): AuditEvent[Details] =
    AuditEvent(auditType, transactionName, detailFactory(correlationId, auditResponse))
}

object AuditHandler {

  def withBody(auditType: String,
               transactionName: String,
               pathParams: Map[String, String],
               request: UserRequest[JsValue]): AuditHandler[GenericAuditDetail] = AuditHandler(
    auditType,
    transactionName,
    detailFactory = (correlationId: String, auditResponse: AuditResponse) =>
      GenericAuditDetail(request.userDetails,
        pathParams,
        Some(request.body),
        correlationId,
        auditResponse)
  )

  def withoutBody(auditType: String,
               transactionName: String,
               pathParams: Map[String, String],
               request: UserRequest[AnyContent]): AuditHandler[GenericAuditDetail] = AuditHandler(
    auditType,
    transactionName,
    detailFactory = (correlationId: String, auditResponse: AuditResponse) =>
      GenericAuditDetail(request.userDetails,
        pathParams,
        None,
        correlationId,
        auditResponse)
  )
}
