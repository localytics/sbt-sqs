package com.localytics.sbt

import org.scalatest.FunSpec
import org.scalatest.Matchers

class PidUtilsTest extends FunSpec with Matchers {

  describe("PidUtils") {

    it("should extract PID correctly") {
      val jpsOutput =
        """
          |86656 /Users/person/code/repository/aws-mocks/elastic-mq/elasticmq-server-0.9.3.jar
          |83876
          |87496 sun.tools.jps.Jps
          |86379 /usr/local/Cellar/sbt/0.13.13/libexec/sbt-launch.jar
        """.stripMargin
      PidUtils.extractElasticMQPid(jpsOutput) should equal(Some("86656"))
    }

  }

}
