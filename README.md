# Akka Extension [![Build Status](https://secure.travis-ci.org/t3hnar/akka-extension.png)](http://travis-ci.org/t3hnar/akka-extension)

Usefull utils for [Akka Actors](http://http://akka.io)

* ### RoutingActor

    Actor which will pass message to child actor defined for route. If no actors found, it will create a new one.

* ### BroadcastingActor

    Broadcast message to all child actors

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
        <groupId>ua.t3hnar.akka</groupId>
        <artifactId>akka-extensiont</artifactId>
        <version>1.0</version>
    </dependency>
```