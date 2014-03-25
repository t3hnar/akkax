package com.github.t3hnar.akkax

import akka.actor.{ ActorLogging, Actor }
import concurrent.Future

trait IgnoreIfBusy {
  this: Actor with ActorLogging =>

  import IgnoreIfBusy._
  import context.{ become, unbecome, dispatcher }

  def future[T](f: => T, completed: Completed) {
    Future(f).onComplete {
      x =>
        if (x.isFailure) log.error(x.failed.get, "Exception while running")
        self ! completed
    }
  }

  def run(data: Option[Any])
  def ignored(data: Option[Any]) = {
    log.debug(s"Message ignored: $data")
  }

  def receiveRun: Receive = {
    case Run(data) =>
      val completed = new Completed
      become {
        case `completed` => unbecome()
        case x => ignored(data)
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