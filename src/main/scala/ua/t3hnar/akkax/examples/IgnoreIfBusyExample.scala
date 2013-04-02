package ua.t3hnar.akkax
package examples

import akka.actor.{ActorLogging, Actor}
import ua.t3hnar.akkax.IgnoreIfBusy

/**
 * @author Yaroslav Klymko
 */
class IgnoreIfBusyExample extends Actor with ActorLogging with IgnoreIfBusy {
  protected def receive = receiveRun
  def run() {
    // heavy call
  }
}