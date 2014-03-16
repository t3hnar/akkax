import sbt._
import Keys._
import sbtrelease.ReleasePlugin._

object Build extends Build {
  lazy val basicSettings = Seq(
    name := "akkax",
    organization := "com.github.t3hnar",
    scalaVersion := "2.10.3",
    licenses := Seq(("Apache License, Version 2.0", url("http://www.apache.org/licenses/LICENSE-2.0"))),
    homepage := Some(new URL("https://github.com/t3hnar/akkax")),
    startYear := Some(2012),
    scalacOptions := Seq("-encoding", "UTF-8", "-unchecked", "-deprecation", "-feature"),
    libraryDependencies ++= Seq(Akka.actor, Akka.testkit, junit, specs2))

  object Akka {
    lazy val actor   = apply("actor")
    lazy val testkit = apply("testkit") % "test"

    def apply(x: String) = "com.typesafe.akka" %% s"akka-$x" % "2.3.0" withSources()
  }

  val junit       = "junit" % "junit" % "4.11" % "test"
  val specs2      = "org.specs2" %% "specs2" % "2.3.4" % "test"

  lazy val root = Project(
    "akkax",
    file("."),
    settings = basicSettings ++ Defaults.defaultSettings ++ releaseSettings ++ Format.settings ++ Publish.settings)
}