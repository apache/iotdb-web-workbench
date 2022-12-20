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
import org.apache.iotdb.admin.tool.JJwtTool;

import io.jsonwebtoken.Claims;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.casbin.casdoor.entity.CasdoorUser;
import org.casbin.casdoor.service.CasdoorAuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.List;

@RestController
@Api(value = "User related")
public class UserController {

  @Autowired private UserService userService;

  @Autowired private ConnectionService connectionService;

  private static final Logger logger = LoggerFactory.getLogger(UserController.class);

  @Autowired private CasdoorAuthService casdoorAuthService;

  @PostMapping("/login")
  @ApiOperation("login")
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
    response.addHeader("Authorization", JJwtTool.generateToken(user));
    return BaseVO.success("Login  successful", connectionVO);
  }

  @PostMapping("/getCasdoorUrl")
  @ApiOperation("Get Casdoor Url")
  public BaseVO<String> getCasdoorUrl(HttpServletRequest request, HttpServletResponse response)
      throws BaseException {
    String origin = request.getParameter("origin");
    String url = casdoorAuthService.getSigninUrl(origin);
    return BaseVO.success("Get Url successful", url);
  }

  @PostMapping("/loginWithCasdoor")
  @ApiOperation("loginWithCasdoor")
  public BaseVO<ConnectionVO> loginWithCasdoor(
      @RequestParam("code") String code,
      @RequestParam("state") String state,
      HttpServletResponse response)
      throws BaseException {
    String token = casdoorAuthService.getOAuthToken(code, state);
    CasdoorUser casdoorUser = casdoorAuthService.parseJwtToken(token);
    User user = new User();
    user.setId(casdoorUser.getRanking());
    user.setName(casdoorUser.getName());
    int userId = user.getId();
    String name = user.getName();
    List<ConnVO> connVOs = connectionService.getAllConnections(userId);
    ConnectionVO connectionVO = new ConnectionVO(connVOs, userId, name);
    response.addHeader("Authorization", JJwtTool.generateToken(user));
    return BaseVO.success("Login  successful", connectionVO);
  }

  @PostMapping("/save")
  @ApiOperation("Create user (not used)")
  public BaseVO save(@RequestBody User user) throws BaseException {
    userService.insert(user);
    return BaseVO.success("Save successful", null);
  }

  @DeleteMapping("/delete")
  @ApiOperation("Delete user (not used)")
  public BaseVO delete(@RequestParam("userId") Integer userId, HttpServletRequest request)
      throws BaseException {
    AuthenticationUtils.userAuthentication(userId, request);
    userService.delete(userId);
    return BaseVO.success("Delete successfully", null);
  }

  @GetMapping("/get")
  @ApiOperation("Get information of user")
  public BaseVO<User> getUser(HttpServletRequest request) {
    String authorization = request.getHeader("Authorization");
    Claims claimsByToken = JJwtTool.getClaimsByToken(authorization);
    User user = new User();
    if (claimsByToken != null) {
      Integer userId = claimsByToken.get("userId", Integer.class);
      String name = claimsByToken.get("name", String.class);
      user.setId(userId);
      user.setName(name);
    }
    return BaseVO.success("Get successfully", user);
  }

  @GetMapping("/")
  public String welcome() {
    String str =
        "<!DOCTYPE html>\n"
            + "<html lang=\"ch\">\n"
            + "<head>\n"
            + "    <meta charset=\"UTF-8\">\n"
            + "    <title>welcome</title>\n"
            + "</head>\n"
            + "<body>\n"
            + "<h1>You have successfully started IoTDB-Workbench backend application!</h1>\n"
            + "<h2>For a better experience with IOTDB-Workbench, Please refer to the <a href=\"https://github.com/loveher147/iotdb-admin/blob/main/backend/doc/deploy.md\">deployment documentation</a> for deployment</h2>\n"
            + "</body>\n"
            + "</html>";
    return str;
  }
}
