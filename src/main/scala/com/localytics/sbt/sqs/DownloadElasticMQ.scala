package com.localytics.sbt.sqs

import java.net.URL
import java.util.zip.ZipFile

import sbt.File
import sbt.Keys._

import scala.sys.process._
import scala.util.Try

object DownloadElasticMQ {

  private[sqs] def validJar(file: File): Boolean = Try(new ZipFile(file)).isSuccess

  def apply(version: String, url: String, dir: File, file: String, streamz: TaskStreams): File = {
    val outputFile = new File(dir, file)
    if (!dir.exists()) {
      streamz.log.info(s"Creating ElasticMQ directory $dir")
      dir.mkdirs()
    }
    if (!outputFile.exists() || !validJar(outputFile)) {
      streamz.log.info(s"Downloading ElasticMQ from [$url] to [${outputFile.getAbsolutePath}]")
      (new URL(url) #> outputFile).!!
    }
    if (!validJar(outputFile)) sys.error(s"Invalid jar file at [${outputFile.getAbsolutePath}]")
    outputFile
  }

}
