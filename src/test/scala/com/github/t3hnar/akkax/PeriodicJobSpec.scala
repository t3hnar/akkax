package com.github.t3hnar.akkax

import java.util.concurrent.TimeUnit
import akka.actor._
import akka.testkit.{ ImplicitSender, TestActorRef, TestKit }
import org.joda.time.DateTime
import org.specs2.mutable.Specification
import org.specs2.specification.Scope
import scala.concurrent.duration.{ Duration, FiniteDuration }

class PeriodicJobSpec extends Specification {
  "PeriodicJob" should {
    "calculate when next job will run when lastRun is less than interval" in new ActorScope((dt) => Some(dt.minusHours(4))) {
      actor.nextRunIn mustEqual FiniteDuration(20, TimeUnit.HOURS)
    }

    "calculate when next job will run when lastRun is more than interval" in new ActorScope((dt) => Some(dt.minusHours(25))) {
      actor.nextRunIn mustEqual FiniteDuration(1, TimeUnit.SECONDS)
    }

    "calculate when next job will run when lastRun is unknown" in new ActorScope((dt) => None) {
      actor.nextRunIn mustEqual FiniteDuration(1, TimeUnit.SECONDS)
    }

    "make sure job is run" in new ActorScope((dt) => Some(dt.minusHours(23).minusMinutes(59).minusSeconds(59))) {
      awaitCond(actor.jobWasRun, Duration(2, TimeUnit.SECONDS), Duration(500, TimeUnit.MILLISECONDS))
    }
  }

  class ActorScope(f: DateTime => Option[DateTime] = (_) => None) extends TestKit(ActorSystem()) with ImplicitSender with Scope {
    val actorRef = TestActorRef(new TestPeriodicJobActor)
    def actor = actorRef.underlyingActor

    class TestPeriodicJobActor extends Actor with ActorLogging with IgnoreIfBusy with PeriodicJob {
      var jobWasRun = false
      val interval: FiniteDuration = FiniteDuration(1, TimeUnit.DAYS)
      def runJob() = jobWasRun = true
      def receive = receiveRun
      override def currentDateTime = new DateTime("2015-04-10T08:42:34Z")
      override val lastRun: Option[DateTime] = f(currentDateTime)
    }
  }
}