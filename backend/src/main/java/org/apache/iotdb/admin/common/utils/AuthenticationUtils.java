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

package org.apache.iotdb.admin.common.utils;

import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import org.apache.iotdb.admin.common.exception.BaseException;
import org.apache.iotdb.admin.common.exception.ErrorCode;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;

/** Validation tool class */
public class AuthenticationUtils {

  public static void userAuthentication(Integer userId, HttpServletRequest request)
      throws BaseException {
    if (userId == null) {
      throw new BaseException(ErrorCode.NO_USER, ErrorCode.NO_USER_MSG);
    }
    //DecodedJWT authorization = JWT.decode(request.getHeader("Authorization"));
    try {
      JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256("IOTDB:" + InetAddress.getLocalHost().getHostAddress())).build();
      DecodedJWT authorization = jwtVerifier.verify(request.getHeader("Authorization"));
      Integer tokenUserId = authorization.getClaim("userId").asInt();
      if (!tokenUserId.equals(userId)) {
        throw new BaseException(ErrorCode.USER_AUTH_FAIL, ErrorCode.USER_AUTH_FAIL_MSG);
      }
    } catch (Exception e) {
    throw new BaseException(ErrorCode.USER_AUTH_FAIL, ErrorCode.USER_AUTH_FAIL_MSG);
    }
  }

  public static Integer getUserId(HttpServletRequest request) {
    DecodedJWT authentication = JWT.decode(request.getHeader("Authorization"));
    return authentication.getClaim("userId").asInt();
  }
}
