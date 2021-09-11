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

import com.sun.management.OperatingSystemMXBean;
import org.junit.jupiter.api.Test;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.UnknownHostException;

/** @anthor fyx 2021/6/18 */
public class TestSystemInfo {

  @Test
  public void getCpuInfo() throws UnknownHostException {
    InetAddress ia = InetAddress.getLocalHost();
    System.out.println(ia.getHostAddress());
    System.out.println(ia.getHostName());
    OperatingSystemMXBean osmxb =
        (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
    int freeCores = osmxb.getAvailableProcessors();
    System.out.println(freeCores);
    System.out.println(Runtime.getRuntime().totalMemory() / 1024 / 1024);
    long totalMemorySize = osmxb.getTotalPhysicalMemorySize() / 1024 / 1024;
    System.out.println(totalMemorySize);
  }
}
