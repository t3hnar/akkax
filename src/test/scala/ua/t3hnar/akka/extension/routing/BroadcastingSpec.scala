package ua.t3hnar.akka.extension.routing

import akka.testkit.{TestActorRef, ImplicitSender, TestKit}
import akka.actor._
import com.typesafe.config.ConfigFactory
import org.specs2.specification.Scope
import org.specs2.mutable.SpecificationWithJUnit

/**
 * @author Yaroslav Klymko
 */
class BroadcastingSpec extends SpecificationWithJUnit {

  "Broadcasting" should {
    "broadcast message to all children" in new BroadcastingScope {
      actor ! Broadcast("hello")
      expectMsgAllOf("hello", "hello", "hello")
    }
  }

  class BroadcastingScope extends TestKit(ActorSystem("test", ConfigFactory.empty())) with ImplicitSender with Scope {
    val actor = TestActorRef(new Broadcaster {
      override val children = collection.mutable.Map[Any, ActorPath](
        "r1" -> testActor.path,
        "r2" -> testActor.path,
        "r3" -> testActor.path)
    })
    def underlying = actor.underlyingActor
  }

  class Broadcaster extends RoutingActor with Broadcasting with ActorLogging {
    def newChild(routeId: Any) = context.actorOf(Props(new EchoActor))
    def receive = receiveBroadcast
  }

  class EchoActor extends Actor {
    protected def receive = { case msg => sender ! msg }
  }
}
