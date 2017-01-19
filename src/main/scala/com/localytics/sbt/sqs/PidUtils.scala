package com.localytics.sbt.sqs

object PidUtils {

  private val ProcessIdRegex = """\d+ .*elasticmq-server""".r

  def extractElasticMQPid(input: String): Option[String] = ProcessIdRegex.findFirstIn(input).map(_.split(" ")(0))

  def osName: String = System.getProperty("os.name") match {
    case n: String if !n.isEmpty => n
    case _ => System.getProperty("os")
  }

  def killPidCommand(pid: String): String =
    if (osName.toLowerCase.contains("windows")) s"Taskkill /PID $pid /F" else s"kill $pid"

}
