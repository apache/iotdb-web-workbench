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
import org.apache.iotdb.admin.model.dto.ConnectionDTO;
import org.apache.iotdb.admin.model.entity.Connection;
import org.apache.iotdb.admin.model.vo.BaseVO;
import org.apache.iotdb.admin.model.vo.ConnVO;
import org.apache.iotdb.admin.model.vo.ConnectionVO;
import org.apache.iotdb.admin.service.ConnectionService;
import org.apache.iotdb.session.Session;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;

@RestController
@Api(value = "Connection related")
public class ConnectionController {

  @Autowired private ConnectionService connectionService;

  @PostMapping("/servers")
  @ApiOperation("Upsert connection")
  public BaseVO saveOrUpdateConnection(
      @RequestBody Connection connection, HttpServletRequest request) throws BaseException {
    AuthenticationUtils.userAuthentication(connection.getUserId(), request);
    if (connection.getId() != null) {
      connectionService.update(connection);
      return BaseVO.success("Update successfully", null);
    }
    connectionService.insert(connection);
    return BaseVO.success("Save successfully", null);
  }

  @DeleteMapping("/servers/{serverId}")
  @ApiOperation("Delete connection")
  public BaseVO deleteConnection(
      @PathVariable("serverId") Integer serverId, HttpServletRequest request) throws BaseException {
    Integer userId = AuthenticationUtils.getUserId(request);
    connectionService.check(serverId, userId);
    connectionService.deleteById(serverId, userId);
    return BaseVO.success("Delete successfully", null);
  }

  @GetMapping("/servers/{serverId}")
  @ApiOperation("Get detailed information of the specified connection")
  public BaseVO<Connection> getConnection(
      @PathVariable("serverId") Integer serverId, HttpServletRequest request) throws BaseException {
    Integer userId = AuthenticationUtils.getUserId(request);
    connectionService.check(serverId, userId);
    return BaseVO.success("Get successfully", connectionService.getById(serverId));
  }

  @GetMapping("/servers")
  @ApiOperation("Get all connection")
  public BaseVO<ConnectionVO> getAllConnections(
      @RequestParam("userId") Integer userId, HttpServletRequest request) throws BaseException {
    AuthenticationUtils.userAuthentication(userId, request);
    List<ConnVO> connVOs = connectionService.getAllConnections(userId);
    ConnectionVO connectionVO = new ConnectionVO(connVOs, userId, null);
    return BaseVO.success("Get successfully", connectionVO);
  }

  @PostMapping("/test")
  @ApiOperation("Connectivity test")
  public BaseVO testConnection(@RequestBody ConnectionDTO conn) throws BaseException {
    Socket socket = null;
    try {
      socket = new Socket();
      socket.connect(new InetSocketAddress(conn.getHost(), conn.getPort()), 5000);
    } catch (Exception e) {
      throw new BaseException(ErrorCode.TEST_CONN_FAIL, ErrorCode.TEST_CONN_FAIL_MSG);
    } finally {
      try {
        if (socket != null) {
          socket.close();
        }
      } catch (Exception e) {
        throw new BaseException(ErrorCode.TEST_CONN_FAIL, ErrorCode.TEST_CONN_FAIL_MSG);
      }
    }
    Session session = null;
    try {
      session = new Session(conn.getHost(), conn.getPort(), conn.getUsername(), conn.getPassword());
      session.open();
    } catch (Exception e) {
      throw new BaseException(ErrorCode.TEST_CONN_FAIL_PWD, ErrorCode.TEST_CONN_FAIL_PWD_MSG);
    } finally {
      try {
        if (session != null) {
          session.close();
        }
      } catch (Exception e) {
        throw new BaseException(ErrorCode.TEST_CONN_FAIL_PWD, ErrorCode.TEST_CONN_FAIL_PWD_MSG);
      }
    }
    return BaseVO.success("Connection successfully", null);
  }
}
