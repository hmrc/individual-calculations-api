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

package common.utils

import com.github.pjfanning.pekko.scheduler.mock.{MockScheduler, VirtualTime}
import shared.utils.UnitSpec

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

class DelayerSpec extends UnitSpec {

  val virtualTime                           = new VirtualTime
  implicit val mockScheduler: MockScheduler = virtualTime.scheduler

  val delayer: Delayer = new Delayer {
    override implicit val scheduler: MockScheduler = mockScheduler
    override implicit val ec: ExecutionContext     = ExecutionContext.global
  }

  "delayer" must {
    "delay  by that amount" in {
      val delay = 10.seconds
      val f     = delayer.delay(delay)
      f.isCompleted shouldBe false

      virtualTime.advance(9.seconds)
      f.isCompleted shouldBe false

      virtualTime.advance(1.second)
      f.isCompleted shouldBe true
    }
  }

}
