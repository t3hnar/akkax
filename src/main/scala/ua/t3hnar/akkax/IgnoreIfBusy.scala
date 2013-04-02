package ua.t3hnar.akkax

import akka.dispatch.Future
import akka.actor.{ActorLogging, Actor}

/**
 * @author Yaroslav Klymko
 */
trait IgnoreIfBusy {
  this: Actor with ActorLogging =>

  import IgnoreIfBusy._
  import context.{become, unbecome}
  import context.system

  def future[T](f: => T, completed: Completed) {
    Future(f).onComplete {
      x =>
        if (x.isLeft) log.error(x.left.get, "Exception while running")
        self ! completed
    }
  }

  def run()

  def receiveRun: Receive = {
    case Run =>
      val completed = new Completed
      become {
        case `completed` => unbecome()
      }
      future(run(), completed)
  }
}

object IgnoreIfBusy {
  case object Run
  class Completed {
    override def toString = "Completed"
  }
}