package ua.t3hnar.akkax

import akka.dispatch.Future
import akka.actor.Actor

/**
 * @author Yaroslav Klymko
 */
trait IgnoreIfBusy {
  this: Actor =>

  import IgnoreIfBusy._
  import context.{become, unbecome}
  import context.system

  def future[T](f: => T, completed: Completed) {
    Future(f).onComplete(_ => self ! completed)
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