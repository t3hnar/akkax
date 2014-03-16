package com.github.t3hnar.akkax


trait Broadcasting {
  this: RoutingActor =>

  def receiveBroadcast: Receive = {
    case Broadcast(msg) =>
      val routes = children.map {
        case (route, child) =>
          child.forward(msg)
          route
      }
      sender ! BroadcastRoutes(routes.toList)
  }
}

case class Broadcast(msg: Any)
case class BroadcastRoutes(routes: List[Any])