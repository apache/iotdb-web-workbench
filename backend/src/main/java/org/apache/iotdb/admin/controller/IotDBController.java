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
import org.apache.iotdb.admin.model.dto.*;
import org.apache.iotdb.admin.model.entity.Connection;
import org.apache.iotdb.admin.model.entity.Device;
import org.apache.iotdb.admin.model.entity.Role;
import org.apache.iotdb.admin.model.entity.StorageGroup;
import org.apache.iotdb.admin.model.vo.*;
import org.apache.iotdb.admin.service.*;
import org.apache.iotdb.admin.tool.ExportCsv;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@RestController
@Api(value = "IoTDB related")
@RequestMapping("/servers/{serverId}")
public class IotDBController {

  @Autowired private ConnectionService connectionService;

  @Autowired private IotDBService iotDBService;

  @Autowired private GroupService groupService;

  @Autowired private DeviceService deviceService;

  @Autowired private MeasurementService measurementService;

  @Autowired private RoleService roleService;

  @Autowired private FileService fileService;

  @Autowired private ExportCsv exportCsv;

  @GetMapping("/dataCount")
  @ApiOperation("Get IoTDB data statistics")
  public BaseVO<DataCountVO> getDataCount(
      @PathVariable("serverId") Integer serverId, HttpServletRequest request) throws BaseException {
    check(request, serverId);
    Connection connection = connectionService.getById(serverId);
    DataCountVO dataCountVO = iotDBService.getDataCount(connection);
    return BaseVO.success("Get IoTDB data statistics successfully", dataCountVO);
  }

  @GetMapping("/dataModel")
  @ApiOperation("Get IoTDB data model")
  public BaseVO<DataModelVO> getDataModel(
      @PathVariable("serverId") Integer serverId,
      @RequestParam(value = "path", required = false, defaultValue = "root") String path,
      HttpServletRequest request)
      throws BaseException {
    check(request, serverId);
    Connection connection = connectionService.getById(serverId);
    DataModelVO dataModelVO = iotDBService.getDataModel(connection, path);
    return BaseVO.success("Get IoTDB data model successfully", dataModelVO);
  }

  @GetMapping("/dataModel/detail")
  @ApiOperation("Get IoTDB data model in detail")
  public BaseVO<DataModelVO> getDataModelDetail(
      @PathVariable("serverId") Integer serverId,
      @RequestParam(value = "path", required = false, defaultValue = "root") String path,
      @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize,
      @RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer pageNum,
      HttpServletRequest request)
      throws BaseException {
    check(request, serverId);
    Connection connection = connectionService.getById(serverId);
    DataModelVO dataModelVO = iotDBService.getDataModelDetail(connection, path, pageSize, pageNum);
    return BaseVO.success("Get IoTDB data model successfully", dataModelVO);
  }

  @GetMapping("/storageGroups/info")
  @ApiOperation("Get information of the storage group list")
  public BaseVO<GroupInfoVO> getAllStorageGroupsInfo(
      @PathVariable("serverId") Integer serverId,
      @RequestParam(value = "pageSize", required = false, defaultValue = "15") Integer pageSize,
      @RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer pageNum,
      HttpServletRequest request)
      throws BaseException {
    check(request, serverId);
    Connection connection = connectionService.getById(serverId);
    List<String> groupNames = iotDBService.getAllStorageGroups(connection);
    List<String> subGroupNames = new ArrayList<>();
    int size = groupNames.size();
    int pageStart = pageNum == 1 ? 0 : (pageNum - 1) * pageSize;
    int pageEnd = size < pageNum * pageSize ? size : pageNum * pageSize;
    if (size > pageStart) {
      subGroupNames = groupNames.subList(pageStart, pageEnd);
    }
    List<GroupInfo> groupInfoList = new ArrayList<>();
    GroupInfoVO groupInfoVO = new GroupInfoVO();
    if (subGroupNames == null || subGroupNames.size() == 0) {
      return BaseVO.success("Get successfully", groupInfoVO);
    }
    String host = connection.getHost();
    List<Integer> deviceCounts = iotDBService.getDevicesCount(connection, subGroupNames);
    List<String> descriptions = groupService.getGroupDescription(host, subGroupNames);
    for (int i = 0; i < subGroupNames.size(); i++) {
      GroupInfo groupInfo = new GroupInfo();
      groupInfo.setGroupName(subGroupNames.get(i));
      groupInfo.setDeviceCount(deviceCounts.get(i));
      groupInfo.setDescription(descriptions.get(i));
      groupInfoList.add(groupInfo);
    }
    groupInfoVO.setGroupInfoList(groupInfoList);
    groupInfoVO.setGroupCount(size);
    return BaseVO.success("Get successfully", groupInfoVO);
  }

  @GetMapping("/storageGroups")
  @ApiOperation("Get storage group list")
  public BaseVO<List<StorageGroupVO>> getAllStorageGroups(
      @PathVariable("serverId") Integer serverId, HttpServletRequest request) throws BaseException {
    check(request, serverId);
    Connection connection = connectionService.getById(serverId);
    List<StorageGroupVO> storageGroupVOList = new ArrayList<>();
    List<String> groupNames = iotDBService.getAllStorageGroups(connection);
    if (groupNames == null || groupNames.size() == 0) {
      return BaseVO.success("Get successfully", storageGroupVOList);
    }
    String host = connection.getHost();
    for (String groupName : groupNames) {
      StorageGroupVO storageGroupVO = new StorageGroupVO();
      storageGroupVO.setGroupName(groupName);
      storageGroupVOList.add(storageGroupVO);
    }
    return BaseVO.success("Get successfully", storageGroupVOList);
  }

  @GetMapping("/storageGroups/nodeTree")
  @ApiOperation("Get storage group list(Node tree structure)")
  public BaseVO<List<NodeTreeVO>> getGroupsNodeTree(
      @PathVariable("serverId") Integer serverId, HttpServletRequest request) throws BaseException {
    check(request, serverId);
    Connection connection = connectionService.getById(serverId);
    List<NodeTreeVO> groupsNodeTree = iotDBService.getGroupsNodeTree(connection);
    return BaseVO.success("Get successfully", groupsNodeTree);
  }

