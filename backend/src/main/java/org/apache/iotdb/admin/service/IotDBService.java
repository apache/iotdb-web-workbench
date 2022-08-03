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

package org.apache.iotdb.admin.service;

import org.apache.iotdb.admin.common.exception.BaseException;
import org.apache.iotdb.admin.model.dto.*;
import org.apache.iotdb.admin.model.entity.Connection;
import org.apache.iotdb.admin.model.vo.*;

import java.util.List;
import java.util.Set;

public interface IotDBService {
  DataCountVO getDataCount(Connection connection) throws BaseException;

  DataModelVO getDataModel(Connection connection, String path) throws BaseException;

  List<String> getAllStorageGroups(Connection connection) throws BaseException;

  List<NodeTreeVO> getGroupsNodeTree(Connection connection) throws BaseException;

  void saveStorageGroup(Connection connection, String groupName) throws BaseException;

  void deleteStorageGroup(Connection connection, String groupName) throws BaseException;

  CountDTO getDevicesByGroup(
      Connection connection, String groupName, Integer pageSize, Integer pageNum, String keyword)
      throws BaseException;

  CountDTO getMeasurementsByDevice(
      Connection connection, String deviceName, Integer pageSize, Integer pageNum, String keyword)
      throws BaseException;

  List<String> getIotDBUserList(Connection connection) throws BaseException;

  List<String> getIotDBRoleList(Connection connection) throws BaseException;

  RoleVO getIotDBRoleInfo(Connection connection, String roleName) throws BaseException;

  void deleteIotDBUser(Connection connection, String userName) throws BaseException;

  void deleteIotDBRole(Connection connection, String roleName) throws BaseException;

  void setIotDBUser(Connection connection, IotDBUser iotDBUserVO) throws BaseException;

  void setIotDBRole(Connection connection, IotDBRole iotDBRole) throws BaseException;

  UserRolesVO getRolesOfUser(Connection connection, String userName) throws BaseException;

  void userGrant(Connection connection, String userName, UserGrantDTO userGrantDTO)
      throws BaseException;

  void roleGrant(Connection connection, String roleName, RoleGrantDTO roleGrantDTO)
      throws BaseException;

  Set<String> getUserAuthorityPrivilege(Connection connection, String userName)
      throws BaseException;

  Set<String> getAllAuthorityPrivilege(Connection connection, String userName) throws BaseException;

  Set<String> getRoleAuthorityPrivilege(Connection connection, String roleName)
      throws BaseException;

  void upsertAuthorityPrivilege(
      Connection connection,
      String userName,
      AuthorityPrivilegeDTO authorityPrivilegeDTO,
      String userOrRole)
      throws BaseException;

  List<DataPrivilegeVO> getUserDataPrivilege(Connection connection, String userName)
      throws BaseException;

  List<DataPrivilegeVO> getRoleDataPrivilege(Connection connection, String roleName)
      throws BaseException;

  void deleteTimeseries(Connection connection, String timeseriesName) throws BaseException;

  List<Integer> getDevicesCount(Connection connection, List<String> groupNames)
      throws BaseException;

  void saveGroupTtl(Connection connection, String groupName, long l) throws BaseException;

  void cancelGroupTtl(Connection connection, String groupName) throws BaseException;

  List<Integer> getTimeseriesCount(Connection connection, List<String> deviceNames)
      throws BaseException;

  List<String> deleteTimeseriesByDevice(Connection connection, String deviceName)
      throws BaseException;

  void upsertMeasurements(Connection connection, DeviceInfoDTO deviceInfoDTO) throws BaseException;

  Integer getOneDataCount(Connection connection, String deviceName, String measurementName)
      throws BaseException;

  String getLastMeasurementValue(Connection connection, String timeseries) throws BaseException;

  String getGroupTTL(Connection connection, String groupName) throws BaseException;

  List<String> getDevices(Connection connection, String groupName) throws BaseException;

  List<NodeTreeVO> getDeviceNodeTree(Connection connection, String groupName) throws BaseException;

  NodeTreeVO getDeviceList(
      Connection connection, String groupName, Integer pageSize, Integer pageNum)
      throws BaseException;

  List<String> getDeviceParents(Connection connection, String groupName, String deviceName)
      throws BaseException;

  Boolean deviceExist(Connection connection, String groupName, String deviceName)
      throws BaseException;

  List<String> getTimeseries(Connection connection, String deviceName) throws BaseException;

  DataVO getDataByDevice(
      Connection connection,
      String deviceName,
      Integer pageSize,
      Integer pageNum,
      DataQueryDTO dataQueryDTO)
      throws BaseException;

  void updateDataByDevice(Connection connection, String deviceName, DataUpdateDTO dataUpdateDTO)
      throws BaseException;

  void deleteDataByDevice(Connection connection, String deviceName, DataDeleteDTO dataDeleteDTO)
      throws BaseException;

  void randomImport(Connection connection, String deviceName, RandomImportDTO randomImportDTO)
      throws BaseException;

  String getSqlForExport(String deviceName, DataQueryDTO dataQueryDTO) throws BaseException;

  void upsertDataPrivileges(
      Connection connection, String userOrRole, String name, PrivilegeInfoDTO privilegeInfoDTO)
      throws BaseException;

  public List<QueryMetricsVO> getSlowQueryMetricsData();

  RecordVO getRecords(
      Connection connection, String deviceName, String timeseriesName, String dataType)
      throws BaseException;

  List<SqlResultVO> queryAll(Connection connection, List<String> sqls, Long timestamp)
      throws BaseException;

  void updatePwd(Connection connection, IotDBUser iotDBUser) throws BaseException;

  public QueryInfoDTO getQueryInfoListByQueryClassificationId(
      Connection connection,
      Integer queryClassificationId,
      Integer pageSize,
      Integer pageNum,
      String filterString,
      Long startTime,
      Long endTime,
      Integer executionResult)
      throws BaseException;

  public List<QueryMetricsVO> getTopQueryMetricsData();

  public MetricsDataForDiagramVO getMetricDataByMetricId(Connection connection, Integer metricId)
      throws BaseException;

  void stopQuery(Integer serverId, Long timestamp) throws BaseException;

  DataModelVO getDataModelDetail(
      Connection connection, String path, Integer pageSize, Integer pageNum) throws BaseException;

  List<String> getBatchLastMeasurementValue(Connection connection, List<String> timeseriesList)
      throws BaseException;

  List<String> getBatchDataCount(
      Connection connection, String deviceName, List<String> timeseriesList) throws BaseException;
}
