ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.12.17"

lazy val root = (project in file("."))
  .settings(
    name := "ShopifyERP"
  )

//Contains all the needed dependencies to use Spark in Scala
libraryDependencies += "org.apache.spark" %% "spark-core" % "3.3.2"
libraryDependencies += "org.apache.spark" %% "spark-sql" % "3.3.2"
libraryDependencies += "org.apache.spark" %% "spark-avro" % "3.3.2"
libraryDependencies += "org.apache.spark" %% "spark-streaming-kafka-0-10" % "3.3.2"
libraryDependencies += "org.apache.spark" %% "spark-streaming" % "3.3.2"
libraryDependencies += "org.apache.spark" %% "spark-sql-kafka-0-10" % "3.3.2"
libraryDependencies += "com.datastax.spark" %% "spark-cassandra-connector" % "3.1.0"
libraryDependencies += "joda-time" % "joda-time" % "2.12.5"


//These are Java options needed so that the Scala code can be run on the JVM
javaOptions ++= Seq(
  "--add-opens=java.base/sun.nio.ch=ALL-UNNAMED",
  "--add-exports java.base/sun.nio.ch=ALL-UNNAMED"
)
