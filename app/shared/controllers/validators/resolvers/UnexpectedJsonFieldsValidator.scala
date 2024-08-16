/*
 * Copyright 2024 HM Revenue & Customs
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

package shared.controllers.validators.resolvers

import play.api.libs.json.{JsArray, JsObject, JsValue}
import shapeless.labelled.FieldType
import shapeless.{::, HList, HNil, LabelledGeneric, Lazy, Witness}
import shared.models.errors.RuleIncorrectOrEmptyBodyError
import shared.utils.Logging
import UnexpectedJsonFieldsValidator.SchemaStructureSource
import shared.models.domain.TaxYear

class UnexpectedJsonFieldsValidator[A](implicit extraPathChecker: SchemaStructureSource[A]) extends ResolverSupport with Logging {
  import UnexpectedJsonFieldsValidator.SchemaStructure

  def validator: Validator[(JsObject, A)] = { case (inputJson, data) =>
    val expectedJson = extraPathChecker.schemaStructureOf(data)

    findExtraPaths(acc = Nil, path = "", in = inputJson, expected = expectedJson) match {
      case Nil => None
      case paths =>
        logger.warn(s"Request body failed validation with errors - Unexpected fields: $paths")
        Some(Seq(RuleIncorrectOrEmptyBodyError.withPaths(paths)))
    }
  }

  private def findExtraPaths(acc: List[String], path: String, in: JsValue, expected: SchemaStructure): List[String] =
    (in, expected) match {
      case (in: JsObject, expected: SchemaStructure.Obj) => handleObjects(acc, path, in, expected)
      case (in: JsArray, expected: SchemaStructure.Arr)  => handleArrays(acc, path, in, expected)
      case _                                             => acc
    }

  private def handleObjects(acc: List[String], path: String, in: JsObject, expected: SchemaStructure.Obj) = {
    val extraPathsInThisObject = {
      val extraFields = in.value.toMap -- expected.keys
      extraFields.map { case (name, _) => s"$path/$name" }.toList
    }

    expected.fields.foldLeft(acc ++ extraPathsInThisObject) { case (acc, (fieldName, expectedField)) =>
      in.value.get(fieldName) match {
        case Some(inField) => findExtraPaths(acc, s"$path/$fieldName", inField, expectedField)
        case None          => acc
      }
    }
  }

  private def handleArrays(acc: List[String], path: String, in: JsArray, expected: SchemaStructure.Arr) = {
    val zippedArrayItems = (in.value.zipWithIndex zip expected.items).toList

    zippedArrayItems.foldLeft(acc) { case (acc, ((inItem, index), expectedItem)) =>
      findExtraPaths(acc, s"$path/$index", inItem, expectedItem)
    }
  }

}

object UnexpectedJsonFieldsValidator extends ResolverSupport {
  def validator[A](implicit extraPathChecker: SchemaStructureSource[A]): Validator[(JsObject, A)] = new UnexpectedJsonFieldsValidator[A].validator

  sealed trait SchemaStructure

  private[UnexpectedJsonFieldsValidator] object SchemaStructure {

    case class Obj(fields: List[(String, SchemaStructure)]) extends SchemaStructure {
      def keys: Set[String] = fields.map(_._1).toSet
    }

    case class Arr(items: Seq[SchemaStructure]) extends SchemaStructure
    case object Leaf                            extends SchemaStructure
  }

  trait SchemaStructureSource[A] {
    def schemaStructureOf(value: A): SchemaStructure
  }

  // Internal specialization of SchemaStructureSource for object instances so we can directly access its fields
  private[UnexpectedJsonFieldsValidator] trait ObjSchemaStructureSource[A] extends SchemaStructureSource[A] {
    def schemaStructureOf(value: A): SchemaStructure.Obj
  }

  object SchemaStructureSource {

    def apply[A](implicit aInstance: SchemaStructureSource[A]): SchemaStructureSource[A] = aInstance

    def instance[A](func: A => SchemaStructure): SchemaStructureSource[A] = (value: A) => func(value)

    private def instanceObj[A](func: A => SchemaStructure.Obj): ObjSchemaStructureSource[A] = (value: A) => func(value)

    def leaf[A]: SchemaStructureSource[A] = SchemaStructureSource.instance(_ => SchemaStructure.Leaf)

    implicit val stringInstance: SchemaStructureSource[String]   = instance(_ => SchemaStructure.Leaf)
    implicit val intInstance: SchemaStructureSource[Int]         = instance(_ => SchemaStructure.Leaf)
    implicit val doubleInstance: SchemaStructureSource[Double]   = instance(_ => SchemaStructure.Leaf)
    implicit val booleanInstance: SchemaStructureSource[Boolean] = instance(_ => SchemaStructure.Leaf)

    implicit val bigIntInstance: SchemaStructureSource[BigInt]         = instance(_ => SchemaStructure.Leaf)
    implicit val bigDecimalInstance: SchemaStructureSource[BigDecimal] = instance(_ => SchemaStructure.Leaf)
    implicit val taxYearInstance: SchemaStructureSource[TaxYear]       = instance(_ => SchemaStructure.Leaf)

    implicit def optionInstance[A](implicit aInstance: SchemaStructureSource[A]): SchemaStructureSource[Option[A]] =
      instance(opt => opt.map(aInstance.schemaStructureOf).getOrElse(SchemaStructure.Leaf))

    implicit def seqInstance[A, I](implicit aInstance: SchemaStructureSource[A]): SchemaStructureSource[Seq[A]] =
      instance(list => SchemaStructure.Arr(list.map(aInstance.schemaStructureOf)))

    implicit def listInstance[A](implicit aInstance: SchemaStructureSource[A]): SchemaStructureSource[List[A]] =
      instance(list => SchemaStructure.Arr(list.map(aInstance.schemaStructureOf)))

    implicit val hnilInstance: ObjSchemaStructureSource[HNil] = instanceObj(_ => SchemaStructure.Obj(Nil))

    implicit def hlistInstance[K <: Symbol, H, T <: HList](implicit
        witness: Witness.Aux[K],
        hInstance: Lazy[SchemaStructureSource[H]],
        tInstance: ObjSchemaStructureSource[T]
    ): ObjSchemaStructureSource[FieldType[K, H] :: T] =
      instanceObj { case h :: t =>
        val hField  = witness.value.name -> hInstance.value.schemaStructureOf(h)
        val tFields = tInstance.schemaStructureOf(t).fields
        SchemaStructure.Obj(hField :: tFields)
      }

    implicit def genericInstance[A, R](implicit
        gen: LabelledGeneric.Aux[A, R],
        enc: Lazy[SchemaStructureSource[R]]
    ): SchemaStructureSource[A] =
      instance(a => enc.value.schemaStructureOf(gen.to(a)))

  }

}
