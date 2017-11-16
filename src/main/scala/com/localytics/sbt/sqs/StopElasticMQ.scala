package com.localytics.sbt.sqs

import sbt.Keys

import scala.sys.process._

object StopElasticMQ {

  def apply(streamz: Keys.TaskStreams): Unit = {
    PidUtils.extractPid("jps -l".!!) match {
      case Some(pid) =>
        streamz.log.info("Stopping ElasticMQ")
        PidUtils.killPidCommand(pid).!
      case None =>
        streamz.log.warn("Cannot find ElasticMQ")
    }
  }

}
