package com.localytics.sbt.sqs

import java.io.File

import com.localytics.sbt.PidUtils
import com.localytics.sbt.sqs.ElasticMQKeys._
import sbt.Keys._
import sbt._

import scala.util.Try

object StartElasticMQ {

  def apply(jarDir: File, jarFile: String, heapSize: Option[Int], nodeAddress: NodeAddressConf, restSQS: RestSQSConf, queues: Seq[QueueConf], streamz: TaskStreams): String = {
    val args =
      Seq("java") ++
        heapSize.map(mb => Seq(s"-Xms${mb}m", s"-Xmx${mb}m")).getOrElse(Nil) ++
        argsFromNodeAddress(nodeAddress) ++
        argsFromRestSQS(restSQS) ++
        argsFromQueues(queues) ++
        Seq("-jar", new File(jarDir, jarFile).getAbsolutePath)

    if (isElasticMQRunning(restSQS.bindHostname, restSQS.bindPort)) {
      streamz.log.warn(s"ElasticMQ is already running at ${restSQS.bindHostname} on ${restSQS.bindPort}")
    } else {
      streamz.log.info("Starting ElasticMQ")
      Process(args).run()
      do {
        streamz.log.info(s"Waiting for ElasticMQ to respond at '${restSQS.bindHostname}' and port '${restSQS.bindPort}'")
        Thread.sleep(500)
      } while (!isElasticMQRunning(restSQS.bindHostname, restSQS.bindPort))
    }
    PidUtils.extractElasticMQPid("jps -l".!!).getOrElse {
      sys.error("Cannot find ElasticMQ PID")
    }
  }

  private def argsFromNodeAddress(nodeAddress: NodeAddressConf): Seq[String] =
    Seq(s"-Dnode-address.protocol=${nodeAddress.protocol}",
      s"-Dnode-address.host=${nodeAddress.host}",
      s"-Dnode-address.port=${nodeAddress.port}",
      s"-Dnode-address.context-path=${nodeAddress.contextPath}")

  private def argsFromRestSQS(restSQS: RestSQSConf): Seq[String] =
    Seq(s"-Drest-sqs.enabled=${restSQS.enabled}",
      s"-Drest-sqs.bind-port=${restSQS.bindPort}",
      s"-Drest-sqs.bind-hostname=${restSQS.bindHostname}",
      s"-Drest-sqs.sqs-limits=${restSQS.sqsLimits}")

  private def argsFromQueues(queues: Seq[QueueConf]): Seq[String] =
    queues.foldLeft(Seq[String]()) { case (seq, queue) =>
      seq ++
        Seq(s"-Dqueues.${queue.name}.defaultVisibilityTimeout=${queue.visibilityTimeoutSecs} seconds",
          s"-Dqueues.${queue.name}.delay=${queue.delaySecs} seconds",
          s"-Dqueues.${queue.name}.receiveMessageWait=${queue.receiveMessageWaitSecs} seconds")
    }

  private def isElasticMQRunning(endpoint: String, port: Int): Boolean = Try(new java.net.Socket(endpoint, port).close()).isSuccess

}
