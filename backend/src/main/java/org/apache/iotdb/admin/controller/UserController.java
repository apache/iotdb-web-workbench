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

package org.apache.iotdb.admin.controller;

import org.apache.iotdb.admin.common.exception.BaseException;
import org.apache.iotdb.admin.common.exception.ErrorCode;
import org.apache.iotdb.admin.common.utils.AuthenticationUtils;
import org.apache.iotdb.admin.model.entity.User;
import org.apache.iotdb.admin.model.vo.BaseVO;
import org.apache.iotdb.admin.model.vo.ConnVO;
import org.apache.iotdb.admin.model.vo.ConnectionVO;
import org.apache.iotdb.admin.service.ConnectionService;
import org.apache.iotdb.admin.service.UserService;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.net.InetAddress;
import java.util.Calendar;
import java.util.List;

@RestController
@Api(value = "登录相关接口")
public class UserController {

  @Autowired private UserService userService;

  @Autowired private ConnectionService connectionService;

  private static final Logger logger = LoggerFactory.getLogger(UserController.class);

  @PostMapping("/login")
  @ApiOperation("登录")
  public BaseVO<ConnectionVO> login(
      @RequestParam("name") String name,
      @RequestParam("password") String password,
      HttpServletResponse response)
      throws BaseException {
    if (name == null || password == null || name.length() < 4 || password.length() < 4) {
      throw new BaseException(ErrorCode.WRONG_USER_PARAM, ErrorCode.WRONG_USER_PARAM_MSG);
    }
    User user = userService.login(name, password);
    int userId = user.getId();
    List<ConnVO> connVOs = connectionService.getAllConnections(userId);
    ConnectionVO connectionVO = new ConnectionVO(connVOs, userId, name);
    response.addHeader("Authorization", getToken(user));
    return BaseVO.success("登录成功", connectionVO);
  }

  @PostMapping("/save")
  @ApiOperation("创建用户  (未使用)")
  public BaseVO save(@RequestBody User user) throws BaseException {
    userService.insert(user);
    return BaseVO.success("保存成功", null);
  }

  @DeleteMapping("/delete")
  @ApiOperation("删除用户  (未使用)")
  public BaseVO delete(@RequestParam("userId") Integer userId, HttpServletRequest request)
      throws BaseException {
    AuthenticationUtils.userAuthentication(userId, request);
    userService.delete(userId);
    return BaseVO.success("删除成功", null);
  }

  @GetMapping("/get")
  @ApiOperation("二次登录获取用户信息")
  public BaseVO<User> getUser(HttpServletRequest request) {
    String authorization = request.getHeader("Authorization");
    DecodedJWT decode = JWT.decode(authorization);
    User user = new User();
    if (decode != null) {
      Integer userId = decode.getClaim("userId").asInt();
      String name = decode.getClaim("name").asString();
      user.setId(userId);
      user.setName(name);
    }
    return BaseVO.success("获取成功", user);
  }

  private String getToken(User user) throws BaseException {
    Calendar instance = Calendar.getInstance();
    try {
      instance.add(Calendar.HOUR, 24);
      String token =
          JWT.create()
              .withClaim("userId", user.getId())
              .withClaim("name", user.getName())
              .withExpiresAt(instance.getTime())
              .sign(Algorithm.HMAC256("IOTDB:" + InetAddress.getLocalHost().getHostAddress()));
      logger.info(user.getName() + "登录成功");
      return token;
    } catch (Exception e) {
      logger.info(e.getMessage());
      throw new BaseException(ErrorCode.GET_TOKEN_FAIL, ErrorCode.GET_TOKEN_FAIL_MSG);
    }
  }
}
