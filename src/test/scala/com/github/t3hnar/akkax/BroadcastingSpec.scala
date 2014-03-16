package com.github.t3hnar.akkax

import akka.testkit.{ TestActorRef, ImplicitSender, TestKit }
import akka.actor._
import com.typesafe.config.ConfigFactory
import org.specs2.specification.Scope
import org.specs2.mutable.Specification
import Broadcasting._

class BroadcastingSpec extends Specification {

  "Broadcasting" should {
    "broadcast message to all children" in new BroadcastingScope {
      val msg = "hello"
      actor ! Broadcast(msg)
      expectMsgAllOf(routes.map(_ => msg): _*)
      val br = expectMsgType[Routes]
      br.routes.toSet mustEqual routes.toSet
    }
  }

  class BroadcastingScope extends TestKit(ActorSystem("test", ConfigFactory.empty())) with ImplicitSender with Scope {
    val routes = (1 to 5).toList
    val actor = TestActorRef(new Broadcaster)
    actor.underlyingActor.children = routes.map(_ -> testActor).toMap

    class Broadcaster extends Actor with ActorLogging with Routing with Broadcasting {
      def newChild(route: Routing.Routed) = None
      def receive = receiveBroadcast
    }
  }
}
