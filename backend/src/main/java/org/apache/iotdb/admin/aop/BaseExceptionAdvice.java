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

package org.apache.iotdb.admin.aop;

import org.apache.iotdb.admin.common.exception.BaseException;
import org.apache.iotdb.admin.model.vo.BaseVO;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/** Global exception handler */
@Slf4j
@ControllerAdvice
public class BaseExceptionAdvice {

  @ExceptionHandler(BaseException.class)
  @ResponseBody
  public BaseVO handleBaseException(BaseException e) {
    log.error("Base exception:", e);
    BaseVO result = new BaseVO(e.getErrorCode(), e.getMessage(), null);
    return result;
  }
}
