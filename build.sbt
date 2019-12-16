/*
 * Copyright 2019 HM Revenue & Customs
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

import TestPhases.oneForkedJvmPerTest
import uk.gov.hmrc.DefaultBuildSettings.{addTestReportOption, defaultSettings}
import uk.gov.hmrc.{SbtArtifactory, SbtAutoBuildPlugin}
import uk.gov.hmrc.sbtdistributables.SbtDistributablesPlugin.publishingSettings
import uk.gov.hmrc.versioning.SbtGitVersioning
import uk.gov.hmrc.versioning.SbtGitVersioning.autoImport.majorVersion

val appName = "individual-calculations-api"

lazy val microservice = Project(appName, file("."))
  .enablePlugins(play.sbt.PlayScala, SbtAutoBuildPlugin, SbtGitVersioning, SbtDistributablesPlugin, SbtArtifactory)
  .settings(
    libraryDependencies ++= AppDependencies.compile ++ AppDependencies.test(),
    dependencyOverrides ++= AppDependencies.overrides,
    retrieveManaged := true,
    evictionWarningOptions in update := EvictionWarningOptions.default.withWarnScalaVersionEviction(false),
    scalaVersion := "2.11.12"
  )
  .settings(
    unmanagedResourceDirectories in Compile += baseDirectory.value / "resources"
  )
  .settings(version := "1.0.0")
  .settings(publishingSettings: _*)
  .settings(CodeCoverageSettings.settings: _*)
  .settings(defaultSettings(): _*)
  .configs(IntegrationTest)
  .settings(inConfig(IntegrationTest)(Defaults.itSettings): _*)
  .settings(
    Keys.fork in IntegrationTest := true,
    unmanagedSourceDirectories in IntegrationTest := (baseDirectory in IntegrationTest) (base => Seq(base / "it", base / "test")).value,
    javaOptions in IntegrationTest += "-Dlogger.resource=logback-test.xml",
    parallelExecution in IntegrationTest := true,
    addTestReportOption(IntegrationTest, "int-test-reports")
  )
  .settings(
    resolvers += Resolver.jcenterRepo
  )
  .settings(PlayKeys.playDefaultPort := 9767)
