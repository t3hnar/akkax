package ua.t3hnar.akkax.routing

import akka.actor._
import akka.actor.SupervisorStrategy.Stop
import scala.Some
import akka.actor.OneForOneStrategy

/**
 * @author Yaroslav Klymko
 */

trait RoutingActor extends Actor {

  override def supervisorStrategy() = OneForOneStrategy() {
    case _: Exception => Stop
  }

  val children = collection.mutable.Map[Any, ActorPath]()

  def newChild(route: Any): ActorRef

  def registerChild(child: ActorRef, route: Any): ActorRef = {
    context.watch(child)
    children += (route -> child.path)
    child
  }

  def child(route: Any) = children.get(route) match {
    case Some(path) => context.actorFor(path)
    case None => registerChild(newChild(route), route)
  }

  def unregisterChild(path: ActorPath) {
    children.toList.collectFirst {
      case (key, `path`) => key
    }.foreach(children -= _)
  }

  protected def receiveRouted: Receive = {
    case Routed(routeId, msg) => child(routeId).tell(msg, sender)
    case Terminated(child) => unregisterChild(child.path)
  }
}

case class Routed(routeId: Any, msg: Any)
