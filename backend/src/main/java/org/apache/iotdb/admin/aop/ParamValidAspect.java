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
import org.apache.iotdb.admin.common.exception.ErrorCode;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintViolation;
import javax.validation.Path;
import javax.validation.Validator;

import java.util.Set;

/** 参数验证 */
@Aspect
@Slf4j
@Component
public class ParamValidAspect {

  @Autowired private Validator validator;

  /** 定义校验包的未知 */
  @Pointcut("execution(* org.apache.iotdb.admin.controller..*.*(..))")
  public void pointcut() {}

  /** 处理参数校验错误的具体方法，抛出异常组件中的异常，遵循异常规范 */
  @Around("pointcut()")
  public Object doAround(ProceedingJoinPoint pjp) throws Throwable {
    Object[] args = pjp.getArgs();
    for (Object arg : args) {
      if (arg != null) {
        Set<ConstraintViolation<Object>> constraintViolations = validator.validate(arg);
        if (constraintViolations.size() > 0) {
          for (ConstraintViolation<Object> constraintViolation : constraintViolations) {
            Path property = constraintViolation.getPropertyPath();
            String name = property.iterator().next().getName();
            // 打印具体异常信息，对应校验框架注解中的message
            log.error("[" + name + "]" + constraintViolation.getMessage());
            throw new BaseException(
                ErrorCode.WRONG_PARAM, "参数错误:" + constraintViolation.getMessage());
          }
        }
      }
    }
    return pjp.proceed();
  }
}
