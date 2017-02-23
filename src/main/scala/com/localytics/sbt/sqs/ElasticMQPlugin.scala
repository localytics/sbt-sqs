package com.localytics.sbt.sqs

import sbt._

object ElasticMQPlugin extends AutoPlugin {

  // auto enable plugin
  override val trigger = allRequirements

  // inject project keys
  val autoImport = ElasticMQKeys

  // inject defaults via projectSettings
  override lazy val projectSettings = ElasticMQKeys.baseElasticMQSettings
}
