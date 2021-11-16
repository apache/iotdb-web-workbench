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

package org.apache.iotdb.admin.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Null;
import javax.validation.constraints.Pattern;

import java.io.Serializable;

@Data
public class User implements Serializable {

  @Null
  @TableId(type = IdType.AUTO)
  private Integer id;

  @NotBlank
  @Length(min = 4, message = "The account name must contain at least 4 characters")
  @Pattern(regexp = "^[^ ]+$", message = "The account name cannot contain spaces")
  private String name;

  @NotBlank
  @Length(min = 6, message = "The account password must contain at least 6 characters")
  @Pattern(regexp = "^[^ ]+$", message = "The account password cannot contain spaces")
  private String password;
}
