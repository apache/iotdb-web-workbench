/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.iotdb.admin.controller;

import org.apache.iotdb.admin.common.exception.BaseException;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

/** 运维平台通过存活、就绪探针表示应用是否存活，就绪。这个类大家最好不好删除，url地址也不要改变 */
@Controller
public class HealthController {

  /** 存活探针，运维平台会每格一段时间，请求一下这个接口，如果这个接口返回200，那么表示存活， 如果多次返回500，则表示这个程序已经死了，会重启这个程序。 */
  @GetMapping(value = "/healthz/liveness")
  public void liveness(HttpServletResponse response) throws IOException, BaseException {
    response.setStatus(HttpServletResponse.SC_OK);
  }

  /** 就绪探针，运维平台会每格一段时间，请求一下这个接口，如果这个接口返回200，那么表示程序已经可用， 如果多次返回500，则表示这个程序未启动成功，会重启这个程序。 */
  @GetMapping(value = "/healthz/readiness")
  public void readiness(HttpServletResponse response) throws IOException {
    response.setStatus(HttpServletResponse.SC_OK);
  }
}
