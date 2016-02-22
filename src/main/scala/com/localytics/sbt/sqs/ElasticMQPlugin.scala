package com.localytics.sbt.sqs

import com.localytics.sbt.sqs.ElasticMQKeys._
import com.localytics.sbt.sqs.ElasticMQTasks._
import sbt._

object ElasticMQPlugin extends AutoPlugin {

  // auto enable plugin
  override val trigger = allRequirements

  // inject project keys
  val autoImport = ElasticMQKeys

  // inject defaults via projectSettings
  override lazy val projectSettings = Seq(

    elasticMQVersion    := "0.9.0-beta1",
    elasticMQDir        := file("elastic-mq"),
    elasticMQUrl        := s"https://s3-eu-west-1.amazonaws.com/softwaremill-public/elasticmq-server-${elasticMQVersion.value}.jar",
    elasticMQFileName   := s"elasticmq-server-${elasticMQVersion.value}.jar",
    elasticMQHeapSize   := None,

    nodeAddressConf     := NodeAddressConf(),
    restSQSConf         := RestSQSConf(),
    queuesConf          := Seq(),

    downloadElasticMQ   <<= downloadElasticMQTask,
    startElasticMQ      <<= startElasticMQTask,
    stopElasticMQ       <<= stopElasticMQTask
  )
}
