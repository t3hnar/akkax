package com.github.t3hnar.akkax

import org.specs2.mutable.Specification
import org.specs2.specification.Scope
import akka.testkit.{ TestActorRef, ImplicitSender, TestKit }
import akka.actor._

class RoutingSpec extends Specification {
  "RoutingActor" should {
    "send message to child if exists, otherwise create a new one" in new ActorScope {
      actor.children must beEmpty
      actorRef ! Routing.Routed(route, msg)
      expectMsg(msg)
      actor.children must haveSize(1)

      actorRef ! Routing.Routed(2, msg)
      actorRef ! Routing.Routed(3, msg)
      expectMsgAllOf(msg, msg)
      actor.children must haveSize(3)
    }

    "not follow dead children" in new ActorScope {
      actorRef ! Routing.Routed(route, msg)
      expectMsg(msg)
      actor.children must haveSize(1)
      actorRef ! Routing.Routed(route, kill)

      awaitCond {
        actor.children.isEmpty
      }
    }

    "not follow failed children" in new ActorScope {
      actorRef ! Routing.Routed(route, msg)
      expectMsg(msg)
      actor.children must haveSize(1)
      actorRef ! Routing.Routed(route, error)

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

    class TestRoutingActor extends Actor with ActorLogging with Routing {
      def newChild(routed: Routing.Routed) = Some(Props(new EchoActor))
      def receive = receiveRouted
    }

    class EchoActor extends Actor {
      def receive = {
        case `error` => throw new Exception
        case `kill` => context.stop(self)
        case x => sender ! x
      }
    }
  }
}