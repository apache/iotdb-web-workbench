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

package org.apache.iotdb.admin.model.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

import java.io.Serializable;
import java.util.List;

@Data
public class DeviceDTO implements Serializable {

  @NotEmpty(message = "物理量不能为空")
  private String timeseries;

  @NotEmpty(message = "数据类型不能为空")
  private String dataType;

  @NotEmpty(message = "编码方式不能为空")
  private String encoding;

  private String description;

  private String alias;

  @NotEmpty(message = "压缩方式不能为空")
  private String compression;

  private List<List<String>> tags;

  private List<List<String>> attributes;
}
