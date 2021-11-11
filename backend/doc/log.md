# 日志配置

日志使用的是log4j，日志配置文件在src/main/resources/log4j2.xml中，log4j2.xml中默认路径写的是：

<property name="log.home">/data/applogs/app-test</property>

window系统请更改路径。

默认我们已经配置好了日志，如果启动的时候报`ERROR Unable to create file /data/applogs/app/projectservicedemo_debug.log java.io.IOException: Could not create directory /data/applogs/app`的错误，那么可能你没有/data目录的权限，这时候你需要换一个目录。
