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

package v7.listCalculations.model.request

import shared.models.domain.{Nino, TaxYear}
import v7.common.model.domain.CalculationType
import v7.listCalculations.schema.ListCalculationsSchema

sealed trait ListCalculationsRequestData {
  val nino: Nino
  val taxYear: TaxYear
  val calculationType: Option[CalculationType]

  val schema: ListCalculationsSchema
}

case class Def1_ListCalculationsRequestData(nino: Nino, taxYear: TaxYear, calculationType: Option[CalculationType]) extends ListCalculationsRequestData{
  override val schema: ListCalculationsSchema = ListCalculationsSchema.Def1
}

case class Def2_ListCalculationsRequestData(nino: Nino, taxYear: TaxYear, calculationType: Option[CalculationType]) extends ListCalculationsRequestData{
  override val schema: ListCalculationsSchema = ListCalculationsSchema.Def2
}

case class Def3_ListCalculationsRequestData(nino: Nino, taxYear: TaxYear, calculationType: Option[CalculationType]) extends ListCalculationsRequestData{
  override val schema: ListCalculationsSchema = ListCalculationsSchema.Def3
}
