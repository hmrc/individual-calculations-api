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

package shared.utils.enums

import scala.deriving.*
import scala.compiletime.*

object Values:

  trait MkValues[E]:
    def values: List[E]

  object MkValues:

    inline def apply[E](using m: Mirror.SumOf[E]): MkValues[E] = new MkValues[E]:
      def values: List[E] = enumValues[E, m.MirroredElemTypes]

    private inline def enumValues[E, Elems <: Tuple]: List[E] =
      val size = constValue[Tuple.Size[Elems]]
      List.tabulate(size)(i => fromOrdinal[E](i))

    private inline def fromOrdinal[E](ordinal: Int): E =
      // Requires E to have a `fromOrdinal` method (only true for `enum`)
      ${ fromOrdinalImpl[E]('ordinal) }

  import scala.quoted.*

  private def fromOrdinalImpl[E: Type](ordinalExpr: Expr[Int])(using Quotes): Expr[E] =
    import quotes.reflect.*
    val tpe = TypeRepr.of[E]
    val companion = tpe.typeSymbol.companionModule
    val fromOrdinalSym = companion.methodMember("fromOrdinal").head
    val fromOrdinal = Select(Ref(companion), fromOrdinalSym)
    Apply(fromOrdinal, List(ordinalExpr.asTerm)).asExprOf[E]
