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
@Api(value = "连接相关接口")
public class ConnectionController {

  @Autowired private ConnectionService connectionService;

  @PostMapping("/servers")
  @ApiOperation("保存或更新连接")
  public BaseVO saveOrUpdateConnection(
      @RequestBody Connection connection, HttpServletRequest request) throws BaseException {
    AuthenticationUtils.userAuthentication(connection.getUserId(), request);
    if (connection.getId() != null) {
      connectionService.update(connection);
      return BaseVO.success("更新成功", null);
    }
    connectionService.insert(connection);

    return BaseVO.success("保存成功", null);
  }

  @DeleteMapping("/servers/{serverId}")
  @ApiOperation("删除连接")
  public BaseVO deleteConnection(
      @PathVariable("serverId") Integer serverId, HttpServletRequest request) throws BaseException {
    Integer userId = AuthenticationUtils.getUserId(request);
    connectionService.check(serverId, userId);
    connectionService.deleteById(serverId, userId);
    return BaseVO.success("删除成功", null);
  }

  @GetMapping("/servers/{serverId}")
  @ApiOperation("获取连接具体配置")
  public BaseVO<Connection> getConnection(
      @PathVariable("serverId") Integer serverId, HttpServletRequest request) throws BaseException {
    Integer userId = AuthenticationUtils.getUserId(request);
    connectionService.check(serverId, userId);
    return BaseVO.success("获取成功", connectionService.getById(serverId));
  }

  @GetMapping("/servers")
  @ApiOperation("获取所有连接")
  public BaseVO<ConnectionVO> getAllConnections(
      @RequestParam("userId") Integer userId, HttpServletRequest request) throws BaseException {
    AuthenticationUtils.userAuthentication(userId, request);
    List<ConnVO> connVOs = connectionService.getAllConnections(userId);
    ConnectionVO connectionVO = new ConnectionVO(connVOs, userId, null);
    return BaseVO.success("获取成功", connectionVO);
  }

  @PostMapping("/test")
  @ApiOperation("连通性测试")
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
    return BaseVO.success("连通成功", null);
  }
}
