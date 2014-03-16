package ua.t3hnar.akkax.examples

import ua.t3hnar.akkax.{ NotifyParentOnRestart, Restarted }
import akka.actor.{ Actor, Props }

/**
 * @author Yaroslav Klymko
 */
class ChildActor extends Actor with NotifyParentOnRestart {
  def receive = {
    case _ =>
  }
}

class ParentActor extends Actor {
  val child = context.actorOf(Props(new ChildActor))

  def receive = {
    case Restarted(`child`) => // your code for handling restart
  }
}