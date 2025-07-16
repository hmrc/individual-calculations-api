/*
 * Copyright 2025 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
//import sbt.*
//import uk.gov.hmrc.DefaultBuildSettings
//
//ThisBuild / scalaVersion := "3.5.2"
//ThisBuild / majorVersion := 1
//
//val appName = "individual-calculations-api"
//
//lazy val microservice = Project(appName, file("."))
//  .enablePlugins(play.sbt.PlayScala, SbtDistributablesPlugin)
//  .disablePlugins(JUnitXmlReportPlugin) // Required to prevent https://github.com/scalatest/scalatest/issues/1427
//  .settings(
//    libraryDependencies ++= AppDependencies.compile ++ AppDependencies.test,
//    retrieveManaged                 := true,
//    update / evictionWarningOptions := EvictionWarningOptions.default.withWarnScalaVersionEviction(warnScalaVersionEviction = false),
//    scalacOptions ++= List(
//      "-feature",
//      "-Wconf:src=routes/.*:s",
//      "-Werror"
//    ),
//    scalacOptions ++= Seq("-nowarn")
//  )
//  .settings(
//    Compile / unmanagedResourceDirectories += baseDirectory.value / "resources",
//    Compile / unmanagedClasspath += baseDirectory.value / "resources"
//  )
//  .settings(CodeCoverageSettings.settings)
//  .settings(PlayKeys.playDefaultPort := 9767)
//
//lazy val it = project
//  .enablePlugins(PlayScala)
//  .dependsOn(microservice % "test->test")
//  .settings(DefaultBuildSettings.itSettings() ++ ScalafmtPlugin.scalafmtConfigSettings)
//  .settings(
//    Test / fork := true,
//    Test / javaOptions += "-Dlogger.resource=logback-test.xml")
//  .settings(libraryDependencies ++= AppDependencies.itDependencies)
//  .settings(
//    scalacOptions ++= Seq("-Werror"),
//    scalacOptions ++= Seq("-nowarn")
//  )
//dependencyUpdatesFilter -= moduleFilter(name = "bootstrap-backend-play-30")
//dependencyUpdatesFilter -= moduleFilter(organization = "org.playframework")
//dependencyUpdatesFilter -= moduleFilter(name = "scala-library")
//dependencyUpdatesFilter -= moduleFilter(name = "scalatestplus-play")



import sbt.*
import uk.gov.hmrc.DefaultBuildSettings

ThisBuild / scalaVersion := "3.5.2"
ThisBuild / majorVersion := 1

val appName = "individual-calculations-api"

lazy val microservice = Project(appName, file("."))
  .enablePlugins(play.sbt.PlayScala, SbtDistributablesPlugin)
  .disablePlugins(JUnitXmlReportPlugin) // Required to prevent https://github.com/scalatest/scalatest/issues/1427
  .settings(
    libraryDependencies ++= AppDependencies.compile ++ AppDependencies.test,
    retrieveManaged                 := true,
    update / evictionWarningOptions := EvictionWarningOptions.default.withWarnScalaVersionEviction(warnScalaVersionEviction = false),
    scalacOptions ++= Seq(
      "-feature",
      "-Wconf:src=routes/.*:s",
      "-Werror"
    ),
    scalacOptions ++= Seq("-nowarn")
  )
  .settings(
    Compile / unmanagedResourceDirectories += baseDirectory.value / "resources",
    Compile / unmanagedClasspath += baseDirectory.value / "resources"
  )
  .settings(CodeCoverageSettings.settings)
  .settings(PlayKeys.playDefaultPort := 9767)

lazy val it = project
  .enablePlugins(PlayScala)
  .dependsOn(microservice % "test->test")
  .settings(DefaultBuildSettings.itSettings() ++ ScalafmtPlugin.scalafmtConfigSettings)
  .settings(
    Test / fork := true,
    Test / javaOptions += "-Dlogger.resource=logback-test.xml")
  .settings(libraryDependencies ++= AppDependencies.itDependencies)
  .settings(
    scalacOptions ++= Seq("-Werror"),
    scalacOptions ++= Seq("-nowarn")
  )

dependencyUpdatesFilter -= moduleFilter(name = "bootstrap-backend-play-30")
dependencyUpdatesFilter -= moduleFilter(organization = "org.playframework")
dependencyUpdatesFilter -= moduleFilter(name = "scala-library")
dependencyUpdatesFilter -= moduleFilter(name = "scalatestplus-play")
