package com.github.t3hnar.akkax

import akka.actor.Actor

object Broadcasting {
  case class Broadcast(msg: Any)
  case class Routes(routes: List[Any])
}

trait Broadcasting {
  this: Actor with Routing =>

  import Broadcasting._

  def receiveBroadcast: Receive = {
    case Broadcast(msg) =>
      val routes = children.map {
        case (route, child) =>
          child.forward(msg)
          route
      }
      sender ! Routes(routes.toList)
  }
}

