package com.localytics.sbt.sqs

import java.io.File
import java.net.URL

import com.localytics.sbt.sqs.ElasticMQKeys._
import sbt.Keys._
import sbt._

import scala.util.Try

object ElasticMQTasks {

  def downloadElasticMQTask = (elasticMQVersion, elasticMQUrl, elasticMQDir, elasticMQFileName, streams) map {
    case (version, url, dir, file, streamz) =>
      import sys.process._
      val outputFile = new File(dir, file)
      if (!dir.exists()) {
        streamz.log.info(s"Creating ElasticMQ directory $dir")
        dir.mkdirs()
      }
      if (!outputFile.exists()) {
        streamz.log.info(s"Downloading ElasticMQ from [$url] to [${outputFile.getAbsolutePath}]")
        (new URL(url) #> outputFile).!!
      }
      if (!outputFile.exists()) {
        sys.error(s"Cannot find ElasticMQ at [${outputFile.getAbsolutePath}]")
      }
  }

  def startElasticMQTask = (downloadElasticMQ, elasticMQDir, elasticMQFileName, elasticMQHeapSize,
                            nodeAddressConf, restSQSConf, queuesConf, streams) map {
    case (_, jarDir, jarFile, heapSize, nodeAddress, restSQS, queues, streamz) =>
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
        streamz.log.info("Waiting for ElasticMQ")
        while (!isElasticMQRunning(restSQS.bindHostname, restSQS.bindPort)) {
          streamz.log.info(s"Waiting for ElasticMQ to respond at '${restSQS.bindHostname}' and port '${restSQS.bindPort}'")
          Thread.sleep(500)
        }
      }
      if (extractElasticMQPid.isEmpty) {
        sys.error("Cannot find ElasticMQ PID")
      }
  }

  def stopElasticMQTask = (streams) map {
    case (streamz) => stopElasticMQHelper(streamz)
  }

  def elasticMQTestCleanupTask = (streams) map {
    case (streamz) => Tests.Cleanup(() => stopElasticMQHelper(streamz))
  }

  def stopElasticMQHelper(streamz: Keys.TaskStreams) = {
    extractElasticMQPid match {
      case Some(pid) =>
        streamz.log.info("Stopping ElasticMQ")
        killPidCommand(pid).!
        ()
      case None =>
        streamz.log.warn("Cannot find ElasticMQ")
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
    queues.foldLeft(Seq[String]()){ case (seq, queue) =>
      seq ++
      Seq(s"-Dqueues.${queue.name}.defaultVisibilityTimeout=${queue.visibilityTimeoutSecs} seconds",
          s"-Dqueues.${queue.name}.delay=${queue.delaySecs} seconds",
          s"-Dqueues.${queue.name}.receiveMessageWait=${queue.receiveMessageWaitSecs} seconds")
    }

  private val ProcessIdRegex = """\d+ .*elasticmq-server""".r

  private def extractElasticMQPid: Option[String] = ProcessIdRegex.findFirstIn("jps -l".!!).map(_.split(" ")(0))

  private def killPidCommand(pid: String): String = {
    val osName = System.getProperty("os.name") match {
      case n: String if !n.isEmpty => n
      case _ => System.getProperty("os")
    }
    if (osName.toLowerCase.contains("windows")) {
      s"Taskkill /PID $pid /F"
    } else {
      s"kill $pid"
    }
  }

  def isElasticMQRunning(endpoint: String, port: Int): Boolean = {
    Try {
      val socket = new java.net.Socket(endpoint, port)
      socket.close()
    }.isSuccess
  }

}
