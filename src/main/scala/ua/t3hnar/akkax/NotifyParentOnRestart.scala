package ua.t3hnar.akkax

import akka.actor.{ ActorRef, Actor }

/**
 * @author Yaroslav Klymko
 */
trait NotifyParentOnRestart {
  this: Actor =>

  abstract override def postRestart(reason: Throwable) {
    context.parent ! Restarted(self)
  }

  def restarted() {
    context.parent ! Restarted(self)
  }
}

case class Restarted(actor: ActorRef)