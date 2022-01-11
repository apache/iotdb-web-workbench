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

package org.apache.iotdb.admin.service.impl;

import org.apache.iotdb.admin.common.exception.BaseException;
import org.apache.iotdb.admin.common.exception.ErrorCode;
import org.apache.iotdb.admin.mapper.ConnectionMapper;
import org.apache.iotdb.admin.model.entity.Connection;
import org.apache.iotdb.admin.model.vo.ConnVO;
import org.apache.iotdb.admin.service.ConnectionService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ConnectionServiceImpl extends ServiceImpl<ConnectionMapper, Connection>
    implements ConnectionService {

  @Autowired private ConnectionMapper connectionMapper;

  @Override
  public List<ConnVO> getAllConnections(Integer userId) {
    QueryWrapper queryWrapper = new QueryWrapper();
    queryWrapper.eq("user_id", userId);
    List<Connection> connections = connectionMapper.selectList(queryWrapper);
    List<ConnVO> ConnVOs = new ArrayList();
    for (Connection connection : connections) {
      ConnVOs.add(
          new ConnVO(
              connection.getId(),
              connection.getAlias(),
              connection.getHost(),
              connection.getPort(),
              connection.getUsername(),
              connection.getPassword()));
    }
    return ConnVOs;
  }

  @Override
  public void insert(Connection connection) throws BaseException {
    String alias = connection.getAlias();
    Integer userId = connection.getUserId();
    QueryWrapper queryWrapper = new QueryWrapper();
    queryWrapper.eq("user_id", userId);
    queryWrapper.eq("alias", alias);
    Connection existConnection = connectionMapper.selectOne(queryWrapper);
    // Alias are unique
    if (existConnection != null) {
      throw new BaseException(ErrorCode.ALIAS_REPEAT, ErrorCode.ALIAS_REPEAT_MSG);
    }
    int flag = connectionMapper.insert(connection);
    if (flag <= 0) {
      throw new BaseException(ErrorCode.INSERT_CONN_FAIL, ErrorCode.INSERT_CONN_FAIL_MSG);
    }
  }

  @Override
  public void deleteById(Integer serverId, Integer userId) throws BaseException {
    try {
      connectionMapper.deleteById(serverId);
    } catch (Exception e) {
      throw new BaseException(ErrorCode.DELETE_CONN_FAIL, ErrorCode.DELETE_CONN_FAIL_MSG);
    }
  }

  @Override
  public Connection getById(Integer serverId) throws BaseException {
    QueryWrapper queryWrapper = new QueryWrapper();
    queryWrapper.eq("id", serverId);
    try {
      Connection connection = connectionMapper.selectOne(queryWrapper);
      if (connection == null) {
        throw new BaseException(ErrorCode.NO_CONN, ErrorCode.NO_CONN_MSG);
      }
      return connection;
    } catch (Exception e) {
      throw new BaseException(ErrorCode.GET_CONN_FAIL, ErrorCode.GET_CONN_FAIL_MSG);
    }
  }

  @Override
  public void check(Integer serverId, Integer userId) throws BaseException {
    QueryWrapper queryWrapper = new QueryWrapper();
    queryWrapper.eq("id", serverId);
    queryWrapper.eq("user_id", userId);
    Connection connection = connectionMapper.selectOne(queryWrapper);
    if (connection == null) {
      throw new BaseException(ErrorCode.CHECK_FAIL, ErrorCode.CHECK_FAIL_MSG);
    }
  }

  @Override
  public void update(Connection connection) throws BaseException {
    String alias = connection.getAlias();
    Integer userId = connection.getUserId();
    QueryWrapper queryWrapper = new QueryWrapper();
    queryWrapper.eq("user_id", userId);
    queryWrapper.eq("alias", alias);
    Connection existConnection = connectionMapper.selectOne(queryWrapper);
    // Alias are unique
    if (existConnection != null && !connection.getId().equals(existConnection.getId())) {
      throw new BaseException(ErrorCode.ALIAS_REPEAT, ErrorCode.ALIAS_REPEAT_MSG);
    }
    int flag = connectionMapper.updateById(connection);
    if (flag <= 0) {
      throw new BaseException(ErrorCode.INSERT_CONN_FAIL, ErrorCode.INSERT_CONN_FAIL_MSG);
    }
  }
}
