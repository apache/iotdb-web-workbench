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
@Api(value = "iotdb操作相关接口")
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
  @ApiOperation("获取iotdb数据统计信息  (新增2.1)")
  public BaseVO<DataCountVO> getDataCount(
      @PathVariable("serverId") Integer serverId, HttpServletRequest request) throws BaseException {
    check(request, serverId);
    Connection connection = connectionService.getById(serverId);
    DataCountVO dataCountVO = iotDBService.getDataCount(connection);
    return BaseVO.success("获取统计信息成功", dataCountVO);
  }

  @GetMapping("/dataModel")
  @ApiOperation("获取iotdb数据模型  (新增2.2)")
  public BaseVO<DataModelVO> getDataModel(
      @PathVariable("serverId") Integer serverId, HttpServletRequest request) throws BaseException {
    check(request, serverId);
    Connection connection = connectionService.getById(serverId);
    DataModelVO dataModelVO = iotDBService.getDataModel(connection);
    return BaseVO.success("获取数据模型成功", dataModelVO);
  }

  @GetMapping("/storageGroups/info")
  @ApiOperation("获得存储组信息列表")
  public BaseVO<List<GroupInfoVO>> getAllStorageGroupsInfo(
      @PathVariable("serverId") Integer serverId, HttpServletRequest request) throws BaseException {
    check(request, serverId);
    Connection connection = connectionService.getById(serverId);
    List<String> groupNames = iotDBService.getAllStorageGroups(connection);
    List<GroupInfoVO> groupInfoList = new ArrayList<>();
    if (groupNames == null || groupNames.size() == 0) {
      return BaseVO.success("获取成功", groupInfoList);
    }
    String host = connection.getHost();
    List<Integer> deviceCounts = iotDBService.getDevicesCount(connection, groupNames);
    List<String> descriptions = groupService.getGroupDescription(host, groupNames);
    for (int i = 0; i < groupNames.size(); i++) {
      GroupInfoVO groupInfoVO = new GroupInfoVO();
      groupInfoVO.setGroupName(groupNames.get(i));
      groupInfoVO.setDeviceCount(deviceCounts.get(i));
      groupInfoVO.setDescription(descriptions.get(i));
      groupInfoList.add(groupInfoVO);
    }
    return BaseVO.success("获取成功", groupInfoList);
  }

  @GetMapping("/storageGroups")
  @ApiOperation("获得存储组列表")
  public BaseVO<List<StorageGroupVO>> getAllStorageGroups(
      @PathVariable("serverId") Integer serverId, HttpServletRequest request) throws BaseException {
    check(request, serverId);
    Connection connection = connectionService.getById(serverId);
    List<StorageGroupVO> storageGroupVOList = new ArrayList<>();
    List<String> groupNames = iotDBService.getAllStorageGroups(connection);
    if (groupNames == null || groupNames.size() == 0) {
      return BaseVO.success("获取成功", storageGroupVOList);
    }
    String host = connection.getHost();
    for (String groupName : groupNames) {
      StorageGroupVO storageGroupVO = new StorageGroupVO();
      Integer id = groupService.getGroupId(host, groupName);
      storageGroupVO.setGroupId(id);
      storageGroupVO.setGroupName(groupName);
      storageGroupVOList.add(storageGroupVO);
    }
    return BaseVO.success("获取成功", storageGroupVOList);
  }

  @GetMapping("/storageGroups/nodeTree")
  @ApiOperation("获得存储组列表(节点树形结构) (新增2.27)")
  public BaseVO<List<NodeTreeVO>> getGroupsNodeTree(
      @PathVariable("serverId") Integer serverId, HttpServletRequest request) throws BaseException {
    check(request, serverId);
    Connection connection = connectionService.getById(serverId);
    List<NodeTreeVO> groupsNodeTree = iotDBService.getGroupsNodeTree(connection);
    return BaseVO.success("获取成功", groupsNodeTree);
  }

  @PostMapping("/storageGroups")
  @ApiOperation("新增或修改存储组")
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
    checkTtl(ttl, ttlUnit);
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
    return BaseVO.success("新增或更新成功", null);
  }

  @DeleteMapping("/storageGroups/{groupName}")
  @ApiOperation("删除存储组")
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
    return BaseVO.success("删除成功", null);
  }

  @GetMapping("/storageGroups/{groupName}")
  @ApiOperation("存储组详情获取")
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
    return BaseVO.success("获取成功", groupVO);
  }

  @GetMapping("/storageGroups/{groupName}/devices/info")
  @ApiOperation("获取指定存储组下的实体(设备)信息列表")
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
        if (devices.get(i) != null) {
          deviceInfo.setDeviceId(devices.get(i).getId());
          deviceInfo.setCreator(devices.get(i).getCreator());
          deviceInfo.setDescription(devices.get(i).getDescription());
        }
        deviceInfos.add(deviceInfo);
      }
    }
    deviceInfoVO.setDeviceInfos(deviceInfos);
    return BaseVO.success("获取设备信息列表成功", deviceInfoVO);
  }

  @GetMapping("/storageGroups/{groupName}/devices")
  @ApiOperation("获取指定存储组下的实体列表")
  public BaseVO<List<String>> getDevicesByGroup(
      @PathVariable("serverId") Integer serverId,
      @PathVariable("groupName") String groupName,
      HttpServletRequest request)
      throws BaseException {
    checkParameter(groupName);
    check(request, serverId);
    Connection connection = connectionService.getById(serverId);
    final List<String> devices = iotDBService.getDevices(connection, groupName);
    return BaseVO.success("获取成功", devices);
  }

  @GetMapping("/storageGroups/{groupName}/devices/nodeTree")
  @ApiOperation("获取指定存储组下的实体列表(节点树形结构)  (新增2.28)")
  public BaseVO<List<NodeTreeVO>> getDevicesNodeTreeByGroup(
      @PathVariable("serverId") Integer serverId,
      @PathVariable("groupName") String groupName,
      HttpServletRequest request)
      throws BaseException {
    checkParameter(groupName);
    check(request, serverId);
    Connection connection = connectionService.getById(serverId);
    List<NodeTreeVO> deviceList = iotDBService.getDeviceNodeTree(connection, groupName);
    return BaseVO.success("获取设备列表成功", deviceList);
  }

  @GetMapping("/storageGroups/{groupName}/devices/tree")
  @ApiOperation("获取指定存储组下的实体列表(树形结构)  (新增2.29)")
  public BaseVO<NodeTreeVO> getDevicesTreeByGroup(
      @PathVariable("serverId") Integer serverId,
      @PathVariable("groupName") String groupName,
      HttpServletRequest request)
      throws BaseException {
    checkParameter(groupName);
    check(request, serverId);
    Connection connection = connectionService.getById(serverId);
    NodeTreeVO deviceList = iotDBService.getDeviceList(connection, groupName);
    return BaseVO.success("获取设备列表成功", deviceList);
  }

  @GetMapping("/storageGroups/{groupName}/devices/{deviceName}/exist")
  @ApiOperation("判断设备是否已存在  (新增2.3)")
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
    return BaseVO.success("获取成功", isExist);
  }

  @PostMapping("/storageGroups/{groupName}/devices")
  @ApiOperation("新增或编辑实体(设备)  (变更1.2)")
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
    return BaseVO.success("新增或更新成功", null);
  }

  @DeleteMapping("/storageGroups/{groupName}/devices/{deviceName}")
  @ApiOperation("删除实体(设备)")
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
    return BaseVO.success("删除成功", null);
  }

  @GetMapping("/storageGroups/{groupName}/devices/{deviceName}")
  @ApiOperation("获取实体(设备)详情")
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
    return BaseVO.success("获取成功", deviceVO);
  }

  @GetMapping("/storageGroups/{groupName}/devices/{deviceName}/info")
  @ApiOperation("获取指定实体(设备)下的物理量列表详情  (变更1.3)")
  public BaseVO<MeasuremtnInfoVO> getMeasurementsByDeviceName(
      @PathVariable("serverId") Integer serverId,
      @PathVariable("groupName") String groupName,
      @PathVariable("deviceName") String deviceName,
      @RequestParam("pageSize") Integer pageSize,
      @RequestParam("pageNum") Integer pageNum,
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
      for (MeasurementDTO measurementDTO : measurementDTOList) {
        MeasurementVO measurementVO = new MeasurementVO();
        BeanUtils.copyProperties(measurementDTO, measurementVO);
        String description =
            measurementService.getDescription(host, measurementDTO.getTimeseries());
        String newValue =
            iotDBService.getLastMeasurementValue(connection, measurementDTO.getTimeseries());
        Integer dataCount =
            iotDBService.getOneDataCount(connection, deviceName, measurementDTO.getTimeseries());
        measurementVO.setDataCount(dataCount);
        measurementVO.setNewValue(newValue);
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
      }
    }
    MeasuremtnInfoVO measuremtnInfoVO = new MeasuremtnInfoVO();
    measuremtnInfoVO.setTotalCount(countDTO.getTotalCount());
    measuremtnInfoVO.setTotalPage(countDTO.getTotalPage());
    measuremtnInfoVO.setMeasurementVOList(measurementVOList);
    return BaseVO.success("获取成功", measuremtnInfoVO);
  }

  @GetMapping("/storageGroups/{groupName}/devices/{deviceName}/timeseries/{timeseriesName}")
  @ApiOperation("获取指定物理量的最新两百条数据记录 (变更1.4)")
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
    return BaseVO.success("获取成功", recordVO);
  }

  @GetMapping("/storageGroups/{groupName}/devices/{deviceName}/timeseries")
  @ApiOperation("指定设备下的物理量列表")
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
    return BaseVO.success("获取成功", timeseries);
  }

  @DeleteMapping("/storageGroups/{groupName}/devices/{deviceName}/timeseries/{timeseriesName}")
  @ApiOperation("删除物理量")
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
    return BaseVO.success("删除成功", null);
  }

  @PostMapping("/storageGroups/{groupName}/devices/{deviceName}/data")
  @ApiOperation("获取指定设备下的物理量数据  (新增2.4)")
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
    return BaseVO.success("获取物理量数据成功", dataVO);
  }

  @PutMapping("/storageGroups/{groupName}/devices/{deviceName}/data")
  @ApiOperation("编辑物理量数据  (新增2.5)")
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
    return BaseVO.success("编辑物理量数据成功", null);
  }

  @DeleteMapping("/storageGroups/{groupName}/devices/{deviceName}/data")
  @ApiOperation("删除物理量数据  (新增2.6)")
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
    return BaseVO.success("删除物理量数据成功", null);
  }

  @PostMapping("/storageGroups/{groupName}/devices/{deviceName}/randomImport")
  @ApiOperation("随机批量导入指定设备下的物理量数据 (新增2.7）")
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
    return BaseVO.success("随机导入物理量数据成功", null);
  }

  @ApiOperation("导出指定实体下的物理量数据  (新增2.9)")
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
  @ApiOperation("创建数据库用户")
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
    return BaseVO.success("创建成功", null);
  }

  @DeleteMapping("/users/{userName}")
  @ApiOperation("删除数据库用户")
  public BaseVO deleteIotDBUser(
      @PathVariable("serverId") Integer serverId,
      @PathVariable("userName") String userName,
      HttpServletRequest request)
      throws BaseException {
    checkName(userName);
    check(request, serverId);
    Connection connection = connectionService.getById(serverId);
    iotDBService.deleteIotDBUser(connection, userName);
    return BaseVO.success("删除成功", null);
  }

  @GetMapping("/users")
  @ApiOperation("获取数据库用户列表")
  public BaseVO<List<String>> getIotDBUserList(
      @PathVariable("serverId") Integer serverId, HttpServletRequest request) throws BaseException {
    check(request, serverId);
    Connection connection = connectionService.getById(serverId);
    List<String> users = iotDBService.getIotDBUserList(connection);
    String username = connection.getUsername();
    if (users == null) {
      users = new ArrayList<>();
      users.add(username);
      return BaseVO.success("获取成功", users);
    }
    // 前端需要将当前用户处于列表第一位
    List<String> newUsers = new ArrayList<>();
    newUsers.add(username);
    for (String user : users) {
      if (username.equalsIgnoreCase(user)) {
        continue;
      }
      newUsers.add(user);
    }
    return BaseVO.success("获取成功", newUsers);
  }

  @GetMapping("/users/{userName}")
  @ApiOperation("获取指定用户的密码和角色列表  (新增2.15)")
  public BaseVO<UserRolesVO> getRolesOfUser(
      @PathVariable("serverId") Integer serverId,
      @PathVariable("userName") String userName,
      HttpServletRequest request)
      throws BaseException {
    checkName(userName);
    check(request, serverId);
    Connection connection = connectionService.getById(serverId);
    UserRolesVO userRolesVO = iotDBService.getRolesOfUser(connection, userName);
    return BaseVO.success("获取成功", userRolesVO);
  }

  @PostMapping("/users/pwd")
  @ApiOperation("改变用户密码")
  public BaseVO updatePassword(
      @PathVariable("serverId") Integer serverId,
      @RequestBody IotDBUser iotDBUser,
      HttpServletRequest request)
      throws BaseException {
    check(request, serverId);
    Connection connection = connectionService.getById(serverId);
    iotDBService.updatePwd(connection, iotDBUser);
    return BaseVO.success("修改成功", null);
  }

  @PostMapping("/roles")
  @ApiOperation("新增或编辑角色   (新增2.11)")
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
    return BaseVO.success("创建或更新成功", null);
  }

  @DeleteMapping("/roles/{roleName}")
  @ApiOperation("删除指定角色  (新增2.12)")
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
    return BaseVO.success("删除成功", null);
  }

  @GetMapping("/roles")
  @ApiOperation("获取所有角色   (新增2.13)")
  public BaseVO<List<String>> getIotDBRoleList(
      @PathVariable("serverId") Integer serverId, HttpServletRequest request) throws BaseException {
    check(request, serverId);
    Connection connection = connectionService.getById(serverId);
    List<String> roles = iotDBService.getIotDBRoleList(connection);
    return BaseVO.success("获取成功", roles);
  }

  @GetMapping("/roles/{roleName}")
  @ApiOperation("获取指定角色的信息和用户列表   (新增2.14)")
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
    return BaseVO.success("获取成功", roleVO);
  }

  @PostMapping("/users/{userName}/grant")
  @ApiOperation("给指定用户赋予角色  (新增2.16)")
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
    return BaseVO.success("操作成功", null);
  }

  @PostMapping("/roles/{roleName}/grant")
  @ApiOperation("把指定角色赋予给用户  (新增2.17)")
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
    return BaseVO.success("操作成功", null);
  }

  @GetMapping("/users/{userName}/authorityPrivilege")
  @ApiOperation("获取用户权限管理权限 (新增2.20)")
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
    return BaseVO.success("获取成功", userAuthorityPrivilege);
  }

  @GetMapping("/users/{userName}/allAuthorityPrivilege")
  @ApiOperation("获取用户权限管理权限(包含用户的角色拥有的） (新增2.30)")
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
    return BaseVO.success("获取成功", userAuthorityPrivilege);
  }

  @GetMapping("/roles/{roleName}/authorityPrivilege")
  @ApiOperation("获取角色权限管理权限 (新增2.21)")
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
    return BaseVO.success("获取成功", roleAuthorityPrivilege);
  }

  @PostMapping("/users/{userName}/authorityPrivilege")
  @ApiOperation("修改用户权限管理权限 (新增2.24)")
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
    return BaseVO.success("修改成功", null);
  }

  @PostMapping("/roles/{roleName}/authorityPrivilege")
  @ApiOperation("修改角色权限管理权限 (新增2.25)")
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
    return BaseVO.success("修改成功", null);
  }

  @GetMapping("/users/{userName}/dataPrivilege")
  @ApiOperation("获取用户数据管理权限 (新增2.18)")
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
    return BaseVO.success("获取成功", dataPrivilegeList);
  }

  @GetMapping("/roles/{roleName}/dataPrivilege")
  @ApiOperation("获取角色数据管理权限 (新增2.19)")
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
    return BaseVO.success("获取成功", dataPrivilegeList);
  }

  @PostMapping("/users/{userName}/dataPrivilege")
  @ApiOperation("修改用户数据管理权限 (新增2.22)")
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
    return BaseVO.success("操作成功", null);
  }

  @PostMapping("/roles/{roleName}/dataPrivilege")
  @ApiOperation("修改角色数据管理权限 (新增2.23)")
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
    return BaseVO.success("操作成功", null);
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
    if (!groupName.matches("^root\\.[^ ]+$")
        || checkName.contains(".root.")
        || checkName.matches("^[^ ]*\\.root$")) {
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
    if (Long.MAX_VALUE / switchTime(unit) < ttl) {
      throw new BaseException(ErrorCode.TTL_OVER, ErrorCode.TTL_OVER_MSG);
    }
  }
}
