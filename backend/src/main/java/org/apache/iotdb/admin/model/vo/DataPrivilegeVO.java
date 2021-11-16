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

package org.apache.iotdb.admin.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class DataPrivilegeVO implements Serializable {
  // 0, 1, 2, 3 corresponding to root, storageGroup, device, timeseries
  private Integer type;
  private List<String> groupPaths;
  private List<String> devicePaths;
  private List<String> timeseriesPaths;
  private List<String> privileges;
  private List<String> allTimeseriesPaths;
  private List<NodeTreeVO> allDevicePaths;
  private List<NodeTreeVO> allGroupPaths;
}
