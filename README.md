# AkkaX [![Build Status](https://secure.travis-ci.org/t3hnar/akkax.svg)](http://travis-ci.org/t3hnar/akkax) [![Coverage Status](https://coveralls.io/repos/t3hnar/akkax/badge.svg)](https://coveralls.io/r/t3hnar/akkax)

Useful utils for [Akka Actors](http://http://akka.io)

### RoutingActor
Actor which will pass message to child actor defined for route. If no actors found, it may create a new one.

### BroadcastingActor
Broadcast message to all child actors

### PeriodicJobActor
Actor which run a job at a certain interval

### DurationToString
Converts duration to string

```scala
import com.github.t3hnar.akkax.DurationToString

DurationToString(Duration(60, TimeUnit.SECONDS)) == "1 minute"
DurationToString(Duration(60, TimeUnit.MINUTES)) == "1 hour"
DurationToString(Duration(180, TimeUnit.MINUTES)) == "3 hours"
```

### NotifyParentOnRestart
Mix this trait to enable on restart notification for actor.

```scala
import com.github.t3hnar.akkax.{NotifyParentOnRestart, Restarted}

class ChildActor extends Actor with NotifyParentOnRestart {
    def receive = { case _ => }
}

class ParentActor extends Actor {
    val child = context.actorOf(Props(new ChildActor))

    def receive = {
        case Restarted(`child`) => // your code for handling restart
    }
}
```

### IgnoreIfBusy
Trait `IgnoreIfBusy` will allow your actor to ignore all messages while it busy with running heavy call

```scala
import com.github.t3hnar.akkax.IgnoreIfBusy

class IgnoreIfBusyExample extends Actor with ActorLogging with IgnoreIfBusy {
  def receive = receiveRun

  def run(data: Option[Any]) {
    // heavy call
  }
}
```

## Setup

* Maven:
```xml
    <dependency>
        <groupId>com.github.t3hnar</groupId>
        <artifactId>akkax_2.11</artifactId>
        <version>2.3</version>
    </dependency>
```

* Sbt
```scala
    libraryDependencies += "com.github.t3hnar" %% "akkax" % "2.3"
```