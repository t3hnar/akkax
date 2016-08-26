import SonatypeKeys._
import scalariform.formatter.preferences._

name := "akkax"

organization := "com.github.t3hnar"

scalaVersion := "2.11.8"

crossScalaVersions := Seq("2.10.5", "2.11.8")

licenses := Seq(("Apache License, Version 2.0", url("http://www.apache.org/licenses/LICENSE-2.0")))

homepage := Some(new URL("https://github.com/t3hnar/akkax"))

scalacOptions := Seq("-encoding", "UTF-8", "-unchecked", "-deprecation", "-feature", "-Xlint")

def akkaDependencies(scalaVersion: String) = {
  val akkaVersion = if (scalaVersion.startsWith("2.10.")) "2.3.15" else "2.4.9"

  Seq(
    "com.typesafe.akka" %% "akka-actor" % akkaVersion,
    "com.typesafe.akka" %% "akka-testkit" % akkaVersion % "test"
  )
}

libraryDependencies <++= scalaVersion { v => akkaDependencies(v) }

libraryDependencies += "joda-time" % "joda-time" % "2.7"

libraryDependencies += "org.joda" % "joda-convert" % "1.2"

libraryDependencies += "org.specs2" %% "specs2-core" % "2.4.15" % "test"

profileName := "t3hnar"

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

ScalariformKeys.preferences := ScalariformKeys.preferences.value
  .setPreference(AlignParameters, true)
  .setPreference(AlignSingleLineCaseStatements, true)
  .setPreference(DoubleIndentClassDeclaration, true)

scalariformSettings

sonatypeSettings

releaseSettings
