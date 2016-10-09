name := "fMod"

version := "0.1"

scalaVersion := "2.11.8"

resolvers += "mvnrepository" at "http://mvnrepository.com/artifact/"


libraryDependencies ++= {
  Seq(
    "org.scalatest" %% "scalatest" % "2.2.6" % Test
  )
}

dependencyOverrides += "org.scala-lang" % "scala-compiler" % scalaVersion.value

fork in run := true