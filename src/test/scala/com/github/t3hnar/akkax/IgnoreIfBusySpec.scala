package com.github.t3hnar.akkax

import org.specs2.mutable.Specification
import org.specs2.specification.Scope
import akka.testkit.{ ImplicitSender, TestKit }
import akka.actor._

class IgnoreIfBusySpec extends Specification {
  "IgnoreIfBusyActor" should {
    "ignore second message if busy with first" in new ActorScope {
      val firstMsg = Some(DoSomething(1))
      val secondMsg = Some(DoSomething(2))

      actorRef ! IgnoreIfBusy.Run(firstMsg)
      actorRef ! IgnoreIfBusy.Run(secondMsg)
      awaitCond(runData == List(firstMsg) && ignoredData == List(secondMsg))
    }

    "accept second message if finished with first" in new ActorScope {
      val firstMsg = Some(DoSomething(1))
      val secondMsg = Some(DoSomething(2))

      actorRef ! IgnoreIfBusy.Run(firstMsg)
      Thread.sleep(200)
      actorRef ! IgnoreIfBusy.Run(secondMsg)
      awaitCond(runData == List(firstMsg, secondMsg))
    }
  }

  class ActorScope extends TestKit(ActorSystem()) with ImplicitSender with Scope {
    case class DoSomething(data: Int)

    val actorRef = system.actorOf(Props(new TestIgnoreIfBusyActor))
    var runData = List[Option[Any]]()
    var ignoredData = List[Option[Any]]()

    class TestIgnoreIfBusyActor extends Actor with ActorLogging with IgnoreIfBusy {
      def receive = receiveRun
      def run(data: Option[Any]) = {
        runData = runData :+ data
        Thread.sleep(100)
      }
      override def ignored(data: Option[Any]) = {
        ignoredData = ignoredData :+ data
      }
    }
  }
}