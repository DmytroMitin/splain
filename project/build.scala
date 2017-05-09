package tryp

import sbt._, Keys._

import bintray.BintrayKeys._

object SplainDeps
extends Deps
{
  val splain = ids(
    d(scalaOrganization.value % "scala-compiler" % scalaVersion.value % "provided"),
    "org.specs2" %% "specs2-core" % "3.8.6" % "test",
    "com.chuusai" %% "shapeless" % "2.3.2" % "test"
  )
}

object Build
extends MultiBuild("splain", deps = SplainDeps)
{
  override def defaultBuilder =
    super.defaultBuilder(_)
      .settingsV(
        scalaVersion := "2.12.2",
        crossScalaVersions ++= List("2.10.6", "2.11.11")
      )

  lazy val splain = "splain"
    .bintray
    .settingsV(
      name := "splain",
      licenses += ("MIT", url("http://opensource.org/licenses/MIT")),
      bintrayRepository in bintray := "releases",
      publishMavenStyle := true,
      fork := true,
      javaOptions in Test ++= {
        val jar = (Keys.`package` in Compile).value.getAbsolutePath
        val tests = baseDirectory.value / "tests"
        List(s"-Dsplain.jar=$jar", s"-Dsplain.tests=$tests")
      },
      (unmanagedSourceDirectories in Compile) ++= {
        CrossVersion.partialVersion(scalaVersion.value) match {
          case Some((2, y)) if y >= 11 =>
            List(baseDirectory.value / "src-2.11+")
          case _ => List()
        }
      }
  )
}
