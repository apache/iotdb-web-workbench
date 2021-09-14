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
import org.apache.iotdb.admin.model.vo.IotDBUserVO;
import org.apache.iotdb.admin.model.vo.RecordVO;
import org.apache.iotdb.admin.model.vo.SqlResultVO;

import java.util.List;

public interface IotDBService {

  List<String> getAllStorageGroups(Connection connection) throws BaseException;

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

  IotDBUserVO getIotDBUser(Connection connection, String userName) throws BaseException;

  void deleteIotDBUser(Connection connection, String userName) throws BaseException;

  void deleteIotDBRole(Connection connection, String roleName) throws BaseException;

  void setIotDBUser(Connection connection, IotDBUser iotDBUserVO) throws BaseException;

  void setIotDBRole(Connection connection, IotDBRole iotDBRole) throws BaseException;

  void insertTimeseries(Connection connection, String deviceName, Timeseries timeseries)
      throws BaseException;

  void deleteTimeseries(Connection connection, String timeseriesName) throws BaseException;

  SqlResultVO showTimeseries(Connection connection, String deviceName) throws BaseException;

  List<Integer> getDevicesCount(Connection connection, List<String> groupNames)
      throws BaseException;

  void saveGroupTtl(Connection connection, String groupName, long l) throws BaseException;

  void cancelGroupTtl(Connection connection, String groupName) throws BaseException;

  Integer getDeviceCount(Connection connection, String groupName) throws BaseException;

  List<Integer> getTimeseriesCount(Connection connection, List<String> deviceNames)
      throws BaseException;

  void deleteTimeseriesByDevice(Connection connection, String deviceName) throws BaseException;

  void createDeviceWithMeasurements(Connection connection, DeviceInfoDTO deviceInfoDTO)
      throws BaseException;

  Integer getMeasurementsCount(Connection connection, String deviceName) throws BaseException;

  String getLastMeasurementValue(Connection connection, String timeseries) throws BaseException;

  String getGroupTTL(Connection connection, String groupName) throws BaseException;

  List<String> getDevices(Connection connection, String groupName) throws BaseException;

  List<String> getTimeseries(Connection connection, String deviceName) throws BaseException;

  void setUserPrivileges(Connection connection, String userName, PrivilegeInfoDTO privilegeInfoDTO)
      throws BaseException;

  RecordVO getRecords(Connection connection, String deviceName, String timeseriesName)
      throws BaseException;

  List<SqlResultVO> queryAll(Connection connection, List<String> sqls, Long timestamp)
      throws BaseException;

  void updatePwd(Connection connection, IotDBUser iotDBUser) throws BaseException;

  void stopQuery(Integer serverId, Long timestamp) throws BaseException;
}
