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
import java.util.ArrayList;
import java.util.List;

@Data
public class NodeTreeVO implements Serializable {
  private String name;

  private List<NodeTreeVO> children;

  private Integer pageSize;

  private Integer pageNum;

  private Integer total;
  //  private List<String> childrenName;

  public NodeTreeVO(String name) {
    this.name = name;
  }

  public NodeTreeVO() {}

  public List<NodeTreeVO> initChildren() {
    if (children == null) {
      children = new ArrayList<>();
    }
    return children;
  }
}
