<!--

    Licensed to the Apache Software Foundation (ASF) under one
    or more contributor license agreements.  See the NOTICE file
    distributed with this work for additional information
    regarding copyright ownership.  The ASF licenses this file
    to you under the Apache License, Version 2.0 (the
    "License"); you may not use this file except in compliance
    with the License.  You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing,
    software distributed under the License is distributed on an
    "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
    KIND, either express or implied.  See the License for the
    specific language governing permissions and limitations
    under the License.

-->

# 日志配置

日志使用的是log4j，日志配置文件在src/main/resources/log4j2.xml中，log4j2.xml中默认路径写的是：

<property name="log.home">/data/applogs/app-test</property>

window系统请更改路径。

默认我们已经配置好了日志，如果启动的时候报`ERROR Unable to create file /data/applogs/app/projectservicedemo_debug.log java.io.IOException: Could not create directory /data/applogs/app`的错误，那么可能你没有/data目录的权限，这时候你需要换一个目录。
