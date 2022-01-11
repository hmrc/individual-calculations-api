/*
 * Copyright 2022 HM Revenue & Customs
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

package v2.models.response.getTaxableIncome.detail

import play.api.libs.json._
import v2.models.response.getTaxableIncome.detail.eeaPropertyFhl.EeaPropertyFhl
import v2.models.response.getTaxableIncome.detail.foreignProperty.ForeignProperty
import v2.models.response.getTaxableIncome.detail.selfEmployment.SelfEmployment
import v2.models.response.getTaxableIncome.detail.ukPropertyFhl.UkPropertyFhl
import v2.models.response.getTaxableIncome.detail.ukPropertyNonFhl.UkPropertyNonFhl

case class BusinessProfitAndLoss(selfEmployments: Option[Seq[SelfEmployment]],
                                 ukPropertyFhl: Option[UkPropertyFhl],
                                 ukPropertyNonFhl: Option[UkPropertyNonFhl],
                                 eeaPropertyFhl: Option[EeaPropertyFhl],
                                 foreignProperty: Option[ForeignProperty])

object BusinessProfitAndLoss {
  implicit val format: OFormat[BusinessProfitAndLoss] = Json.format[BusinessProfitAndLoss]
}