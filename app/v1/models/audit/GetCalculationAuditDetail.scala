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

package v1.models.audit

import play.api.libs.json.{JsValue, Json, OWrites, Writes}
import v1.models.auth.UserDetails

case class GetCalculationAuditDetail(userType: String,
                                     agentReferenceNumber: Option[String],
                                     nino: String,
                                     calculationId: String,
                                     `X-CorrelationId`: String,
                                     response: AuditResponse)

object GetCalculationAuditDetail {
  implicit val writes: OWrites[GetCalculationAuditDetail] = Json.writes[GetCalculationAuditDetail]

  def apply(userDetails: UserDetails,
            nino: String,
            calculationId: String,
            request: JsValue,
            `X-CorrelationId`: String,
            auditResponse: AuditResponse): GetCalculationAuditDetail = {

    GetCalculationAuditDetail(
      userType = userDetails.userType,
      agentReferenceNumber = userDetails.agentReferenceNumber,
      nino = nino,
      calculationId = calculationId,
      `X-CorrelationId`,
      auditResponse
    )
  }
}