/*
 * Copyright 2021 HM Revenue & Customs
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

package v1.fixtures.getIncomeTaxAndNics.detail

import play.api.libs.json.{JsValue, Json}
import v1.models.response.getIncomeTaxAndNics.detail.TaxDeductedAtSource

object TaxDeductedAtSourceFixture {

  val ukLandAndProperty: Option[BigDecimal] = Some(130.45)
  val savings: Option[BigDecimal] = Some(200.45)
  val cis: Option[BigDecimal] = Some(130.45)
  val securities: Option[BigDecimal] = Some(200.45)
  val voidedIsa: Option[BigDecimal] = Some(300.45)
  val payeEmployments: Option[BigDecimal] = Some(400.45)
  val occupationalPensions: Option[BigDecimal] = Some(500.45)
  val stateBenefits: Option[BigDecimal] = Some(600.45)

  val taxDeductedAtSourceModel: TaxDeductedAtSource =
    TaxDeductedAtSource(
      ukLandAndProperty = ukLandAndProperty,
      savings = savings,
      cis = cis,
      securities = securities,
      voidedIsa = voidedIsa,
      payeEmployments = payeEmployments,
      occupationalPensions = occupationalPensions,
      stateBenefits = stateBenefits
    )

  val taxDeductedAtSourceJson: JsValue = Json.parse(
    s"""
       |{
       |   "ukLandAndProperty" : ${ukLandAndProperty.get},
       |   "savings" : ${savings.get},
       |   "cis" : ${cis.get},
       |   "securities" : ${securities.get},
       |   "voidedIsa" : ${voidedIsa.get},
       |   "payeEmployments" : ${payeEmployments.get},
       |   "occupationalPensions" : ${occupationalPensions.get},
       |   "stateBenefits" : ${stateBenefits.get}
       |}
  """.stripMargin
  )
}