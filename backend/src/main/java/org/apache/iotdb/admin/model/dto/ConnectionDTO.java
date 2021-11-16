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
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import java.io.Serializable;

@Data
public class ConnectionDTO implements Serializable {

  @NotBlank(message = "The host address cannot be empty or null")
  @Pattern(
      regexp =
          "^((2(5[0-5]{1}|[0-4]\\d{1})|[0-1]?\\d{1,2})(\\.(2(5[0-5]{1}|[0-4]\\d{1})|[0-1]?\\d{1,2})){3})|(localhost)$",
      message = "The host is invalid")
  private String host;

  @NotNull(message = "The port number cannot be null")
  @Range(min = 0, max = 65535, message = "The host is invalid")
  private Integer port;

  @NotBlank(message = "The username cannot be empty or null")
  @Length(min = 4, message = "The username must contain at least 4 characters")
  @Pattern(regexp = "^[^ ]+$", message = "The username cannot contain spaces")
  private String username;

  @NotBlank(message = "The password cannot be empty or null")
  @Length(min = 4, message = "The password must contain at least 4 characters")
  @Pattern(regexp = "^[^ ]+$", message = "The username cannot contain spaces")
  private String password;
}
