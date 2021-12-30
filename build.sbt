/*
 * Copyright 2021 HM Revenue & Customs
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

import sbt._
import uk.gov.hmrc.DefaultBuildSettings.{addTestReportOption, defaultSettings}
import uk.gov.hmrc.SbtAutoBuildPlugin
import uk.gov.hmrc.sbtdistributables.SbtDistributablesPlugin.publishingSettings
import uk.gov.hmrc.versioning.SbtGitVersioning.autoImport.majorVersion

val appName = "individual-calculations-api"

lazy val ItTest = config("it") extend Test

lazy val microservice = Project(appName, file("."))
  .enablePlugins(play.sbt.PlayScala, SbtAutoBuildPlugin, SbtDistributablesPlugin)
  .disablePlugins(JUnitXmlReportPlugin) //Required to prevent https://github.com/scalatest/scalatest/issues/1427
  .settings(
    libraryDependencies ++= AppDependencies.compile ++ AppDependencies.test(),
    retrieveManaged := true,
    update / evictionWarningOptions := EvictionWarningOptions.default.withWarnScalaVersionEviction(warnScalaVersionEviction = false),
    scalaVersion := "2.12.13"
  )
  .settings(
    Compile / unmanagedResourceDirectories += baseDirectory.value / "resources"
  )
  .settings(majorVersion := 1)
  .settings(publishingSettings: _*)
  .settings(CodeCoverageSettings.settings: _*)
  .settings(defaultSettings(): _*)
  .configs(ItTest)
  .settings(
  inConfig(ItTest)(Defaults.itSettings ++ headerSettings(ItTest) ++ automateHeaderSettings(ItTest)),
  ItTest / fork := true,
  ItTest / unmanagedSourceDirectories := Seq((ItTest / baseDirectory).value / "it"),
  ItTest / unmanagedClasspath += baseDirectory.value / "resources",
  Runtime / unmanagedClasspath += baseDirectory.value / "resources",
  ItTest / javaOptions += "-Dlogger.resource=logback-test.xml",
  ItTest / parallelExecution := false,
  addTestReportOption(ItTest, directory = "int-test-reports")
)
  .settings(
    resolvers += Resolver.jcenterRepo
  )
  .settings(PlayKeys.playDefaultPort := 9767)
  .settings(SilencerSettings())

dependencyUpdatesFilter -= moduleFilter(organization = "com.typesafe.play")
dependencyUpdatesFilter -= moduleFilter(name = "scala-library")
dependencyUpdatesFilter -= moduleFilter(name = "flexmark-all")
dependencyUpdatesFilter -= moduleFilter(name = "scalatestplus-play")
dependencyUpdatesFilter -= moduleFilter(name = "scalatestplus-scalacheck")
dependencyUpdatesFilter -= moduleFilter(name = "bootstrap-backend-play-28")
dependencyUpdatesFailBuild := true
