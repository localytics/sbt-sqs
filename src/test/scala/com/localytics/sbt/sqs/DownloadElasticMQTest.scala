package com.localytics.sbt.sqs

import java.io.File

import org.scalatest.FunSpec
import org.scalatest.Matchers

class DownloadElasticMQTest extends FunSpec with Matchers {

  describe("DeployDynamoDBLocal") {

    it("should identify a valid jar") {
      DownloadElasticMQ.validJar(new File(getClass.getResource("/valid.jar").getFile)) should be(true)
    }

    it("should identify an invalid jar") {
      DownloadElasticMQ.validJar(new File(getClass.getResource("/invalid.jar").getFile)) should be(false)
    }

  }

}
