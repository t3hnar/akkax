package com.github.t3hnar.akkax

import org.specs2.mutable.Specification
import org.specs2.specification.Scope
import akka.testkit.{ ImplicitSender, TestKit }
import akka.actor._

class IgnoreIfBusySpec extends Specification {
  "IgnoreIfBusyActor" should {
    "ignore second message if busy with first" in new ActorScope {
      actorRef ! IgnoreIfBusy.Run(Some(DoSomething))
      actorRef ! IgnoreIfBusy.Run(Some(DoSomething))
      awaitCond(runCount == 1 && ignoreCount == 1)
    }

    "accept second message if finished with first" in new ActorScope {
      actorRef ! IgnoreIfBusy.Run(Some(DoSomething))
      Thread.sleep(200)
      actorRef ! IgnoreIfBusy.Run(Some(DoSomething))
      awaitCond(runCount == 2 && ignoreCount == 0)
    }
  }

  class ActorScope extends TestKit(ActorSystem()) with ImplicitSender with Scope {
    case object DoSomething

    val actorRef = system.actorOf(Props(new TestIgnoreIfBusyActor))
    var runCount = 0
    var ignoreCount = 0

    class TestIgnoreIfBusyActor extends Actor with ActorLogging with IgnoreIfBusy {
      def receive = receiveRun
      def run(data: Option[Any]) = {
        runCount += 1
        Thread.sleep(100)
      }
      override def ignored(data: Option[Any]) = {
        ignoreCount += 1
      }
    }
  }
}