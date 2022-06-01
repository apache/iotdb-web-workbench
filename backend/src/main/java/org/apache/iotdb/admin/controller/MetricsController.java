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
import org.apache.iotdb.admin.common.utils.AuthenticationUtils;
import org.apache.iotdb.admin.model.entity.Connection;
import org.apache.iotdb.admin.model.vo.*;
import org.apache.iotdb.admin.service.ConnectionService;
import org.apache.iotdb.admin.service.IotDBService;
import org.apache.iotdb.admin.service.MetricsService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;

@RestController
@Api(value = "metrics related")
public class MetricsController {

  @Autowired private ConnectionService connectionService;
  @Autowired private IotDBService iotDBService;
  @Autowired private MetricsService metricsService;

  private static final Logger logger = LoggerFactory.getLogger(MetricsController.class);

  @GetMapping("/servers/metrics/connection")
  @ApiOperation("[metrics]Get All Connection")
  public BaseVO<List<MetricsConnectionVO>> getConnectionList(HttpServletRequest request)
      throws BaseException {
    Integer userId = AuthenticationUtils.getUserId(request);
    AuthenticationUtils.userAuthentication(userId, request);
    List<ConnVO> allConnections = connectionService.getAllConnections(userId);
    ArrayList<MetricsConnectionVO> metricsConnectionVOS = new ArrayList<>();
    for (ConnVO connVO : allConnections) {
      MetricsConnectionVO temp = new MetricsConnectionVO();
      temp.setId(connVO.getId());
      temp.setName(connVO.getAlias());
      metricsConnectionVOS.add(temp);
    }
    return BaseVO.success("Get Successfully", metricsConnectionVOS);
  }

  @GetMapping("/servers/{serverId}/metrics/datacount")
  @ApiOperation("[metrics]Get Datacount")
  public BaseVO<MetricsDataCountVO> getMetricsConnectionDataCount(
      @PathVariable("serverId") Integer serverId, HttpServletRequest request) throws BaseException {
    check(request, serverId);
    MetricsDataCountVO metricsDataCountVO = metricsService.getMetricsDataCount(serverId);
    return BaseVO.success("Get IoTDB data statistics successfully", metricsDataCountVO);
  }

  @GetMapping("/servers/{serverId}/metrics/QueryClassification")
  @ApiOperation("[metrics]Get all query classifications")
  public BaseVO<MetircsQueryClassificationVO> getAllQueryClassification(
      @PathVariable("serverId") Integer serverId, HttpServletRequest request) throws BaseException {
    check(request, serverId);
    MetircsQueryClassificationVO metircsQueryClassificationVO =
        metricsService.getMetircsQueryClassification(serverId);
    return BaseVO.success("Get IoTDB data statistics successfully", metircsQueryClassificationVO);
  }

  @GetMapping("/servers/{serverId}/metrics/{queryClassificationId}/selectcount")
  @ApiOperation("[Metrics]Get detail information of query sql")
  public BaseVO<QueryInfoVO> getQueryInfo(
      @PathVariable("serverId") Integer serverId,
      @PathVariable("queryClassificationId") Integer queryClassificationId,
      @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize,
      @RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer pageNum,
      @RequestParam(value = "filterString", required = false) String filterString,
      @RequestParam(value = "startTime", required = false, defaultValue = "-1") String startTimeStr,
      @RequestParam(value = "endTime", required = false, defaultValue = "-1") String endTimeStr,
      @RequestParam(value = "executionResult", required = false) Integer executionResult,
      HttpServletRequest request)
      throws BaseException {
    check(request, serverId);
    QueryInfoVO queryInfoVO =
        metricsService.getQueryInfo(
            serverId,
            queryClassificationId,
            pageSize,
            pageNum,
            filterString,
            startTimeStr,
            endTimeStr,
            executionResult);
    return BaseVO.success("Get IoTDB query statement data statistics successfully", queryInfoVO);
  }

  @GetMapping("/servers/{serverId}/metrics/diagram")
  @ApiOperation("Get metrics data for diagram")
  public BaseVO<MetricsDataForDiagramVO> getMetricsDataForDiagram(
      @PathVariable("serverId") Integer serverId,
      @RequestParam Integer metricId,
      HttpServletRequest request)
      throws BaseException {
    check(request, serverId);
    Connection connection = connectionService.getById(serverId);
    MetricsDataForDiagramVO metricsDataForDiagramVO =
        iotDBService.getMetricDataByMetricId(connection, metricId);
    metricsDataForDiagramVO.setServerId(serverId);
    return BaseVO.success("Get metrics data for diagram successfully", metricsDataForDiagramVO);
  }

  @GetMapping("/servers/{serverId}/metrics/list/{metricsType}")
  @ApiOperation("Get metrics data for list")
  public BaseVO<MetricsDataForListVO> getMetricsDataForList(
      @PathVariable("serverId") Integer serverId,
      @PathVariable("metricsType") Integer metricsType,
      HttpServletRequest request)
      throws BaseException {
    check(request, serverId);
    MetricsDataForListVO metricsDataForListVO =
        metricsService.getMetricsDataForList(serverId, metricsType);
    return BaseVO.success("Get metrics data for list successfully", metricsDataForListVO);
  }

  @GetMapping("/servers/{serverId}/metrics/list/query/{mode}")
  @ApiOperation("Get query metrics data for list")
  public BaseVO<QueryDataForListVO> getQueryMetricsDataForList(
      @PathVariable("serverId") Integer serverId, @PathVariable("mode") Integer mode)
      throws BaseException {
    QueryDataForListVO queryDataForListVO = new QueryDataForListVO();
    queryDataForListVO.setMode(mode);
    queryDataForListVO.setServerId(serverId);

    if (mode == 1) {
      List<QueryMetricsVO> queryMetricsVOs = iotDBService.getTopQueryMetricsData();
      queryDataForListVO.setQueryMetricsVOs(queryMetricsVOs);

    } else if (mode == 0) {
      List<QueryMetricsVO> queryMetricsVOs = iotDBService.getSlowQueryMetricsData();
      queryDataForListVO.setQueryMetricsVOs(queryMetricsVOs);
    }
    return BaseVO.success("Get query metrics data for list successfully", queryDataForListVO);
  }

  private void check(HttpServletRequest request, Integer serverId) throws BaseException {
    Integer userId = AuthenticationUtils.getUserId(request);
    connectionService.check(serverId, userId);
  }
}
