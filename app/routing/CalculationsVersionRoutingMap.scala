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

package routing

import play.api.routing.Router
import shared.routing.*

import javax.inject.{Inject, Singleton}

// Add routes corresponding to available versions...
@Singleton
case class CalculationsVersionRoutingMap @Inject() (defaultRouter: Router, v7Router: v7.Routes, v8Router: v8.Routes) extends VersionRoutingMap {

  val map: Map[Version, Router] = Map(
    Version7 -> v7Router,
    Version8 -> v8Router
  )

}
