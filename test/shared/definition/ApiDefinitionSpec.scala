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

package shared.definition

import shared.routing.Version3
import shared.utils.UnitSpec
import play.api.libs.json.Json

class ApiDefinitionSpec extends UnitSpec {

  private val apiVersion: APIVersion       = APIVersion(Version3, APIStatus.ALPHA, endpointsEnabled = true)
  private val apiDefinition: APIDefinition = APIDefinition("b", "c", "d", List("category"), List(apiVersion), Some(false))

  private val apiVersionJson = Json.parse("""
        {
          "version": "3.0",
          "status": "ALPHA",
          "endpointsEnabled": true
        }
      """)

  private val apiDefinitionJson = Json.parse("""
        {
          "name": "b",
          "description": "c",
          "context": "d",
          "categories": ["category"],
          "versions": [
            {
              "version": "3.0",
              "status": "ALPHA",
              "endpointsEnabled": true
            }
          ],
          "requiresTrust": false
        }
      """)

  private val definitionJson = Json.parse("""
        {
          "api": {
            "name": "b",
            "description": "c",
            "context": "d",
            "categories": ["category"],
            "versions": [
              {
                "version": "3.0",
                "status": "ALPHA",
                "endpointsEnabled": true
              }
            ],
            "requiresTrust": false
          }
        }
      """)

  "APIDefinition" when {
    "the 'name' parameter is empty" should {
      "throw an 'IllegalArgumentException'" in {
        assertThrows[IllegalArgumentException](
          apiDefinition.copy(name = "")
        )
      }
    }
  }

  "the 'description' parameter is empty" should {
    "throw an 'IllegalArgumentException'" in {
      assertThrows[IllegalArgumentException](
        apiDefinition.copy(description = "")
      )
    }
  }

  "the 'context' parameter is empty" should {
    "throw an 'IllegalArgumentException'" in {
      assertThrows[IllegalArgumentException](
        apiDefinition.copy(context = "")
      )
    }
  }

  "the 'versions' parameter is not unique" should {
    "throw an 'IllegalArgumentException'" in {
      assertThrows[IllegalArgumentException](
        apiDefinition.copy(versions = List(apiVersion, apiVersion))
      )
    }
  }

  "the 'versions' parameter is empty" should {
    "throw an 'IllegalArgumentException'" in {
      assertThrows[IllegalArgumentException](
        apiDefinition.copy(versions = Nil)
      )
    }
  }

  "APIVersion" should {
    "deserialise to model" in {
      apiVersionJson.as[APIVersion] shouldBe apiVersion
    }

    "serialise to JSON" in {
      Json.toJson(apiVersion) shouldBe apiVersionJson
    }
  }

  "APIDefinition" should {
    "deserialise to model" in {
      apiDefinitionJson.as[APIDefinition] shouldBe apiDefinition
    }

    "serialise to JSON" in {
      Json.toJson(apiDefinition) shouldBe apiDefinitionJson
    }
  }

  "Definition" should {
    val definition = Definition(apiDefinition)

    "deserialise to model" in {
      definitionJson.as[Definition] shouldBe definition
    }

    "serialise to JSON" in {
      Json.toJson(definition) shouldBe definitionJson
    }
  }

}
