/*
 * Copyright 2026 HM Revenue & Customs
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

package v9.createAmendProvisionalFiguresInformation

import api.controllers.validators.Validator
import api.controllers.validators.resolvers.{ResolveDetailedTaxYear, ResolveNino}
import api.models.domain.TaxYear
import api.models.errors.MtdError
import cats.data.Validated
import cats.implicits.catsSyntaxTuple3Semigroupal
import play.api.libs.json.JsValue
import v9.common.model.resolver.ResolveCreateAmendProvisionalFiguresInformationBody
import v9.createAmendProvisionalFiguresInformation.model.request.{
  CreateAmendProvisionalFiguresInformationRequestBody,
  CreateAmendProvisionalFiguresInformationRequestData
}

import scala.language.postfixOps

class CreateAmendProvisionalFiguresInformationValidator(nino: String, taxYear: String, body: Option[JsValue], temporalValidationEnabled: Boolean)
    extends Validator[CreateAmendProvisionalFiguresInformationRequestData] {

  private val minimumTaxYear: TaxYear = TaxYear.fromMtd("2026-27")

  private val resolveTaxYear =
    ResolveDetailedTaxYear(minimumTaxYear = minimumTaxYear, allowIncompleteTaxYear = !temporalValidationEnabled)

  def validate: Validated[Seq[MtdError], CreateAmendProvisionalFiguresInformationRequestData] =
    (
      ResolveNino(nino),
      resolveTaxYear(taxYear),
      ResolveCreateAmendProvisionalFiguresInformationBody.apply(body)
    ).mapN(CreateAmendProvisionalFiguresInformationRequestData.apply)

}
