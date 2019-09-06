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

package v1.models.request

import uk.gov.hmrc.domain.Nino

case class GetCalculationRawData(nino: String, calculationId: String) extends RawData

case class GetCalculationRequest(nino: Nino, calculationId: String)

case class GetCalculationMessagesRawData(nino: String, calculationId: String, queryData: Seq[String]) extends RawData

case class GetCalculationMessagesRequest(nino: Nino, calculationId: String, queryData: Seq[MessageType])
