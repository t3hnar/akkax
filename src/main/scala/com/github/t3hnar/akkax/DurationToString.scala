package com.github.t3hnar.akkax

import java.util.concurrent.TimeUnit
import concurrent.duration.FiniteDuration
import TimeUnit._

object DurationToString {
  def apply(d: FiniteDuration): String = {
    def to(unit: => TimeUnit, value: Long): Option[FiniteDuration] = {
      if (value > 0) Some(FiniteDuration(value, unit)) else None
    }

    val duration = to(HOURS, d.toHours) getOrElse (to(MINUTES, d.toMinutes) getOrElse (to(SECONDS, d.toSeconds) getOrElse d))
    duration.toString()
  }
}
