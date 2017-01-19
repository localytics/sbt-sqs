package com.localytics.sbt.sqs

import java.io.File

import sbt._

object ElasticMQKeys {

  case class QueueConf(name: String, visibilityTimeoutSecs: Int = 10, delaySecs: Int = 5, receiveMessageWaitSecs: Int = 0)
  case class NodeAddressConf(protocol: String = "http", host: String = "localhost", port: Int = 9324, contextPath: String = "")
  case class RestSQSConf(enabled: Boolean = true, bindPort: Int = 9324, bindHostname: String = "0.0.0.0", sqsLimits: String = "strict")

  lazy val elasticMQDir            = settingKey[File]("The directory ElasticMQ will be downloaded to. Defaults to 'elastic-mq'")
  lazy val elasticMQUrl            = settingKey[String]("The URL to download ElasticMQ from. Defaults to 'https://s3-eu-west-1.amazonaws.com/softwaremill-public/elasticmq-server-{version}.jar'")
  lazy val elasticMQFileName       = settingKey[String]("The name of the ElasticMQ jar file. Defaults to 'elasticmq-server-{version}.jar'")
  lazy val elasticMQVersion        = settingKey[String]("The version of ElasticMQ. Defaults to '0.9.0-beta1'")
  lazy val elasticMQHeapSize       = settingKey[Option[Int]]("The size of the heap for ElasticMQ. Defaults to the JVM default.")

  lazy val nodeAddressConf         = settingKey[NodeAddressConf]("The NodeAddress configuration. Defaults to NodeAddressConf(protocol = \"http\", host = \"localhost\", port = 9324, contextPath = \"\").")
  lazy val restSQSConf             = settingKey[RestSQSConf]("The RestSQS configuration. Defaults to RestSQSConf(enabled = true, bindPort = 9324, bindHostname = \"0.0.0.0\", sqsLimits = \"strict\")")
  lazy val queuesConf              = settingKey[Seq[QueueConf]]("QueueConfs to initialize queues on startup. No queues are created by default. QueueConf(name, visibilityTimeoutSecs = 10, delaySecs = 5, receivedMessageWaitSecs = 0)")

  lazy val downloadElasticMQ       = TaskKey[File]("download-elastic-mq")
  lazy val startElasticMQ          = TaskKey[String]("start-elastic-mq")
  lazy val stopElasticMQ           = TaskKey[Unit]("stop-elastic-mq")
  lazy val elasticMQTestCleanup    = TaskKey[Tests.Cleanup]("elastic-mq-test-cleanup")
}