  @PostMapping("/storageGroups")
  @ApiOperation("Upsert storage group")
  public BaseVO saveStorageGroup(
      @PathVariable("serverId") Integer serverId,
      @RequestBody GroupDTO groupDTO,
      HttpServletRequest request)
      throws BaseException {
    String groupName = groupDTO.getGroupName();
    checkParameter(groupName);
    check(request, serverId);
    Connection connection = connectionService.getById(serverId);
    Long ttl = groupDTO.getTtl();
    String ttlUnit = groupDTO.getTtlUnit();
    Integer groupId = groupDTO.getGroupId();
    groupDTO.setGroupName(groupName);

    List<String> groupNames = iotDBService.getAllStorageGroups(connection);
    if (groupId == null) {
      if (!groupNames.contains(groupDTO.getGroupName())) {
        iotDBService.saveStorageGroup(connection, groupName);
      }
      groupService.setStorageGroupInfo(connection, groupDTO);
    } else {
      groupService.updateStorageGroupInfo(connection, groupDTO);
    }
    if (ttl != null && ttlUnit != null) {
      checkTtl(ttl, ttlUnit);
      if (ttl >= 0) {
        Long times = switchTime(ttlUnit);
        iotDBService.saveGroupTtl(connection, groupName, ttl * times);
      } else {
        throw new BaseException(ErrorCode.TTL_WRONG, ErrorCode.TTL_WRONG_MSG);
      }
    } else {
      if (ttl == null && ttlUnit == null) {
        iotDBService.cancelGroupTtl(connection, groupName);
      } else {
        throw new BaseException(ErrorCode.WRONG_DB_PARAM, ErrorCode.WRONG_DB_PARAM_MSG);
      }
    }
    return BaseVO.success("Upsert successfully", null);
  }

  @DeleteMapping("/storageGroups/{groupName}")
  @ApiOperation("Delete storage group")
  public BaseVO deleteStorageGroup(
      @PathVariable("serverId") Integer serverId,
      @PathVariable("groupName") String groupName,
      HttpServletRequest request)
      throws BaseException {
    checkParameter(groupName);
    check(request, serverId);
    Connection connection = connectionService.getById(serverId);
    String host = connection.getHost();
    iotDBService.deleteStorageGroup(connection, groupName);
    groupService.deleteGroupInfo(host, groupName);
    deviceService.deleteDeviceInfo(host, groupName);
    measurementService.deleteMeasurementInfo(host, groupName);
    return BaseVO.success("Delete successfully", null);
  }

  @GetMapping("/storageGroups/{groupName}")
  @ApiOperation("Get detailed information of the specified storage group")
  public BaseVO<GroupVO> getStorageGroup(
      @PathVariable("serverId") Integer serverId,
      @PathVariable("groupName") String groupName,
      HttpServletRequest request)
      throws BaseException {
    checkParameter(groupName);
    check(request, serverId);
    Connection connection = connectionService.getById(serverId);
    String host = connection.getHost();
    StorageGroup group = groupService.getGroupInfo(host, groupName);
    String ttl = iotDBService.getGroupTTL(connection, groupName);
    String ttlUnit;
    GroupVO groupVO = new GroupVO();
    if (ttl != null && !"null".equalsIgnoreCase(ttl)) {
      Long totalTime = Long.valueOf(ttl);
      ttlUnit = getTTL(totalTime);
      Long times = switchTime(ttlUnit);
      ttl = String.valueOf(totalTime / times);
      groupVO.setTtl(ttl);
      groupVO.setTtiUnit(ttlUnit);
    } else {
      groupVO.setTtl(null);
    }
    if (group != null) {
      groupVO.setCreator(group.getCreator());
      groupVO.setDescription(group.getDescription());
      Date date = new Date(group.getCreateTime());
      SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
      String createTime = simpleDateFormat.format(date);
      groupVO.setCreateTime(createTime);
    }
    groupVO.setGroupName(groupName);
    groupVO.setAlias(connection.getAlias());
    return BaseVO.success("Get successfully", groupVO);
  }

  @GetMapping("/storageGroups/{groupName}/devices/info")
  @ApiOperation("Gets information of entities under the specified storage group")
  public BaseVO<DeviceInfoVO> getDevicesInfoByGroupName(
      @PathVariable("serverId") Integer serverId,
      @PathVariable("groupName") String groupName,
      @RequestParam("pageSize") Integer pageSize,
      @RequestParam("pageNum") Integer pageNum,
      @RequestParam(value = "keyword", required = false) String keyword,
      HttpServletRequest request)
      throws BaseException {
    checkParameter(groupName);
    check(request, serverId);
    Connection connection = connectionService.getById(serverId);
    CountDTO countDTO =
        iotDBService.getDevicesByGroup(connection, groupName, pageSize, pageNum, keyword);
    List<String> deviceNames = countDTO.getObjects();
    DeviceInfoVO deviceInfoVO = new DeviceInfoVO();
    Integer totalPage = countDTO.getTotalPage();
    Integer totalCount = countDTO.getTotalCount();
    deviceInfoVO.setTotalCount(totalCount);
    deviceInfoVO.setTotalPage(totalPage);
    List<Integer> lines = iotDBService.getTimeseriesCount(connection, deviceNames);
    List<Device> devices = deviceService.getDevices(connection.getHost(), deviceNames);
    List<DeviceInfo> deviceInfos = new ArrayList<>();
    if (deviceNames != null) {
      for (int i = 0; i < deviceNames.size(); i++) {
        String deviceName = deviceNames.get(i);
        DeviceInfo deviceInfo = new DeviceInfo();
        deviceInfo.setDeviceName(deviceName);
        deviceInfo.setLine(lines.get(i));
        deviceInfo.setParents(iotDBService.getDeviceParents(connection, groupName, deviceName));
        if (devices.get(i) != null) {
          deviceInfo.setDeviceId(devices.get(i).getId());
          deviceInfo.setCreator(devices.get(i).getCreator());
          deviceInfo.setDescription(devices.get(i).getDescription());
        }
        deviceInfos.add(deviceInfo);
      }
    }
    deviceInfoVO.setDeviceInfos(deviceInfos);
    return BaseVO.success("Get successfully", deviceInfoVO);
  }

  @GetMapping("/storageGroups/{groupName}/devices")
  @ApiOperation("Gets a list of entities under the specified storage group")
  public BaseVO<List<String>> getDevicesByGroup(
      @PathVariable("serverId") Integer serverId,
      @PathVariable("groupName") String groupName,
      HttpServletRequest request)
      throws BaseException {
    checkParameter(groupName);
    check(request, serverId);
    Connection connection = connectionService.getById(serverId);
    final List<String> devices = iotDBService.getDevices(connection, groupName);
    return BaseVO.success("Get successfully", devices);
  }

  @GetMapping("/storageGroups/{groupName}/devices/nodeTree")
  @ApiOperation("Get entity list(Node tree structure) of the specified storage group")
  public BaseVO<List<NodeTreeVO>> getDevicesNodeTreeByGroup(
      @PathVariable("serverId") Integer serverId,
      @PathVariable("groupName") String groupName,
      HttpServletRequest request)
      throws BaseException {
    checkParameter(groupName);
    check(request, serverId);
    Connection connection = connectionService.getById(serverId);
    List<NodeTreeVO> deviceList = iotDBService.getDeviceNodeTree(connection, groupName);
    return BaseVO.success("Get successfully", deviceList);
  }

