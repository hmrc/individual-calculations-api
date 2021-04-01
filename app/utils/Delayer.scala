
package utils

import akka.actor.Scheduler

import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.concurrent.duration.FiniteDuration

trait Delayer {

  implicit val scheduler: Scheduler
  implicit val ec: ExecutionContext

  def delay(delay: FiniteDuration): Future[Unit] = {
    val promise = Promise[Unit]

    scheduler.scheduleOnce(delay)(promise.success(()))

    promise.future
  }
}
