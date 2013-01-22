package ua.t3hnar.akkax

import akka.util.Duration
import java.util.concurrent.TimeUnit
import TimeUnit._

/**
 * @author Yaroslav Klymko
 */
object DurationToString {
  def apply(d: Duration): String = {
    def to(unit: => TimeUnit, value: Long): Option[Duration] = {
      if (value > 0) Some(Duration(value, unit)) else None
    }

    val duration = to(HOURS, d.toHours) getOrElse (to(MINUTES, d.toMinutes) getOrElse (to(SECONDS, d.toSeconds) getOrElse d))
    duration.toString
  }
}