  @GetMapping("/storageGroups/{groupName}/devices/tree")
  @ApiOperation("Get entity list(Tree structure) of the specified storage group")
  public BaseVO<NodeTreeVO> getDevicesTreeByGroup(
      @PathVariable("serverId") Integer serverId,
      @PathVariable("groupName") String groupName,
      @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize,
      @RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer pageNum,
      HttpServletRequest request)
      throws BaseException {
    checkParameter(groupName);
    check(request, serverId);
    Connection connection = connectionService.getById(serverId);
    NodeTreeVO deviceList = iotDBService.getDeviceList(connection, groupName, pageSize, pageNum);
    if (deviceList == null) {
      deviceList = new NodeTreeVO(groupName);
    }
    deviceList.setPageNum(pageNum);
    deviceList.setPageSize(pageSize);
    return BaseVO.success("Get successfully", deviceList);
  }

  @GetMapping("/storageGroups/{groupName}/devices/{deviceName}/parents")
  @ApiOperation("Get the list of the parent entities of the specified entity")
  public BaseVO<List<String>> getDeviceParents(
      @PathVariable("serverId") Integer serverId,
      @PathVariable("groupName") String groupName,
      @PathVariable("deviceName") String deviceName,
      HttpServletRequest request)
      throws BaseException {
    checkParameter(groupName);
    check(request, serverId);
    Connection connection = connectionService.getById(serverId);
    List<String> deviceParents = iotDBService.getDeviceParents(connection, groupName, deviceName);
    return BaseVO.success("Get successfully", deviceParents);
  }

  @GetMapping("/storageGroups/{groupName}/devices/{deviceName}/exist")
  @ApiOperation("Check whether the device already exists")
  public BaseVO<Boolean> deviceExist(
      @PathVariable("serverId") Integer serverId,
      @PathVariable("groupName") String groupName,
      @PathVariable("deviceName") String deviceName,
      HttpServletRequest request)
      throws BaseException {
    checkParameter(groupName, deviceName);
    check(request, serverId);
    Connection connection = connectionService.getById(serverId);
    Boolean isExist = iotDBService.deviceExist(connection, groupName, deviceName);
    return BaseVO.success("Get successfully", isExist);
  }

  @PostMapping("/storageGroups/{groupName}/devices")
  @ApiOperation("Upsert entity")
  public BaseVO saveOrUpdateDevice(
      @PathVariable("serverId") Integer serverId,
      @PathVariable("groupName") String groupName,
      @RequestBody DeviceInfoDTO deviceInfoDTO,
      HttpServletRequest request)
      throws BaseException {
    for (DeviceDTO deviceDTO : deviceInfoDTO.getDeviceDTOList()) {
      checkParameter(groupName, deviceInfoDTO.getDeviceName(), deviceDTO.getTimeseries());
    }
    check(request, serverId);
    Connection connection = connectionService.getById(serverId);
    iotDBService.upsertMeasurements(connection, deviceInfoDTO);
    String host = connection.getHost();
    if (deviceInfoDTO.getDeviceId() == null) {
      deviceService.setDeviceInfo(connection, deviceInfoDTO);
      measurementService.updateMeasurementsInfo(host, deviceInfoDTO);
    } else {
      deviceService.updateDeviceInfo(deviceInfoDTO);
      measurementService.updateMeasurementsInfo(host, deviceInfoDTO);
    }
    return BaseVO.success("Upsert successfully", null);
  }

  @DeleteMapping("/storageGroups/{groupName}/devices/{deviceName}")
  @ApiOperation("Delete entity")
  public BaseVO deleteDevice(
      @PathVariable("serverId") Integer serverId,
      @PathVariable("groupName") String groupName,
      @PathVariable("deviceName") String deviceName,
      HttpServletRequest request)
      throws BaseException {
    checkParameter(groupName, deviceName);
    check(request, serverId);
    Connection connection = connectionService.getById(serverId);
    String host = connection.getHost();
    List<String> deletedTimeseriesList =
        iotDBService.deleteTimeseriesByDevice(connection, deviceName);
    deviceService.deleteDeviceInfoByDeviceName(host, deviceName);
    for (String timeseries : deletedTimeseriesList) {
      measurementService.deleteMeasurementInfo(host, timeseries);
    }
    return BaseVO.success("Delete successfully", null);
  }

  @GetMapping("/storageGroups/{groupName}/devices/{deviceName}")
  @ApiOperation("Get information of the specified entity")
  public BaseVO<DeviceVO> getDeviceInfo(
      @PathVariable("serverId") Integer serverId,
      @PathVariable("groupName") String groupName,
      @PathVariable("deviceName") String deviceName,
      HttpServletRequest request)
      throws BaseException {
    checkParameter(groupName, deviceName);
    check(request, serverId);
    Connection connection = connectionService.getById(serverId);
    String host = connection.getHost();
    DeviceVO deviceVO = deviceService.getDevice(host, deviceName);
    return BaseVO.success("Get successfully", deviceVO);
  }

  @GetMapping("/storageGroups/{groupName}/devices/{deviceName}/info")
  @ApiOperation("Gets detailed information of measurements under the specified entity")
  public BaseVO<MeasuremtnInfoVO> getMeasurementsByDeviceName(
      @PathVariable("serverId") Integer serverId,
      @PathVariable("groupName") String groupName,
      @PathVariable("deviceName") String deviceName,
      @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize,
      @RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer pageNum,
      @RequestParam(value = "keyword", required = false) String keyword,
      HttpServletRequest request)
      throws BaseException {
    checkParameter(groupName, deviceName);
    check(request, serverId);
    Connection connection = connectionService.getById(serverId);
    CountDTO countDTO =
        iotDBService.getMeasurementsByDevice(connection, deviceName, pageSize, pageNum, keyword);
    List<MeasurementDTO> measurementDTOList = countDTO.getObjects();
    List<MeasurementVO> measurementVOList = new ArrayList<>();
    String host = connection.getHost();
    if (measurementDTOList != null) {
      List<String> timeseriesList = new ArrayList<>();
      for (MeasurementDTO measurementDTO : measurementDTOList) {
        timeseriesList.add(measurementDTO.getTimeseries());
      }
      List<String> batchNewValue =
          iotDBService.getBatchLastMeasurementValue(connection, timeseriesList);
      List<String> batchDataCount =
          iotDBService.getBatchDataCount(connection, deviceName, timeseriesList);
      int index = 0;
      for (MeasurementDTO measurementDTO : measurementDTOList) {
        MeasurementVO measurementVO = new MeasurementVO();
        BeanUtils.copyProperties(measurementDTO, measurementVO);
        String description =
            measurementService.getDescription(host, measurementDTO.getTimeseries());
        if (batchNewValue.size() != 0) {
          measurementVO.setNewValue(batchNewValue.get(index));
        }
        if (batchDataCount.size() != 0) {
          measurementVO.setDataCount(Integer.parseInt(batchDataCount.get(index)));
        }
        measurementVO.setDescription(description);
        ObjectMapper mapper = new ObjectMapper();
        List<List<String>> tags = new ArrayList<>();
        List<List<String>> attributes = new ArrayList<>();
        try {
          if (!"null".equals(measurementDTO.getTags())) {

            Map<String, String> tagsMap = mapper.readValue(measurementDTO.getTags(), Map.class);
            for (String key : tagsMap.keySet()) {
              List<String> tag = new ArrayList<>();
              tag.add(key);
              tag.add(tagsMap.get(key));
              tags.add(tag);
            }
          }
          measurementVO.setTags(tags);
          if (!"null".equals(measurementDTO.getAttributes())) {

            Map<String, String> attributesMap =
                mapper.readValue(measurementDTO.getAttributes(), Map.class);
            for (String key : attributesMap.keySet()) {
              List<String> attribute = new ArrayList<>();
              attribute.add(key);
              attribute.add(attributesMap.get(key));
              attributes.add(attribute);
            }
          }
          measurementVO.setAttributes(attributes);
        } catch (JsonProcessingException e) {
          log.error(e.getMessage());
          throw new BaseException(ErrorCode.GET_MSM_FAIL, ErrorCode.GET_MSM_FAIL_MSG);
        }
        measurementVOList.add(measurementVO);
        index++;
      }
    }
    MeasuremtnInfoVO measuremtnInfoVO = new MeasuremtnInfoVO();
    measuremtnInfoVO.setTotalCount(countDTO.getTotalCount());
    measuremtnInfoVO.setTotalPage(countDTO.getTotalPage());
    measuremtnInfoVO.setMeasurementVOList(measurementVOList);
    return BaseVO.success("Get successfully", measuremtnInfoVO);
  }

