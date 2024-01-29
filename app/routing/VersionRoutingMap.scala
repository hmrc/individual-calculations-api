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

package routing

import com.google.inject.ImplementedBy
import play.api.routing.Router

import javax.inject.Inject

// So that we can have API-independent implementations of
// VersionRoutingRequestHandler and VersionRoutingRequestHandlerSpec
// implement this for the specific API...
@ImplementedBy(classOf[VersionRoutingMapImpl])
trait VersionRoutingMap {
  val defaultRouter: Router

  val map: Map[Version, Router]

  final def versionRouter(version: Version): Option[Router] = map.get(version)
}

// Add routes corresponding to available versions...
case class VersionRoutingMapImpl @Inject() (defaultRouter: Router, v3Router: v3.Routes, v4Router: v4.Routes, v5Router: v5.Routes)
    extends VersionRoutingMap {

  val map: Map[Version, Router] = Map(
    Version3 -> v3Router,
    Version4 -> v4Router,
    Version5 -> v5Router
  )

}
