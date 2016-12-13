package com.localytics.sbt.sqs

import com.localytics.sbt.PidUtils
import sbt._

object StopElasticMQ {

  def apply(streamz: Keys.TaskStreams): Unit = {
    PidUtils.extractElasticMQPid("jps -l".!!) match {
      case Some(pid) =>
        streamz.log.info("Stopping ElasticMQ")
        PidUtils.killPidCommand(pid).!
      case None =>
        streamz.log.warn("Cannot find ElasticMQ")
    }
  }

}
