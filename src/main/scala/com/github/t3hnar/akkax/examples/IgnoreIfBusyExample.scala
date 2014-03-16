package com.github.t3hnar.akkax
package examples

import akka.actor.{ ActorLogging, Actor }
import com.github.t3hnar.akkax.IgnoreIfBusy

class IgnoreIfBusyExample extends Actor with ActorLogging with IgnoreIfBusy {
  def receive = receiveRun

  def run(data: Option[Any]) {
    // heavy call
  }
}