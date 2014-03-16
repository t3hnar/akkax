package com.github.t3hnar.akkax

import org.specs2.mutable.Specification
import org.specs2.specification.Scope
import akka.testkit.{ TestActorRef, ImplicitSender, TestKit }
import akka.actor._
import com.github.t3hnar.akkax.RoutedMsg


class RoutingActorSpec extends Specification {
  "RoutingActor" should {
    "send message to child if exists, otherwise create a new one" in new ActorScope {
      actor.children must beEmpty
      actorRef ! RoutedMsg(route, msg)
      expectMsg(msg)
      actor.children must haveSize(1)

      actorRef ! RoutedMsg(2, msg)
      actorRef ! RoutedMsg(3, msg)
      expectMsgAllOf(msg, msg)
      actor.children must haveSize(3)
    }

    "not follow dead children" in new ActorScope {
      actorRef ! RoutedMsg(route, msg)
      expectMsg(msg)
      actor.children must haveSize(1)
      actorRef ! RoutedMsg(route, kill)

      awaitCond {
        actor.children.isEmpty
      }
    }

    "not follow failed children" in new ActorScope {
      actorRef ! RoutedMsg(route, msg)
      expectMsg(msg)
      actor.children must haveSize(1)
      actorRef ! RoutedMsg(route, error)

      awaitCond {
        actor.children.isEmpty
      }
    }

    "monitor not only direct children" in new ActorScope {
      actor.registerChild(testActor, route)
      actor.children must haveSize(1)
      system.stop(testActor)
      awaitCond {
        actor.children.isEmpty
      }
    }
  }

  class ActorScope extends TestKit(ActorSystem()) with ImplicitSender with Scope {
    val route = 1
    val msg = "msg"
    val kill = "kill"
    val error = "error"
    val actorRef = TestActorRef(new TestRoutingActor)
    def actor = actorRef.underlyingActor

    class TestRoutingActor extends RoutingActor {
      def newChild(routed: RoutedMsg) = Some(Props(new EchoActor))
      def receive = receiveRouted
    }

    class EchoActor extends Actor {
      def receive = {
        case `error` => throw new Exception
        case `kill`  => context.stop(self)
        case x       => sender ! x
      }
    }
  }
}