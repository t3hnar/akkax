package com.github.t3hnar.akkax

import akka.actor._

object Routing {
  case class Routed(route: Any, msg: Any, createRouteIfNotExist: Boolean = true)
}

trait Routing {
  this: Actor with ActorLogging =>

  import Routing._

  final override def supervisorStrategy = SupervisorStrategy.stoppingStrategy

  var children = Map[Any, ActorRef]()

  def newChild(routed: Routed): Option[Props]

  def registerChild(child: ActorRef, route: Any): ActorRef = {
    log.info("add: {} {}", route, child)
    context.watch(child)
    children = children + (route -> child)
    child
  }

  def child(routed: Routed): Option[ActorRef] = {
    val route = routed.route
    children.get(route) match {
      case some @ Some(_) => some
      case None => if (routed.createRouteIfNotExist) newChild(routed) match {
        case None =>
          log.info("ignore: {}", routed)
          context.system.deadLetters forward routed
          None
        case Some(props) =>
          val child = context.actorOf(props)
          Some(registerChild(child, route))
      }
      else None
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
    case routed @ Routed(_, msg, _) => child(routed).foreach(_ forward msg)
    case Terminated(child)          => unregisterChild(child)
  }
}