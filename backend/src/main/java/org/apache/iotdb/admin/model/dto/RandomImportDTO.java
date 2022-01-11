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
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import java.io.Serializable;
import java.util.Date;

@Data
public class RandomImportDTO implements Serializable {

  @NotNull(message = "The start time cannot be null")
  private Date startTime;

  @NotNull(message = "The step size cannot be null")
  @Min(value = 1, message = "The step size must be at least 1ms")
  private Integer stepSize;

  @NotNull(message = "The data line cannot be null")
  @Range(min = 1, max = 1000000, message = "The number of data lines is between 1 and 1000000")
  private Integer totalLine;
}
