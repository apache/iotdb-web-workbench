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

# 接口规范

有时候，我们可能直接会定义如下的一些接口:

- /user/delete
- /user/add
- /user/update
- /user/select

但这样的接口太数据库化，读起来虽然比较容易。

## RESTful API

这里建议使用RESTful风格的API，例如：

- 查询 /user?name=tom GET
- 详情 /user/1 GET
- 创建 /user POST
- 修改 /user/1 PUT
- 删除 /user/1 DELETE 

它是一种互联网应用程序的API设计理念：URL定位资源，用HTTP动词（GET,POST,DELETE,PUT）描述操作。
