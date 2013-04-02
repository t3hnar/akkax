# AkkaX [![Build Status](https://secure.travis-ci.org/t3hnar/akkax.png)](http://travis-ci.org/t3hnar/akkax)

Useful utils for [Akka Actors](http://http://akka.io)

### RoutingActor
Actor which will pass message to child actor defined for route. If no actors found, it will create a new one.

### BroadcastingActor
Broadcast message to all child actors

### DurationToString
Converts duration to string

```scala
import ua.t3hnar.akkax.DurationToString

DurationToString(Duration(60, TimeUnit.SECONDS)) == "1 minute"
DurationToString(Duration(60, TimeUnit.MINUTES)) == "1 hour"
DurationToString(Duration(180, TimeUnit.MINUTES)) == "3 hours"
```

### NotifyParentOnRestart
Mix this trait to enable on restart notification for actor.

```scala
import ua.t3hnar.akkax.{NotifyParentOnRestart, Restarted}

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
import ua.t3hnar.akkax.IgnoreIfBusy

class IgnoreIfBusyExample extends Actor with ActorLogging with IgnoreIfBusy {
  protected def receive = receiveRun

  def run(data: Option[Any]) {
    // heavy call
  }
}
```

## Setup

1. Add this repository to your pom.xml:
```xml
    <repository>
        <id>thenewmotion</id>
        <name>The New Motion Repository</name>
        <url>http://nexus.thenewmotion.com/content/repositories/releases-public</url>
    </repository>
```

2. Add dependency to your pom.xml:
```xml
    <dependency>
        <groupId>ua.t3hnar.akkax</groupId>
        <artifactId>akkax</artifactId>
        <version>1.2</version>
    </dependency>
```