  @GetMapping("/storageGroups/{groupName}/devices/{deviceName}/timeseries/{timeseriesName}")
  @ApiOperation("Get the latest 200 data records of the specified measurement")
  public BaseVO<RecordVO> getMeasurementInfo(
      @PathVariable("serverId") Integer serverId,
      @PathVariable("groupName") String groupName,
      @PathVariable("deviceName") String deviceName,
      @PathVariable("timeseriesName") String timeseriesName,
      @RequestParam("dataType") String dataType,
      HttpServletRequest request)
      throws BaseException {
    checkParameter(groupName, deviceName, timeseriesName);
    check(request, serverId);
    Connection connection = connectionService.getById(serverId);
    RecordVO recordVO = iotDBService.getRecords(connection, deviceName, timeseriesName, dataType);
    return BaseVO.success("Get successfully", recordVO);
  }

  @GetMapping("/storageGroups/{groupName}/devices/{deviceName}/timeseries")
  @ApiOperation("Get the list of measurement under the specified entity")
  public BaseVO<List<String>> getTimeseries(
      @PathVariable("serverId") Integer serverId,
      @PathVariable("groupName") String groupName,
      @PathVariable("deviceName") String deviceName,
      HttpServletRequest request)
      throws BaseException {
    checkParameter(groupName, deviceName);
    check(request, serverId);
    Connection connection = connectionService.getById(serverId);
    List<String> timeseries = iotDBService.getTimeseries(connection, deviceName);
    return BaseVO.success("Get successfully", timeseries);
  }

  @DeleteMapping("/storageGroups/{groupName}/devices/{deviceName}/timeseries/{timeseriesName}")
  @ApiOperation("Delete measurement")
  public BaseVO deleteTimeseries(
      @PathVariable("serverId") Integer serverId,
      @PathVariable("groupName") String groupName,
      @PathVariable("deviceName") String deviceName,
      @PathVariable("timeseriesName") String timeseriesName,
      HttpServletRequest request)
      throws BaseException {
    checkParameter(groupName, deviceName, timeseriesName);
    check(request, serverId);
    Connection connection = connectionService.getById(serverId);
    iotDBService.deleteTimeseries(connection, timeseriesName);
    String host = connection.getHost();
    measurementService.deleteMeasurementInfo(host, timeseriesName);
    return BaseVO.success("Delete successfully", null);
  }

  @PostMapping("/storageGroups/{groupName}/devices/{deviceName}/data")
  @ApiOperation("Get measurement data of the specified entity")
  public BaseVO<DataVO> getDataByDevice(
      @PathVariable("serverId") Integer serverId,
      @PathVariable("groupName") String groupName,
      @PathVariable("deviceName") String deviceName,
      @RequestParam("pageSize") Integer pageSize,
      @RequestParam("pageNum") Integer pageNum,
      @RequestBody DataQueryDTO dataQueryDTO,
      HttpServletRequest request)
      throws BaseException {
    checkParameter(groupName, deviceName);
    check(request, serverId);
    Connection connection = connectionService.getById(serverId);
    DataVO dataVO =
        iotDBService.getDataByDevice(connection, deviceName, pageSize, pageNum, dataQueryDTO);
    return BaseVO.success("Get successfully", dataVO);
  }

  @PutMapping("/storageGroups/{groupName}/devices/{deviceName}/data")
  @ApiOperation("Update measurement data")
  public BaseVO updateDataByDevice(
      @PathVariable("serverId") Integer serverId,
      @PathVariable("groupName") String groupName,
      @PathVariable("deviceName") String deviceName,
      @RequestBody DataUpdateDTO dataUpdateDTO,
      HttpServletRequest request)
      throws BaseException {
    if (dataUpdateDTO.getValueList().size() != dataUpdateDTO.getMeasurementList().size()) {
      throw new BaseException(ErrorCode.WRONG_DB_PARAM, ErrorCode.WRONG_DB_PARAM_MSG);
    }
    for (String measurement : dataUpdateDTO.getMeasurementList()) {
      checkParameter(groupName, deviceName, measurement);
    }
    check(request, serverId);
    Connection connection = connectionService.getById(serverId);
    iotDBService.updateDataByDevice(connection, deviceName, dataUpdateDTO);
    return BaseVO.success("Upsert successfully", null);
  }

  @DeleteMapping("/storageGroups/{groupName}/devices/{deviceName}/data")
  @ApiOperation("Delete measurement data")
  public BaseVO deleteDataByDevice(
      @PathVariable("serverId") Integer serverId,
      @PathVariable("groupName") String groupName,
      @PathVariable("deviceName") String deviceName,
      @RequestBody DataDeleteDTO dataDeleteDTO,
      HttpServletRequest request)
      throws BaseException {
    for (String measurement : dataDeleteDTO.getMeasurementList()) {
      checkParameter(groupName, deviceName, measurement);
    }
    check(request, serverId);
    Connection connection = connectionService.getById(serverId);
    iotDBService.deleteDataByDevice(connection, deviceName, dataDeleteDTO);
    return BaseVO.success("Delete successfully", null);
  }

  @PostMapping("/storageGroups/{groupName}/devices/{deviceName}/randomImport")
  @ApiOperation("Import measurement data of specified devices in batches")
  public BaseVO randomImport(
      @PathVariable("serverId") Integer serverId,
      @PathVariable("groupName") String groupName,
      @PathVariable("deviceName") String deviceName,
      @RequestBody RandomImportDTO randomImportDTO,
      HttpServletRequest request)
      throws BaseException {
    checkParameter(groupName, deviceName);
    check(request, serverId);
    Connection connection = connectionService.getById(serverId);
    iotDBService.randomImport(connection, deviceName, randomImportDTO);
    return BaseVO.success("Randomly import successfully", null);
  }

