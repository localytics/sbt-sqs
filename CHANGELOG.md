Change Log
==========

All notable changes to this project will be documented in this file.

0.4.2 - 2017-02-23
---------------------
- Enable different settings for different configurations

0.4.1 - 2017-01-19
---------------------
- Fix PidUtil class collision when using multiple localytics/sbt-* projects

0.4.0 - 2016-12-13
---------------------
- Refactor to best practices and SBT 1.0 syntax
- Update to ElasticMQ 0.9.3

0.3.0 - 2016-04-28
---------------------
- Change test cleanup to support testOnly

0.2.0 - 2016-02-22
---------------------
* Add HeapSize configuration

0.1.0 - Initial release
---------------------
* Download and spin up a local ElasticMQ instance via sbt
* Allow setting ElasticMQ configs (NodeAddress, RestSQS, Queues) via sbt
