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

package v6.retrieveCalculation.def1.model.response.calculation.employmentAndPensionsIncome

import play.api.libs.json.{Format, Json}

case class BenefitFromEmployerFinancedRetirementScheme(amount: Option[BigDecimal],
                                                            exemptAmount: Option[BigDecimal],
                                                            taxPaid: Option[BigDecimal],
                                                            taxTakenOffInEmployment: Option[Boolean])

object BenefitFromEmployerFinancedRetirementScheme {

  implicit val format: Format[BenefitFromEmployerFinancedRetirementScheme] = Json.format[BenefitFromEmployerFinancedRetirementScheme]
}