  @ApiOperation("Export measurement data of the specified entity")
  @PostMapping("/storageGroups/{groupName}/devices/{deviceName}/exportData")
  public ResponseEntity<Resource> exportData(
      @PathVariable("serverId") Integer serverId,
      @PathVariable("groupName") String groupName,
      @PathVariable("deviceName") String deviceName,
      @RequestBody DataQueryDTO dataQueryDTO,
      HttpServletRequest request)
      throws BaseException {
    List<String> measurementList = dataQueryDTO.getMeasurementList();
    if (measurementList == null || measurementList.size() == 0) {
      throw new BaseException(ErrorCode.WRONG_DB_PARAM, ErrorCode.WRONG_DB_PARAM_MSG);
    }
    for (String measurement : measurementList) {
      checkParameter(groupName, deviceName, measurement);
    }
    check(request, serverId);
    Connection connection = connectionService.getById(serverId);
    String host = connection.getHost();
    Integer port = connection.getPort();
    String username = connection.getUsername();
    String password = connection.getPassword();

    String sql = iotDBService.getSqlForExport(deviceName, dataQueryDTO);
    String fileName = exportCsv.exportCsv(host, port, username, password, sql, null);

    org.springframework.core.io.Resource resource = fileService.loadFileAsResource(fileName);
    String contentType = "application/octet-stream";
    return ResponseEntity.ok()
        .contentType(MediaType.parseMediaType(contentType))
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + resource.getFilename())
        .body(resource);
  }

  @PostMapping("/users")
  @ApiOperation("Create IoTDB user")
  public BaseVO setIotDBUser(
      @PathVariable("serverId") Integer serverId,
      @RequestBody IotDBUser iotDBUser,
      HttpServletRequest request)
      throws BaseException {
    check(request, serverId);
    if (iotDBUser.getUserName().matches("^\\d+$")) {
      throw new BaseException(ErrorCode.NOT_SUPPORT_ALL_DIGIT, ErrorCode.NOT_SUPPORT_ALL_DIGIT_MSG);
    }
    Connection connection = connectionService.getById(serverId);
    iotDBService.setIotDBUser(connection, iotDBUser);
    return BaseVO.success("Create successfully", null);
  }

  @DeleteMapping("/users/{userName}")
  @ApiOperation("Delete IoTDB user")
  public BaseVO deleteIotDBUser(
      @PathVariable("serverId") Integer serverId,
      @PathVariable("userName") String userName,
      HttpServletRequest request)
      throws BaseException {
    checkName(userName);
    check(request, serverId);
    Connection connection = connectionService.getById(serverId);
    iotDBService.deleteIotDBUser(connection, userName);
    return BaseVO.success("Delete successfully", null);
  }

  @GetMapping("/users")
  @ApiOperation("Get IoTDB users")
  public BaseVO<List<String>> getIotDBUserList(
      @PathVariable("serverId") Integer serverId, HttpServletRequest request) throws BaseException {
    check(request, serverId);
    Connection connection = connectionService.getById(serverId);
    List<String> users = iotDBService.getIotDBUserList(connection);
    String username = connection.getUsername();
    if (users == null) {
      users = new ArrayList<>();
      users.add(username);
      return BaseVO.success("Get successfully", users);
    }
    // The page needs to place the current user first in the list
    List<String> newUsers = new ArrayList<>();
    newUsers.add(username);
    for (String user : users) {
      if (username.equalsIgnoreCase(user)) {
        continue;
      }
      newUsers.add(user);
    }
    return BaseVO.success("Get successfully", newUsers);
  }

  @GetMapping("/users/{userName}")
  @ApiOperation("Get the password and role list of the specified user")
  public BaseVO<UserRolesVO> getRolesOfUser(
      @PathVariable("serverId") Integer serverId,
      @PathVariable("userName") String userName,
      HttpServletRequest request)
      throws BaseException {
    checkName(userName);
    check(request, serverId);
    Connection connection = connectionService.getById(serverId);
    UserRolesVO userRolesVO = iotDBService.getRolesOfUser(connection, userName);
    return BaseVO.success("Get successfully", userRolesVO);
  }

  @PostMapping("/users/pwd")
  @ApiOperation("Alter user password")
  public BaseVO updatePassword(
      @PathVariable("serverId") Integer serverId,
      @RequestBody IotDBUser iotDBUser,
      HttpServletRequest request)
      throws BaseException {
    check(request, serverId);
    Connection connection = connectionService.getById(serverId);
    iotDBService.updatePwd(connection, iotDBUser);
    return BaseVO.success("Alter password successfully", null);
  }

  @PostMapping("/roles")
  @ApiOperation("Upsert role")
  public BaseVO upsertIotDBRole(
      @PathVariable("serverId") Integer serverId,
      @RequestBody IotDBRole iotDBRole,
      HttpServletRequest request)
      throws BaseException {
    check(request, serverId);
    if (iotDBRole.getRoleName().matches("^\\d+$")) {
      throw new BaseException(ErrorCode.NOT_SUPPORT_ALL_DIGIT, ErrorCode.NOT_SUPPORT_ALL_DIGIT_MSG);
    }
    Connection connection = connectionService.getById(serverId);
    if (iotDBRole.getId() == null) {
      iotDBService.setIotDBRole(connection, iotDBRole);
    }
    roleService.upsertRoleInfo(connection.getHost(), connection.getPort(), iotDBRole);
    return BaseVO.success("Upsert successfully", null);
  }

  @DeleteMapping("/roles/{roleName}")
  @ApiOperation("Delete role")
  public BaseVO deleteIotDBRole(
      @PathVariable("serverId") Integer serverId,
      @PathVariable("roleName") String roleName,
      HttpServletRequest request)
      throws BaseException {
    checkName(roleName);
    check(request, serverId);
    Connection connection = connectionService.getById(serverId);
    iotDBService.deleteIotDBRole(connection, roleName);
    roleService.deleteRoleInfo(connection.getHost(), connection.getPort(), roleName);
    return BaseVO.success("Delete successfully", null);
  }

  @GetMapping("/roles")
  @ApiOperation("Get all roles)")
  public BaseVO<List<String>> getIotDBRoleList(
      @PathVariable("serverId") Integer serverId, HttpServletRequest request) throws BaseException {
    check(request, serverId);
    Connection connection = connectionService.getById(serverId);
    List<String> roles = iotDBService.getIotDBRoleList(connection);
    return BaseVO.success("Get successfully", roles);
  }

  @GetMapping("/roles/{roleName}")
  @ApiOperation("Get information and user list of the specified role")
  public BaseVO<RoleVO> getIotDBRoleInfo(
      @PathVariable("serverId") Integer serverId,
      @PathVariable("roleName") String roleName,
      HttpServletRequest request)
      throws BaseException {
    checkName(roleName);
    check(request, serverId);
    Connection connection = connectionService.getById(serverId);
    RoleVO roleVO = iotDBService.getIotDBRoleInfo(connection, roleName);
    Role roleInfo = roleService.getRoleInfo(connection.getHost(), connection.getPort(), roleName);
    if (roleInfo != null) {
      roleVO.setId(roleInfo.getId());
      roleVO.setDescription(roleInfo.getDescription());
    }
    return BaseVO.success("Get successfully", roleVO);
  }

  @PostMapping("/users/{userName}/grant")
  @ApiOperation("Grant roles to specified user")
  public BaseVO userGrant(
      @PathVariable("serverId") Integer serverId,
      @PathVariable("userName") String userName,
      @RequestBody UserGrantDTO userGrantDTO,
      HttpServletRequest request)
      throws BaseException {
    checkName(userName);
    check(request, serverId);
    Connection connection = connectionService.getById(serverId);
    iotDBService.userGrant(connection, userName, userGrantDTO);
    return BaseVO.success("Upsert successfully", null);
  }

  @PostMapping("/roles/{roleName}/grant")
  @ApiOperation("Grant specified role to users")
  public BaseVO roleGrant(
      @PathVariable("serverId") Integer serverId,
      @PathVariable("roleName") String roleName,
      @RequestBody RoleGrantDTO roleGrantDTO,
      HttpServletRequest request)
      throws BaseException {
    checkName(roleName);
    check(request, serverId);
    Connection connection = connectionService.getById(serverId);
    iotDBService.roleGrant(connection, roleName, roleGrantDTO);
    return BaseVO.success("Upsert successfully", null);
  }

  @GetMapping("/users/{userName}/authorityPrivilege")
  @ApiOperation("Get authority management privileges of user")
  public BaseVO<Set<String>> getUserAuthorityPrivilege(
      @PathVariable("serverId") Integer serverId,
      @PathVariable("userName") String userName,
      HttpServletRequest request)
      throws BaseException {
    checkName(userName);
    check(request, serverId);
    Connection connection = connectionService.getById(serverId);
    Set<String> userAuthorityPrivilege =
        iotDBService.getUserAuthorityPrivilege(connection, userName);
    return BaseVO.success("Get successfully", userAuthorityPrivilege);
  }

  @GetMapping("/users/{userName}/allAuthorityPrivilege")
  @ApiOperation("Get all authority management privileges of user")
  public BaseVO<Set<String>> getAllAuthorityPrivilege(
      @PathVariable("serverId") Integer serverId,
      @PathVariable("userName") String userName,
      HttpServletRequest request)
      throws BaseException {
    checkName(userName);
    check(request, serverId);
    Connection connection = connectionService.getById(serverId);
    Set<String> userAuthorityPrivilege =
        iotDBService.getAllAuthorityPrivilege(connection, userName);
    return BaseVO.success("Get successfully", userAuthorityPrivilege);
  }

  @GetMapping("/roles/{roleName}/authorityPrivilege")
  @ApiOperation("Get authority management privileges of role")
  public BaseVO<Set<String>> getRoleAuthorityPrivilege(
      @PathVariable("serverId") Integer serverId,
      @PathVariable("roleName") String roleName,
      HttpServletRequest request)
      throws BaseException {
    checkName(roleName);
    check(request, serverId);
    Connection connection = connectionService.getById(serverId);
    Set<String> roleAuthorityPrivilege =
        iotDBService.getRoleAuthorityPrivilege(connection, roleName);
    return BaseVO.success("Get successfully", roleAuthorityPrivilege);
  }

  @PostMapping("/users/{userName}/authorityPrivilege")
  @ApiOperation("Modify authority management privileges of user")
  public BaseVO upsertUserAuthorityPrivilege(
      @PathVariable("serverId") Integer serverId,
      @PathVariable("userName") String userName,
      @RequestBody AuthorityPrivilegeDTO authorityPrivilegeDTO,
      HttpServletRequest request)
      throws BaseException {
    checkName(userName);
    check(request, serverId);
    Connection connection = connectionService.getById(serverId);
    iotDBService.upsertAuthorityPrivilege(connection, userName, authorityPrivilegeDTO, "user");
    return BaseVO.success("Upsert successfully", null);
  }

  @PostMapping("/roles/{roleName}/authorityPrivilege")
  @ApiOperation("Modify authority management privileges of role")
  public BaseVO upsertRoleAuthorityPrivilege(
      @PathVariable("serverId") Integer serverId,
      @PathVariable("roleName") String roleName,
      @RequestBody AuthorityPrivilegeDTO authorityPrivilegeDTO,
      HttpServletRequest request)
      throws BaseException {
    checkName(roleName);
    check(request, serverId);
    Connection connection = connectionService.getById(serverId);
    iotDBService.upsertAuthorityPrivilege(connection, roleName, authorityPrivilegeDTO, "role");
    return BaseVO.success("Upsert successfully", null);
  }

  @GetMapping("/users/{userName}/dataPrivilege")
  @ApiOperation("Get data management privileges of user")
  public BaseVO<List<DataPrivilegeVO>> getUserDataPrivilege(
      @PathVariable("serverId") Integer serverId,
      @PathVariable("userName") String userName,
      HttpServletRequest request)
      throws BaseException {
    checkName(userName);
    check(request, serverId);
    Connection connection = connectionService.getById(serverId);
    List<DataPrivilegeVO> dataPrivilegeList =
        iotDBService.getUserDataPrivilege(connection, userName);
    return BaseVO.success("Get successfully", dataPrivilegeList);
  }

  @GetMapping("/roles/{roleName}/dataPrivilege")
  @ApiOperation("Get data management privileges of role")
  public BaseVO<List<DataPrivilegeVO>> getRoleDataPrivilege(
      @PathVariable("serverId") Integer serverId,
      @PathVariable("roleName") String roleName,
      HttpServletRequest request)
      throws BaseException {
    checkName(roleName);
    check(request, serverId);
    Connection connection = connectionService.getById(serverId);
    List<DataPrivilegeVO> dataPrivilegeList =
        iotDBService.getRoleDataPrivilege(connection, roleName);
    return BaseVO.success("Get successfully", dataPrivilegeList);
  }

  @PostMapping("/users/{userName}/dataPrivilege")
  @ApiOperation("Modify data management privileges of user")
  public BaseVO setUserPrivileges(
      @PathVariable("serverId") Integer serverId,
      @PathVariable("userName") String userName,
      @RequestBody PrivilegeInfoDTO privilegeInfoDTO,
      HttpServletRequest request)
      throws BaseException {
    check(request, serverId);
    checkName(userName);
    checkPrivilegeInfoDTO(privilegeInfoDTO);
    Connection connection = connectionService.getById(serverId);
    iotDBService.upsertDataPrivileges(connection, "user", userName, privilegeInfoDTO);
    return BaseVO.success("Upsert successfully", null);
  }

  @PostMapping("/roles/{roleName}/dataPrivilege")
  @ApiOperation("Modify data management privileges of role")
  public BaseVO setRolePrivileges(
      @PathVariable("serverId") Integer serverId,
      @PathVariable("roleName") String roleName,
      @RequestBody PrivilegeInfoDTO privilegeInfoDTO,
      HttpServletRequest request)
      throws BaseException {
    check(request, serverId);
    checkName(roleName);
    checkPrivilegeInfoDTO(privilegeInfoDTO);
    Connection connection = connectionService.getById(serverId);
    iotDBService.upsertDataPrivileges(connection, "role", roleName, privilegeInfoDTO);
    return BaseVO.success("Upsert successfully", null);
  }

  private void checkPrivilegeInfoDTO(PrivilegeInfoDTO privilegeInfoDTO) throws BaseException {
    Integer type = privilegeInfoDTO.getType();
    List<String> groupPaths = privilegeInfoDTO.getGroupPaths();
    List<String> devicePaths = privilegeInfoDTO.getDevicePaths();
    List<String> timeseriesPaths = privilegeInfoDTO.getTimeseriesPaths();
    List<String> delDevicePaths = privilegeInfoDTO.getDelDevicePaths();
    List<String> delGroupPaths = privilegeInfoDTO.getDelGroupPaths();
    List<String> delTimeseriesPaths = privilegeInfoDTO.getDelTimeseriesPaths();
    if (type == null) {
      throw new BaseException(ErrorCode.WRONG_DB_PARAM, ErrorCode.WRONG_DB_PARAM_MSG);
    }
    switch (type) {
      case 0:
        if (groupPaths != null && groupPaths.size() > 0) {
          throw new BaseException(ErrorCode.WRONG_DB_PARAM, ErrorCode.WRONG_DB_PARAM_MSG);
        }
        if (devicePaths != null && devicePaths.size() > 0) {
          throw new BaseException(ErrorCode.WRONG_DB_PARAM, ErrorCode.WRONG_DB_PARAM_MSG);
        }
        if (timeseriesPaths != null && timeseriesPaths.size() > 0) {
          throw new BaseException(ErrorCode.WRONG_DB_PARAM, ErrorCode.WRONG_DB_PARAM_MSG);
        }
        if (delDevicePaths != null && delDevicePaths.size() > 0) {
          throw new BaseException(ErrorCode.WRONG_DB_PARAM, ErrorCode.WRONG_DB_PARAM_MSG);
        }
        if (delGroupPaths != null && delGroupPaths.size() > 0) {
          throw new BaseException(ErrorCode.WRONG_DB_PARAM, ErrorCode.WRONG_DB_PARAM_MSG);
        }
        if (delTimeseriesPaths != null && delTimeseriesPaths.size() > 0) {
          throw new BaseException(ErrorCode.WRONG_DB_PARAM, ErrorCode.WRONG_DB_PARAM_MSG);
        }
        break;
      case 1:
        if (devicePaths != null && devicePaths.size() > 0) {
          throw new BaseException(ErrorCode.WRONG_DB_PARAM, ErrorCode.WRONG_DB_PARAM_MSG);
        }
        if (timeseriesPaths != null && timeseriesPaths.size() > 0) {
          throw new BaseException(ErrorCode.WRONG_DB_PARAM, ErrorCode.WRONG_DB_PARAM_MSG);
        }
        if (delDevicePaths != null && delDevicePaths.size() > 0) {
          throw new BaseException(ErrorCode.WRONG_DB_PARAM, ErrorCode.WRONG_DB_PARAM_MSG);
        }
        if (delTimeseriesPaths != null && delTimeseriesPaths.size() > 0) {
          throw new BaseException(ErrorCode.WRONG_DB_PARAM, ErrorCode.WRONG_DB_PARAM_MSG);
        }
        break;
      case 2:
        if (groupPaths != null && groupPaths.size() > 0) {
          if (groupPaths.size() != 1) {
            throw new BaseException(ErrorCode.WRONG_DB_PARAM, ErrorCode.WRONG_DB_PARAM_MSG);
          }
          if (devicePaths == null || devicePaths.size() == 0) {
            throw new BaseException(ErrorCode.WRONG_DB_PARAM, ErrorCode.WRONG_DB_PARAM_MSG);
          }
        }
        if (devicePaths != null && devicePaths.size() > 0) {
          if (groupPaths == null || groupPaths.size() != 1) {
            throw new BaseException(ErrorCode.WRONG_DB_PARAM, ErrorCode.WRONG_DB_PARAM_MSG);
          }
        }
        if (timeseriesPaths != null && timeseriesPaths.size() > 0) {
          throw new BaseException(ErrorCode.WRONG_DB_PARAM, ErrorCode.WRONG_DB_PARAM_MSG);
        }
        if (delGroupPaths != null && delGroupPaths.size() > 0) {
          if (delGroupPaths.size() != 1) {
            throw new BaseException(ErrorCode.WRONG_DB_PARAM, ErrorCode.WRONG_DB_PARAM_MSG);
          }
          if (delDevicePaths == null || delDevicePaths.size() == 0) {
            throw new BaseException(ErrorCode.WRONG_DB_PARAM, ErrorCode.WRONG_DB_PARAM_MSG);
          }
        }
        if (delDevicePaths != null && delDevicePaths.size() > 0) {
          if (delGroupPaths == null || delGroupPaths.size() != 1) {
            throw new BaseException(ErrorCode.WRONG_DB_PARAM, ErrorCode.WRONG_DB_PARAM_MSG);
          }
        }
        if (delTimeseriesPaths != null && delTimeseriesPaths.size() > 0) {
          throw new BaseException(ErrorCode.WRONG_DB_PARAM, ErrorCode.WRONG_DB_PARAM_MSG);
        }
        break;
      case 3:
        if (groupPaths != null && groupPaths.size() > 0) {
          if (groupPaths.size() != 1) {
            throw new BaseException(ErrorCode.WRONG_DB_PARAM, ErrorCode.WRONG_DB_PARAM_MSG);
          }
          if (devicePaths == null || devicePaths.size() != 1) {
            throw new BaseException(ErrorCode.WRONG_DB_PARAM, ErrorCode.WRONG_DB_PARAM_MSG);
          }
          if (timeseriesPaths == null || timeseriesPaths.size() == 0) {
            throw new BaseException(ErrorCode.WRONG_DB_PARAM, ErrorCode.WRONG_DB_PARAM_MSG);
          }
        }
        if (devicePaths != null && devicePaths.size() > 0) {
          if (devicePaths.size() != 1) {
            throw new BaseException(ErrorCode.WRONG_DB_PARAM, ErrorCode.WRONG_DB_PARAM_MSG);
          }
          if (groupPaths == null || groupPaths.size() != 1) {
            throw new BaseException(ErrorCode.WRONG_DB_PARAM, ErrorCode.WRONG_DB_PARAM_MSG);
          }
          if (timeseriesPaths == null || timeseriesPaths.size() == 0) {
            throw new BaseException(ErrorCode.WRONG_DB_PARAM, ErrorCode.WRONG_DB_PARAM_MSG);
          }
        }
        if (timeseriesPaths != null && timeseriesPaths.size() > 0) {
          if (groupPaths == null || groupPaths.size() != 1) {
            throw new BaseException(ErrorCode.WRONG_DB_PARAM, ErrorCode.WRONG_DB_PARAM_MSG);
          }
          if (devicePaths == null || devicePaths.size() != 1) {
            throw new BaseException(ErrorCode.WRONG_DB_PARAM, ErrorCode.WRONG_DB_PARAM_MSG);
          }
        }
        if (delGroupPaths != null && delGroupPaths.size() > 0) {
          if (delGroupPaths.size() != 1) {
            throw new BaseException(ErrorCode.WRONG_DB_PARAM, ErrorCode.WRONG_DB_PARAM_MSG);
          }
          if (delDevicePaths == null || delDevicePaths.size() != 1) {
            throw new BaseException(ErrorCode.WRONG_DB_PARAM, ErrorCode.WRONG_DB_PARAM_MSG);
          }
          if (delTimeseriesPaths == null || delTimeseriesPaths.size() == 0) {
            throw new BaseException(ErrorCode.WRONG_DB_PARAM, ErrorCode.WRONG_DB_PARAM_MSG);
          }
        }
        if (delDevicePaths != null && delDevicePaths.size() > 0) {
          if (delDevicePaths.size() != 1) {
            throw new BaseException(ErrorCode.WRONG_DB_PARAM, ErrorCode.WRONG_DB_PARAM_MSG);
          }
          if (delGroupPaths == null || delGroupPaths.size() != 1) {
            throw new BaseException(ErrorCode.WRONG_DB_PARAM, ErrorCode.WRONG_DB_PARAM_MSG);
          }
          if (delTimeseriesPaths == null || delTimeseriesPaths.size() == 0) {
            throw new BaseException(ErrorCode.WRONG_DB_PARAM, ErrorCode.WRONG_DB_PARAM_MSG);
          }
        }
        if (delTimeseriesPaths != null && delTimeseriesPaths.size() > 0) {
          if (delGroupPaths == null || delGroupPaths.size() != 1) {
            throw new BaseException(ErrorCode.WRONG_DB_PARAM, ErrorCode.WRONG_DB_PARAM_MSG);
          }
          if (delDevicePaths == null || delDevicePaths.size() != 1) {
            throw new BaseException(ErrorCode.WRONG_DB_PARAM, ErrorCode.WRONG_DB_PARAM_MSG);
          }
        }
        break;
      default:
        throw new BaseException(ErrorCode.WRONG_DB_PARAM, ErrorCode.WRONG_DB_PARAM_MSG);
    }
  }

  private void check(HttpServletRequest request, Integer serverId) throws BaseException {
    Integer userId = AuthenticationUtils.getUserId(request);
    connectionService.check(serverId, userId);
  }

  private void checkParameter(String groupName) throws BaseException {
    String checkName = StringUtils.removeStart(groupName, "root").toLowerCase();
    if (groupName.contains(".root.") || groupName.contains(".root")) {
      throw new BaseException(ErrorCode.NO_SUP_CONTAIN_ROOT, ErrorCode.NO_SUP_CONTAIN_ROOT_MSG);
    }
    if (checkName.contains(".as.")
        || checkName.contains(".null.")
        || checkName.contains(".like.")
        || checkName.matches("^[^ ]*\\.as$")
        || checkName.matches("^[^ ]*\\.null$")
        || checkName.matches("^[^ ]*\\.like$")) {
      throw new BaseException(ErrorCode.NO_SUP_CONTAIN_WORD, ErrorCode.NO_SUP_CONTAIN_WORD_MSG);
    }
  }

  private void checkParameter(String groupName, String deviceName) throws BaseException {
    checkParameter(deviceName);
    checkParameter(groupName);
    if (!deviceName.startsWith(groupName)) {
      throw new BaseException(ErrorCode.WRONG_DB_PARAM, ErrorCode.WRONG_DB_PARAM_MSG);
    }
  }

  private void checkParameter(String groupName, String deviceName, String timeseriesName)
      throws BaseException {
    checkParameter(deviceName, timeseriesName);
    checkParameter(groupName, deviceName);
    if (StringUtils.removeStart(timeseriesName, deviceName + ".").contains(".")) {
      throw new BaseException(
          ErrorCode.MEASUREMENTS_NAME_CONTAIN_DOT, ErrorCode.MEASUREMENTS_NAME_CONTAIN_DOT_MSG);
    }
  }

  private void checkName(String name) throws BaseException {
    if (name == null || !name.matches("^[^ ]{4,}$")) {
      throw new BaseException(ErrorCode.WRONG_DB_PARAM, ErrorCode.WRONG_DB_PARAM_MSG);
    }
  }

  private Long switchTime(String ttlUnit) throws BaseException {
    Long time;
    switch (ttlUnit) {
      case "millisecond":
        time = 1L;
        break;
      case "second":
        time = 1000L;
        break;
      case "minute":
        time = 60 * 1000L;
        break;
      case "hour":
        time = 60 * 60 * 1000L;
        break;
      case "day":
        time = 24 * 60 * 60 * 1000L;
        break;
      case "week":
        time = 7 * 24 * 60 * 60 * 1000L;
        break;
      case "month":
        time = 30 * 24 * 60 * 60 * 1000L;
        break;
      case "year":
        time = 12 * 30 * 24 * 60 * 60 * 1000L;
        break;
      default:
        throw new BaseException(ErrorCode.WRONG_DB_PARAM, ErrorCode.WRONG_DB_PARAM_MSG);
    }
    return time;
  }

  private String getTTL(Long time) {
    long yearTime = 31104000000L;
    long monthTime = 2592000000L;
    long weekTime = 604800000L;
    long dayTime = 86400000L;
    long hourTime = 3600000L;
    long minuteTime = 60000L;
    long secondTime = 1000L;
    if (time == 0) {
      return "millisecond";
    }
    if ((time / yearTime != 0) && (time % yearTime == 0)) {
      return "year";
    }
    if ((time / monthTime != 0) && (time % monthTime == 0)) {
      return "month";
    }
    if ((time / weekTime != 0) && (time % weekTime == 0)) {
      return "week";
    }
    if ((time / dayTime != 0) && (time % dayTime == 0)) {
      return "day";
    }
    if ((time / hourTime != 0) && (time % hourTime == 0)) {
      return "hour";
    }
    if ((time / minuteTime != 0) && (time % minuteTime == 0)) {
      return "minute";
    }
    if ((time / secondTime != 0) && (time % secondTime == 0)) {
      return "second";
    }
    return "millisecond";
  }

  private void checkTtl(Long ttl, String unit) throws BaseException {
    if (ttl == null || unit == null) {
      return;
    }
    if (Long.MAX_VALUE / switchTime(unit) < ttl) {
      throw new BaseException(ErrorCode.TTL_OVER, ErrorCode.TTL_OVER_MSG);
    }
  }
}
