package ua.t3hnar.akkax.routing


/**
 * @author Yaroslav Klymko
 */
trait Broadcasting {
  this: RoutingActor =>

  def receiveBroadcast: Receive = {
    case Broadcast(msg) =>

      val routes = children.toList.map {
        case (route, path) =>
          context.actorFor(path).tell(msg, sender)
          route
      }
      sender ! BroadcastRoutes(routes)
  }
}

case class Broadcast(msg: Any)
case class BroadcastRoutes(routes: List[Any])