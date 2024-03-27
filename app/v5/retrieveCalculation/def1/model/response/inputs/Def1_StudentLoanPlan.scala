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

package v5.retrieveCalculation.def1.model.response.inputs

import play.api.libs.json.{JsPath, Json, OWrites, Reads}
import v5.retrieveCalculation.def1.model.response.common.Def1_StudentLoanPlanType

case class Def1_StudentLoanPlan(planType: Def1_StudentLoanPlanType)

object Def1_StudentLoanPlan {
  implicit val writes: OWrites[Def1_StudentLoanPlan] = Json.writes[Def1_StudentLoanPlan]
  implicit val reads: Reads[Def1_StudentLoanPlan]    = (JsPath \ "planType").read[Def1_StudentLoanPlanType].map(Def1_StudentLoanPlan(_))
}
