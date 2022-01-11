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
import org.apache.iotdb.admin.model.dto.SearchDTO;
import org.apache.iotdb.admin.model.entity.Connection;
import org.apache.iotdb.admin.model.entity.Query;
import org.apache.iotdb.admin.model.vo.BaseVO;
import org.apache.iotdb.admin.model.vo.QueryVO;
import org.apache.iotdb.admin.model.vo.SqlResultVO;
import org.apache.iotdb.admin.service.ConnectionService;
import org.apache.iotdb.admin.service.IotDBService;
import org.apache.iotdb.admin.service.QueryService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

import java.util.List;

@RestController
@Api(value = "Query related")
@RequestMapping("/servers/{serverId}")
public class QueryController {

  @Autowired private ConnectionService connectionService;

  @Autowired private IotDBService iotDBService;

  @Autowired private QueryService queryService;

  @PostMapping("/querySql")
  @ApiOperation("Execute the query script")
  public BaseVO<List<SqlResultVO>> query(
      @PathVariable("serverId") Integer serverId,
      @RequestBody SearchDTO searchDTO,
      HttpServletRequest request)
      throws BaseException {
    List<String> sqls = searchDTO.getSqls();
    if (sqls == null || sqls.size() == 0) {
      throw new BaseException(ErrorCode.NO_SQL, ErrorCode.NO_SQL_MSG);
    }
    check(request, serverId);
    Connection connection = connectionService.getById(serverId);
    Long timestamp = searchDTO.getTimestamp();
    List<SqlResultVO> sqlResultVOList = iotDBService.queryAll(connection, sqls, timestamp);
    return BaseVO.success("Execute the query script successfully", sqlResultVOList);
  }

  @PostMapping("/query")
  @ApiOperation("Save or update the query script")
  public BaseVO saveQuery(
      @PathVariable("serverId") Integer serverId,
      @RequestBody Query query,
      HttpServletRequest request)
      throws BaseException {
    String sqls = query.getSqls();
    if (sqls == null || "".equals(sqls)) {
      throw new BaseException(ErrorCode.NO_SQL, ErrorCode.NO_SQL_MSG);
    }
    check(request, serverId);
    if (query.getId() != null) {
      queryService.update(serverId, query);
      return BaseVO.success("Update successful", query.getId());
    }
    Integer id = queryService.save(serverId, query);
    return BaseVO.success("Save successful", id);
  }

  @GetMapping("/query")
  @ApiOperation("Get query scripts")
  public BaseVO<List<QueryVO>> getQueries(
      @PathVariable("serverId") Integer serverId, HttpServletRequest request) throws BaseException {
    check(request, serverId);
    List<QueryVO> queryVOList = queryService.getQueryList(serverId);
    return BaseVO.success("Get successful", queryVOList);
  }

  @DeleteMapping("/query/{queryId}")
  @ApiOperation("Delete the query script")
  public BaseVO deleteQuery(
      @PathVariable("serverId") Integer serverId,
      @PathVariable("queryId") Integer queryId,
      HttpServletRequest request)
      throws BaseException {
    check(request, serverId);
    queryService.deleteQuery(queryId);
    return BaseVO.success("Delete successfully", null);
  }

  @GetMapping("/query/{queryId}")
  @ApiOperation("Get the specified query script")
  public BaseVO<Query> getQuery(
      @PathVariable("serverId") Integer serverId,
      @PathVariable("queryId") Integer queryId,
      HttpServletRequest request)
      throws BaseException {
    check(request, serverId);
    Query query = queryService.getQuery(queryId);
    return BaseVO.success("Get successfully", query);
  }

  @GetMapping("/stop")
  @ApiOperation("Stop the query")
  public BaseVO query(
      @PathVariable("serverId") Integer serverId,
      @RequestParam("timestamp") Long timestamp,
      HttpServletRequest request)
      throws BaseException {
    check(request, serverId);
    iotDBService.stopQuery(serverId, timestamp);
    return BaseVO.success("Stop the query successful", null);
  }

  public void check(HttpServletRequest request, Integer serverId) throws BaseException {
    Integer userId = AuthenticationUtils.getUserId(request);
    connectionService.check(serverId, userId);
  }
}
