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

package v1.handling

import play.api.libs.json.{ JsValue, Writes }
import v1.models.audit.{ AuditError, AuditEvent }

case class AuditHandling[Details](auditType: String,
                                  transactionName: String,
                                  successEventFactory: (String, Int, Option[JsValue]) => Details,
                                  failureEventFactory: (String, Int, Seq[AuditError]) => Details)(implicit val writes: Writes[Details]) {

  def successEvent(correlationId: String, status: Int, response: Option[JsValue]): AuditEvent[Details] =
    AuditEvent(auditType, transactionName, successEventFactory(correlationId, status, response))

  def failureEvent(correlationId: String, status: Int, errors: Seq[AuditError]): AuditEvent[Details] =
    AuditEvent(auditType, transactionName, failureEventFactory(correlationId, status, errors))
}
