sbt-sqs
==============
Support for running [ElasitcMQ](https://github.com/adamw/elasticmq) in tests.

[![MIT license](https://img.shields.io/badge/license-MIT%20License-blue.svg)](LICENSE)

Installation
------------

Add the following to your `project/plugins.sbt` file:
```
addSbtPlugin("com.localytics" % "sbt-sqs" % "0.5.0")
```

sbt 1.0.0+ is supported by 0.5.0+ of this library.

if using sbt 0.13.6+ the recommended version of this library is 0.4.2.

sbt 0.13.5 should work with the right bintray resolvers.



Usage
-----
To use ElasticMQ in your project you can call `start-elastic-mq` and `stop-elastic-mq` directly in `sbt`.

To have ElasticMQ automatically start and stop around your tests
```
startElasticMQ := startElasticMQ.dependsOn(compile in Test).value
test in Test := (test in Test).dependsOn(startElasticMQ).value
testOnly in Test := (testOnly in Test).dependsOn(startElasticMQ).evaluated
testOptions in Test += elasticMQTestCleanup.value
```

`startElasticMQ` will download an ElasticMQ jar if one is not already present in the `elasticMQDir`.

Configuration
-------------
**Setup**

Set the download directory for the jar. Defaults to "elastic-mq".
```
elasticMQDir := file("dir-name")
```

Set the version of ElasticMQ to download. Defaults to "0.9.0-beta1".

NOTE: You can only create queues on startup in ElasticMQ versions `0.9.0-beta1` and above
```
elasticMQVersion := "0.9.0-beta1"
```

Set the URL to download ElasticMQ from. Defaults to "https://s3-eu-west-1.amazonaws.com/softwaremill-public/elasticmq-server-${elasticMQVersion.value}.jar"
```
elasticMQUrl := "http://someurl.net"
```

Set the name of the ElasticMQ jar. Defaults to "elasticmq-server-${elasticMQVersion.value}.jar"
```
elasticMQFileName := myfilename.jar"
```

Set the JVM heap size (specified in MB). Defaults to the JVM default.

```
elasticMQHeapSize := Some(1024)
```

**Node Address**

The Node Address is the externally visible address for the ElasticMQ node. Defaults to a Node Address using
`protocol = "http"`, `host = "localhost"`, `port = 9324`, `context-path = ""`.
```
nodeAddressConf := NodeAddressConf(protocol = "http", host = "localhost", port = 1111, contextPath = "/queues/")
```

**Rest SQS**

The Rest SQS config represents the binding port and host for SQS requests. The sqsLimits parameter can be
set to either `relaxed` or `strict`. Defaults to a RestSQSConf using `enabled = true`, `bindPort = 9324`,
`bindHostname = "0.0.0.0"`, `sqsLimits = "strict"`.
```
restSQSConf := RestSQSConf(enabled = true, bindPort = 2222, bindHostname = "0.0.0.0", sqsLimits = "strict")
```

**Queues**

For ElasticMQ versions 0.9.0-beta1 and beyond, you can configure ElasticMQ to create queues on startup.

Queues must be given a name and default to `visibilityTimeoutSecs = 10`, `delaySecs = 5`, `receiveMessagWaitSecs = 0`.
```
queuesConf := Seq(QueueConf(name = "myFirstQueue"), QueueConf(name = "second", delaySecs = 15))
```

Scopes
------

By default this plugin lives entirely in the `Global` scope. However, different settings for different scopes is possible. For instance, you can add the plugin to the `Test` scope using

```
inConfig(Test)(baseElasticMQSettings)
```

You can then adjust the settings within the `Test` scope using

```
(elasticMQDir in Test) := file("in-test/elatic-mq")
```

and you can execute the plugin tasks within the `Test` scope using

```
sbt test:start-elastic-mq
```

Similarly, you can have the plugin automatically start and stop around your tests using

```
startElasticMQ in Test := (startElasticMQ in Test).dependsOn(compile in Test).value
test in Test := (test in Test).dependsOn(startElasticMQ in Test).value
testOnly in Test := (testOnly in Test).dependsOn(startElasticMQ in Test).evaluated
testOptions in Test += (elasticMQTestCleanup in Test).value
```

Thanks
------
Thanks to [Adam Warski](https://github.com/adamw) for the excellent [ElasticMQ](https://github.com/adamw/elasticmq)!
