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

package org.apache.iotdb.admin.filter;

import org.apache.iotdb.admin.common.exception.BaseException;
import org.apache.iotdb.admin.common.exception.ErrorCode;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.net.InetAddress;
import java.net.UnknownHostException;

/** 拦截器 */
public class TokenFilter implements HandlerInterceptor {

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
      throws BaseException {
    JWTVerifier jwtVerifier;
    try {
      jwtVerifier =
          JWT.require(Algorithm.HMAC256("IOTDB:" + InetAddress.getLocalHost().getHostAddress()))
              .build();
    } catch (UnknownHostException e) {
      e.printStackTrace();
      throw new BaseException(ErrorCode.SET_JWT_FAIL, ErrorCode.SET_JWT_FAIL_MSG);
    }
    try {
      String authorization = request.getHeader("Authorization");
      jwtVerifier.verify(authorization);
    } catch (Exception e) {
      e.printStackTrace();
      throw new BaseException(ErrorCode.TOKEN_ERR, ErrorCode.TOKEN_ERR_MSG);
    }
    return true;
  }
}
