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
package org.apache.iotdb.admin.tool;

import org.apache.iotdb.admin.model.entity.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

/** date:2022/12/6 author:yzf project_name:backend */
@Slf4j
public class JJwtTool {
  private static String secret =
      "HSyJ0eXAiOiJKV1QasdfffffffSd3g8923402347523fffasdfasgwaegwaegawegawegawegawetwgewagagew"
          + "asdf23r23DEEasdfawef134t2fawt2g325gafasdfasdfiLCJhbGciOiJIUzI1NiJ9";

  public static String generateToken(User user) {
    log.info("user=" + user.toString());
    Date now = new Date();
    //    Calendar instance = Calendar.getInstance();
    //    instance.add(Calendar.HOUR_OF_DAY, 24);
    Date expireDate = new Date(new Date().getTime() + (1000 * 60 * 60 * 10));
    return Jwts.builder()
        .setHeaderParam("type", "JWT")
        .setSubject(user.getId() + "")
        .setIssuedAt(now) // 签发时间
        .claim("userId", user.getId())
        .claim("name", user.getName())
        .setExpiration(expireDate) // 过期时间
        .signWith(SignatureAlgorithm.HS512, secret)
        .compact();
  }

  /** 解析token */
  public static Claims getClaimsByToken(String token) {
    try {
      return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    } catch (Exception e) {
      System.out.println("validate is token error");
      return null;
    }
  }

  /** 判断 token 是否过期 */
  public boolean isTokenExpired(Date expiration) {
    return expiration.before(new Date());
  }
}
