name := "gravity"

version := "0.0.1"

scalaVersion := "2.11.7"

libraryDependencies += "com.squants"  %% "squants"  % "0.5.3"

libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.6" % "test"

resolvers += "Janos" at "http://maven.janosgyerik.com/"

libraryDependencies += "com.janosgyerik" % "microbench" % "1.0"
