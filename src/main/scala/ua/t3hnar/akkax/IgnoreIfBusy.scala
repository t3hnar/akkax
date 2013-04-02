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

  def run(data: Option[Any])

  def receiveRun: Receive = {
    case Run(data) =>
      val completed = new Completed
      become {
        case `completed` => unbecome()
      }
      future(run(data), completed)
  }
}

object IgnoreIfBusy {
  case class Run(data: Option[Any] = None)

  class Completed {
    override def toString = "Completed"
  }
}