package com.github.t3hnar.akkax

import akka.actor.{ Cancellable, ActorLogging, Actor }
import org.joda.time._
import scala.concurrent.duration.FiniteDuration
import java.util.concurrent.TimeUnit

trait PeriodicJob {
  this: Actor with IgnoreIfBusy with ActorLogging =>

  import context._

  var scheduler: Option[Cancellable] = None

  /** Override to get the last time this kind of job was run when the actor is initialized. */
  def lastRun: Option[DateTime] = None

  /** The interval between runs */
  def interval: FiniteDuration

  def currentDateTime = DateTime.now

  def nextRunIn: FiniteDuration = {
    val now = FiniteDuration(1, TimeUnit.SECONDS)
    lastRun match {
      case None => now
      case Some(lastRunTime) =>
        val defaultDuration = new org.joda.time.Duration(interval.toMillis)
        val durationSinceLastRun = new Interval(lastRunTime, currentDateTime).toDuration
        if (durationSinceLastRun.isLongerThan(defaultDuration) || durationSinceLastRun.isEqual(defaultDuration)) now
        else FiniteDuration(defaultDuration.minus(durationSinceLastRun).getStandardSeconds, TimeUnit.SECONDS)
    }
  }

  override def preStart() {
    schedule(Some(nextRunIn))
  }

  def schedule(nextRunIn: Option[FiniteDuration] = None) {
    scheduler.foreach(x => if (!x.isCancelled) x.cancel())
    val runTime = nextRunIn.getOrElse(interval)
    log.debug(s"Next run in $runTime")
    scheduler = Some(context.system.scheduler.scheduleOnce(runTime, self, IgnoreIfBusy.Run(None)))
  }

  def run(data: Option[Any]) = {
    schedule()
    runJob()
  }

  /** To override with your periodic job code */
  def runJob(): Unit
}
