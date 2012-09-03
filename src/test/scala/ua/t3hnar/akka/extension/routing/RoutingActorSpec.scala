package ua.t3hnar.akka.extension.routing

import org.specs2.mutable.SpecificationWithJUnit
import org.specs2.specification.Scope
import com.typesafe.config.ConfigFactory
import akka.testkit.{TestActorRef, ImplicitSender, TestKit}
import akka.actor.{ActorPath, Actor, Props, ActorSystem}

/**
 * @author Yaroslav Klymko
 */
class RoutingActorSpec extends SpecificationWithJUnit {
  "RoutingActor" should {
    "send message to child if exists, or create new if doesn't" >> new ChargerRouterScope {
      actor.children must beEmpty

      actorRef ! Routed("route1", "ping")
      expectMsg("pong")
      actor.children must haveSize(1)

      actorRef ! Routed("route2", "ping")
      actorRef ! Routed("route3", "ping")
      expectMsg("pong")
      expectMsg("pong")
      actor.children must haveSize(3)
    }

    "not follow dead children" >> new ChargerRouterScope {
      actorRef ! Routed("route", "ping")
      expectMsg("pong")
      actor.children must haveSize(1)
      actorRef ! Routed("route", "kill")
      expectMsg("killed")
      actor.children must haveSize(0)
    }

    "not follow failed children" >> new ChargerRouterScope {
      actorRef ! Routed("route", "ping")
      expectMsg("pong")
      actor.children must haveSize(1)
      actorRef ! Routed("route", "error")
      expectMsg("killed")
      actor.children must haveSize(0)
    }
  }

  class ChargerRouterScope extends TestKit(ActorSystem("test", ConfigFactory.empty())) with ImplicitSender with Scope {
    val actorRef = TestActorRef(new PingPongRoutingActor {
      override def unregisterChild(path: ActorPath) {
        super.unregisterChild(path)
        testActor ! "killed"
      }
    }, "pingpongrouter")

    def actor = actorRef.underlyingActor
  }

  class PingPongRoutingActor extends RoutingActor {
    def newChild(routeId: Any) = context.actorOf(Props(new PingPongActor))

    def receive = receiveRouted
  }

  class PingPongActor extends Actor {
    protected def receive = {
      case "ping" => sender ! "pong"
      case "error" => throw new Exception
      case "kill" => context.stop(self)
    }
  }
}