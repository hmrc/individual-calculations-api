/*
 * Copyright 2019 HM Revenue & Customs
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

package v1.models.hateoas

object RelType {
  val DELETE_BF_LOSS = "delete-brought-forward-loss"
  val AMEND_BF_LOSS = "amend-brought-forward-loss"
  val CREATE_BF_LOSS = "create-brought-forward-loss"

  val AMEND_LOSS_CLAIM = "amend-loss-claim"
  val DELETE_LOSS_CLAIM = "delete-loss-claim"
  val CREATE_LOSS_CLAIM = "create-loss-claim"

  val SELF = "self"
}
