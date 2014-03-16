package ua.t3hnar.akkax.routing

import akka.actor._

/**
 * @author Yaroslav Klymko
 */

trait RoutingActor extends Actor with ActorLogging {
  final override def supervisorStrategy = SupervisorStrategy.stoppingStrategy

  var children = Map[Any, ActorRef]()

  def newChild(routed: RoutedMsg): Option[Props]

  def registerChild(child: ActorRef, route: Any): ActorRef = {
    log.info("add: {} {}", route, child)
    context.watch(child)
    children = children + (route -> child)
    child
  }

  def child(routed: RoutedMsg): Option[ActorRef] = {
    val route = routed.route
    children.get(route) match {
      case some @ Some(_) => some
      case None => newChild(routed) match {
        case None =>
          log.info("ignore: {}", routed)
          context.system.deadLetters forward routed
          None
        case Some(props) =>
          val child = context.actorOf(props)
          Some(registerChild(child, route))
      }
    }
  }

  def unregisterChild(child: ActorRef) {
    val keys = children.collect {
      case (key, `child`) => key
    }.toSet
    if (keys.nonEmpty) log.info("remove: {}", keys.mkString(","))
    children = children -- keys
  }

  def receiveRouted: Receive = {
    case routed @ RoutedMsg(_, msg) => child(routed).foreach(_ forward msg)
    case Terminated(child)          => unregisterChild(child)
  }
}

case class RoutedMsg(route: Any, msg: Any)