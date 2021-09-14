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
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Pattern;

import java.io.Serializable;

/** 传输User信息类 */
@Data
public class IotDBUser implements Serializable {

  @Length(min = 4, message = "长度必须大于等于4")
  @Pattern(regexp = "^[^ ]+$", message = "不能包含空格")
  private String userName;

  @Length(min = 4, message = "长度必须大于等于4")
  @Pattern(regexp = "^[^ ]+$", message = "不能包含空格")
  private String password;

  //    private List<String> privileges;
  //
  //    private List<String> roles;
}
