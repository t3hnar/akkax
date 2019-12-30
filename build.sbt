name := "akkax"

organization := "com.github.t3hnar"

scalaVersion := "2.13.1"

crossScalaVersions := Seq("2.11.8", "2.12.10", "2.13.1")

licenses := Seq(("Apache License, Version 2.0", url("http://www.apache.org/licenses/LICENSE-2.0")))

homepage := Some(new URL("https://github.com/t3hnar/akkax"))

scalacOptions := Seq("-encoding", "UTF-8", "-unchecked", "-deprecation", "-feature", "-Xlint")

def akkaDependencies(scalaVersion: String) = {
  val akkaVersion = "2.5.27"

  Seq(
    "com.typesafe.akka" %% "akka-actor" % akkaVersion,
    "com.typesafe.akka" %% "akka-testkit" % akkaVersion % "test"
  )
}

libraryDependencies ++= { akkaDependencies(scalaVersion.value) }

libraryDependencies += "joda-time" % "joda-time" % "2.10.5"

libraryDependencies += "org.joda" % "joda-convert" % "2.2.1"

libraryDependencies += "org.specs2" %% "specs2-core" % "4.8.1" % "test"

sonatypeProfileName := "t3hnar"

pomExtra := {
  <scm>
    <url>git@github.com:t3hnar/akkax.git</url>
    <developerConnection>scm:git:git@github.com:t3hnar/akkax.git</developerConnection>
    <connection>scm:git:git@github.com:t3hnar/akkax.git</connection>
  </scm>
  <developers>
    <developer>
      <id>t3hnar</id>
      <name>Yaroslav Klymko</name>
      <email>t3hnar@gmail.com</email>
    </developer>
  </developers>
}

import scalariform.formatter.preferences._

scalariformPreferences := scalariformPreferences.value
  .setPreference(AlignParameters, true)
  .setPreference(AlignSingleLineCaseStatements, true)
  .setPreference(DoubleIndentClassDeclaration, true)
