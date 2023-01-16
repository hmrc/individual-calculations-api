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

package support

trait V3IntegrationBaseSpec extends IntegrationBaseSpec {

  override def servicesConfig: Map[String, String] = Map(
    "microservice.services.des.host"                     -> mockHost,
    "microservice.services.des.port"                     -> mockPort,
    "microservice.services.ifs.host"                     -> mockHost,
    "microservice.services.ifs.port"                     -> mockPort,
    "microservice.services.tys-ifs.host"                 -> mockHost,
    "microservice.services.tys-ifs.port"                 -> mockPort,
    "microservice.services.individual-calculations.host" -> mockHost,
    "microservice.services.individual-calculations.port" -> mockPort,
    "microservice.services.mtd-id-lookup.host"           -> mockHost,
    "microservice.services.mtd-id-lookup.port"           -> mockPort,
    "microservice.services.auth.host"                    -> mockHost,
    "microservice.services.auth.port"                    -> mockPort,
    "auditing.consumer.baseUri.port"                     -> mockPort,
    "feature-switch.version-2.enabled"                   -> "false",
    "feature-switch.version-3.enabled"                   -> "true"
  )

}
