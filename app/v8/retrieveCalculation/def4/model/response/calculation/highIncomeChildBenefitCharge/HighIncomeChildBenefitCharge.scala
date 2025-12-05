/*
 * Copyright 2025 HM Revenue & Customs
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

package v8.retrieveCalculation.def4.model.response.calculation.highIncomeChildBenefitCharge

import play.api.libs.functional.syntax.toFunctionalBuilderOps
import play.api.libs.json.*

case class HighIncomeChildBenefitCharge(adjustedNetIncome: BigDecimal,
                                        amountOfChildBenefitReceived: BigDecimal,
                                        incomeThreshold: BigDecimal,
                                        childBenefitChargeTaper: BigDecimal,
                                        rate: BigDecimal,
                                        highIncomeBenefitCharge: BigDecimal)

object HighIncomeChildBenefitCharge {

  given Reads[HighIncomeChildBenefitCharge] = (
    (JsPath \ "adjustedNetIncome").read[BigDecimal] and
      (JsPath \ "amountOfChildBenefitReceived").read[BigDecimal] and
      (JsPath \ "incomeThreshold").read[BigDecimal] and
      (JsPath \ "childBenefitChargeTaper").read[BigDecimal] and
      (JsPath \ "rate").read[BigDecimal] and
      (JsPath \ "highIncomeChildBenefitCharge").read[BigDecimal]
  )(HighIncomeChildBenefitCharge.apply)

  given OWrites[HighIncomeChildBenefitCharge] = Json.writes[HighIncomeChildBenefitCharge]
}
