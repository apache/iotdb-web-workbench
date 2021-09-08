package org.apache.iotdb.admin.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.iotdb.admin.common.exception.BaseException;
import org.apache.iotdb.admin.common.exception.ErrorCode;
import org.apache.iotdb.admin.common.utils.AuthenticationUtils;
import org.apache.iotdb.admin.model.dto.*;
import org.apache.iotdb.admin.model.entity.Connection;
import org.apache.iotdb.admin.model.entity.Device;
import org.apache.iotdb.admin.model.entity.StorageGroup;
import org.apache.iotdb.admin.model.vo.*;
import org.apache.iotdb.admin.service.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@Api(value = "iotdb操作相关接口")
@RequestMapping("/servers/{serverId}")
public class IotDBController {

    @Autowired
    private ConnectionService connectionService;

    @Autowired
    private IotDBService iotDBService;

    @Autowired
    private GroupService groupService;

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private MeasurementService measurementService;

    @GetMapping("/dataCount")
    @ApiOperation("获取iotdb数据统计信息  (新增2.1)")
    public BaseVO getDataCount(@PathVariable("serverId") Integer serverId, HttpServletRequest request) throws BaseException {
        check(request, serverId);
        Connection connection = connectionService.getById(serverId);
        DataCountVO dataCountVO = iotDBService.getDataCount(connection);
        return BaseVO.success("获取统计信息成功", dataCountVO);
    }

    @GetMapping("/storageGroups/info")
    @ApiOperation("获得存储组信息列表")
    public BaseVO<List<GroupInfoVO>> getAllStorageGroupsInfo(@PathVariable("serverId") Integer serverId, HttpServletRequest request) throws BaseException {
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
    public BaseVO<List<StorageGroupVO>> getAllStorageGroups(@PathVariable("serverId") Integer serverId, HttpServletRequest request) throws BaseException {
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

    @PostMapping("/storageGroups")
    @ApiOperation("新增或修改存储组")
    public BaseVO saveStorageGroup(@PathVariable("serverId") Integer serverId,
                                   @RequestBody GroupDTO groupDTO,
                                   HttpServletRequest request) throws BaseException {
        check(request, serverId);
        Connection connection = connectionService.getById(serverId);
        String groupName = groupDTO.getGroupName();
        String checkName = groupName.toLowerCase();
        if (checkName.contains("root")) {
            throw new BaseException(ErrorCode.NO_SUP_CONTAIN_ROOT, ErrorCode.NO_SUP_CONTAIN_ROOT_MSG);
        }
        groupName = "root." + groupName;
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
    public BaseVO deleteStorageGroup(@PathVariable("serverId") Integer serverId,
                                     @PathVariable("groupName") String groupName,
                                     HttpServletRequest request) throws BaseException {
        if (groupName == null || !groupName.matches("^[^ ]+$")) {
            throw new BaseException(ErrorCode.WRONG_DB_PARAM, ErrorCode.WRONG_DB_PARAM_MSG);
        }
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
    public BaseVO<GroupVO> getStorageGroup(@PathVariable("serverId") Integer serverId,
                                           @PathVariable("groupName") String groupName,
                                           HttpServletRequest request) throws BaseException {
        if (groupName == null || !groupName.matches("^[^ ]+$")) {
            throw new BaseException(ErrorCode.WRONG_DB_PARAM, ErrorCode.WRONG_DB_PARAM_MSG);
        }
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
    public BaseVO<DeviceInfoVO> getDevicesInfoByGroupName(@PathVariable("serverId") Integer serverId,
                                                          @PathVariable("groupName") String groupName,
                                                          @RequestParam("pageSize") Integer pageSize,
                                                          @RequestParam("pageNum") Integer pageNum,
                                                          @RequestParam(value = "keyword", required = false) String keyword,
                                                          HttpServletRequest request) throws BaseException {
        if (groupName == null || !groupName.matches("^[^ ]+$")) {
            throw new BaseException(ErrorCode.WRONG_DB_PARAM, ErrorCode.WRONG_DB_PARAM_MSG);
        }
        if (pageSize == null || pageNum == null) {
            throw new BaseException(ErrorCode.WRONG_DB_PARAM, ErrorCode.WRONG_DB_PARAM_MSG);
        }
        check(request, serverId);
        Connection connection = connectionService.getById(serverId);
        CountDTO countDTO = iotDBService.getDevicesByGroup(connection, groupName, pageSize, pageNum, keyword);
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
                /*if (groupName.equals(deviceName)) {
                    continue;
                }*/
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
    @ApiOperation("获取指定存储组下的实体(设备)列表  (变更1.1)")
    public BaseVO<DeviceNodeVO> getDevicesByGroupName(@PathVariable("serverId") Integer serverId,
                                                      @PathVariable("groupName") String groupName,
                                                      HttpServletRequest request) throws BaseException {
        if (groupName == null || !groupName.matches("^[^ ]+$")) {
            throw new BaseException(ErrorCode.WRONG_DB_PARAM, ErrorCode.WRONG_DB_PARAM_MSG);
        }
        check(request, serverId);
        Connection connection = connectionService.getById(serverId);
        DeviceNodeVO deviceList = iotDBService.getDeviceList(connection, groupName);
        return BaseVO.success("获取设备列表成功", deviceList);
    }

    @GetMapping("/storageGroups/{groupName}/devices/{deviceName}/exist")
    @ApiOperation("判断设备是否已存在  (新增2.3)")
    public BaseVO<Boolean> deviceExist(@PathVariable("serverId") Integer serverId,
                                       @PathVariable("groupName") String groupName,
                                       @PathVariable("deviceName") String deviceName,
                                       HttpServletRequest request) throws BaseException {
        if (groupName == null || !groupName.matches("^[^ ]+$")) {
            throw new BaseException(ErrorCode.WRONG_DB_PARAM, ErrorCode.WRONG_DB_PARAM_MSG);
        }
        if (deviceName == null || !deviceName.matches("^[^ ]+$")) {
            throw new BaseException(ErrorCode.WRONG_DB_PARAM, ErrorCode.WRONG_DB_PARAM_MSG);
        }
        check(request, serverId);
        Connection connection = connectionService.getById(serverId);
        Boolean isExist = iotDBService.deviceExist(connection, groupName, deviceName);
        return BaseVO.success("获取成功", isExist);
    }

    @PostMapping("/storageGroups/{groupName}/devices")
    @ApiOperation("新增或编辑实体(设备)  (变更1.2)")
    public BaseVO saveOrUpdateDevice(@PathVariable("serverId") Integer serverId,
                                     @PathVariable("groupName") String groupName,
                                     @RequestBody DeviceInfoDTO deviceInfoDTO,
                                     HttpServletRequest request) throws BaseException {
        if (groupName == null || !groupName.matches("^[^ ]+$")) {
            throw new BaseException(ErrorCode.WRONG_DB_PARAM, ErrorCode.WRONG_DB_PARAM_MSG);
        }
        if (deviceInfoDTO.getDeviceDTOList() == null || deviceInfoDTO.getDeviceDTOList().size() == 0) {
            throw new BaseException(ErrorCode.WRONG_DB_PARAM, ErrorCode.WRONG_DB_PARAM_MSG);
        }
        check(request, serverId);
        Connection connection = connectionService.getById(serverId);
//        groupName = "root." + groupName;
//        deviceInfoDTO.setDeviceName(groupName + "." + deviceInfoDTO.getDeviceName());
//        for (DeviceDTO deviceDTO : deviceInfoDTO.getDeviceDTOList()) {
//            deviceDTO.setTimeseries(deviceInfoDTO.getDeviceName() + "." + deviceDTO.getTimeseries());
//        }
        iotDBService.createDeviceWithMeasurements(connection, deviceInfoDTO);
        iotDBService.upsertMeasurementAlias(connection, deviceInfoDTO.getDeviceDTOList());
        iotDBService.upsertMeasurementTags(connection, deviceInfoDTO.getDeviceDTOList());
        iotDBService.upsertMeasurementAttributes(connection, deviceInfoDTO.getDeviceDTOList());
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
    public BaseVO deleteDevice(@PathVariable("serverId") Integer serverId,
                               @PathVariable("groupName") String groupName,
                               @PathVariable("deviceName") String deviceName,
                               HttpServletRequest request) throws BaseException {
        if (groupName == null || !groupName.matches("^[^ ]+$")) {
            throw new BaseException(ErrorCode.WRONG_DB_PARAM, ErrorCode.WRONG_DB_PARAM_MSG);
        }
        if (deviceName == null || !deviceName.matches("^[^ ]+$")) {
            throw new BaseException(ErrorCode.WRONG_DB_PARAM, ErrorCode.WRONG_DB_PARAM_MSG);
        }
        check(request, serverId);
        Connection connection = connectionService.getById(serverId);
//        groupName = "root." + groupName;
//        deviceName = groupName + "." + deviceName;
        String host = connection.getHost();
        List<String> deletedTimeseriesList = iotDBService.deleteTimeseriesByDevice(connection, deviceName);
        deviceService.deleteDeviceInfoByDeviceName(host, deviceName);
        for (String timeseries : deletedTimeseriesList) {
            measurementService.deleteMeasurementInfo(host, timeseries);
        }
        return BaseVO.success("删除成功", null);
    }

    @GetMapping("/storageGroups/{groupName}/devices/{deviceName}")
    @ApiOperation("获取实体(设备)详情")
    public BaseVO<DeviceVO> getDeviceInfo(@PathVariable("serverId") Integer serverId,
                                          @PathVariable("groupName") String groupName,
                                          @PathVariable("deviceName") String deviceName,
                                          HttpServletRequest request) throws BaseException {
        if (deviceName == null || !deviceName.matches("^[^ ]+$")) {
            throw new BaseException(ErrorCode.WRONG_DB_PARAM, ErrorCode.WRONG_DB_PARAM_MSG);
        }
        check(request, serverId);
        Connection connection = connectionService.getById(serverId);
//        groupName = "root." + groupName;
//        deviceName = groupName + "." + deviceName;
        String host = connection.getHost();
        DeviceVO deviceVO = deviceService.getDevice(host, deviceName);
        return BaseVO.success("获取成功", deviceVO);
    }

    @GetMapping("/storageGroups/{groupName}/devices/{deviceName}/info")
    @ApiOperation("获取指定实体(设备)下的物理量列表详情  (变更1.3)")
    public BaseVO<MeasuremtnInfoVO> getMeasurementsByDeviceName(@PathVariable("serverId") Integer serverId,
                                                                @PathVariable("groupName") String groupName,
                                                                @PathVariable("deviceName") String deviceName,
                                                                @RequestParam("pageSize") Integer pageSize,
                                                                @RequestParam("pageNum") Integer pageNum,
                                                                @RequestParam(value = "keyword", required = false) String keyword,
                                                                HttpServletRequest request) throws BaseException {
        if (groupName == null || !groupName.matches("^[^ ]+$")) {
            throw new BaseException(ErrorCode.WRONG_DB_PARAM, ErrorCode.WRONG_DB_PARAM_MSG);
        }
        if (deviceName == null || !deviceName.matches("^[^ ]+$")) {
            throw new BaseException(ErrorCode.WRONG_DB_PARAM, ErrorCode.WRONG_DB_PARAM_MSG);
        }
        check(request, serverId);
        Connection connection = connectionService.getById(serverId);
//        groupName = "root." + groupName;
//        deviceName = groupName + "." + deviceName;
        CountDTO countDTO = iotDBService.getMeasurementsByDevice(connection, deviceName, pageSize, pageNum, keyword);
        List<MeasurementDTO> measurementDTOList = countDTO.getObjects();
        List<MeasurementVO> measurementVOList = new ArrayList<>();
        String host = connection.getHost();
        if (measurementDTOList != null) {
            for (MeasurementDTO measurementDTO : measurementDTOList) {
                MeasurementVO measurementVO = new MeasurementVO();
                BeanUtils.copyProperties(measurementDTO, measurementVO);
//                if (measurementVO.getTimeseries() != null) {
//                    measurementVO.setTimeseries(measurementVO.getTimeseries().replaceFirst(deviceName + ".", ""));
//                }
                String description = measurementService.getDescription(host, measurementDTO.getTimeseries());
                String newValue = iotDBService.getLastMeasurementValue(connection, measurementDTO.getTimeseries());
                Integer dataCount = iotDBService.getOneDataCount(connection, deviceName, measurementDTO.getTimeseries());
                measurementVO.setDataCount(dataCount);
                measurementVO.setNewValue(newValue);
                measurementVO.setDescription(description);
                ObjectMapper mapper = new ObjectMapper();
                try {
                    if (!"null".equals(measurementDTO.getTags())) {
                        measurementVO.setTags(mapper.readValue(measurementDTO.getTags(), Map.class));
                    }
                    if (!"null".equals(measurementDTO.getAttributes())) {
                        measurementVO.setAttributes(mapper.readValue(measurementDTO.getAttributes(), Map.class));
                    }
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
    public BaseVO<RecordVO> getMeasurementInfo(@PathVariable("serverId") Integer serverId,
                                               @PathVariable("groupName") String groupName,
                                               @PathVariable("deviceName") String deviceName,
                                               @PathVariable("timeseriesName") String timeseriesName,
                                               @RequestParam("dataType") String dataType,
                                               HttpServletRequest request) throws BaseException {
        if (groupName == null || !groupName.matches("^[^ ]+$")) {
            throw new BaseException(ErrorCode.WRONG_DB_PARAM, ErrorCode.WRONG_DB_PARAM_MSG);
        }
        if (deviceName == null || !deviceName.matches("^[^ ]+$")) {
            throw new BaseException(ErrorCode.WRONG_DB_PARAM, ErrorCode.WRONG_DB_PARAM_MSG);
        }
        check(request, serverId);
        Connection connection = connectionService.getById(serverId);
//        groupName = "root." + groupName;
//        deviceName = groupName + "." + deviceName;
        RecordVO recordVO = iotDBService.getRecords(connection, deviceName, timeseriesName, dataType);
        return BaseVO.success("获取成功", recordVO);
    }

    @PostMapping("/storageGroups/{groupName}/devices/{deviceName}/timeseries")
    @ApiOperation("创建时间序列  (未使用)")
    public BaseVO insertTimeseries(@PathVariable("serverId") Integer serverId,
                                   @PathVariable("groupName") String groupName,
                                   @PathVariable("deviceName") String deviceName,
                                   @RequestBody Timeseries timeseries,
                                   HttpServletRequest request) throws BaseException {
        if (deviceName == null || !deviceName.matches("^[^ ]+$")) {
            throw new BaseException(ErrorCode.WRONG_DB_PARAM, ErrorCode.WRONG_DB_PARAM_MSG);
        }
        check(request, serverId);
        Connection connection = connectionService.getById(serverId);
        groupName = "root." + groupName;
        deviceName = groupName + "." + deviceName;
        iotDBService.insertTimeseries(connection, deviceName, timeseries);
        return BaseVO.success("创建成功", null);
    }

    @GetMapping("/storageGroups/{groupName}/devices/{deviceName}/timeseries/info")
    @ApiOperation("指定设备下的所有物理量  (未使用)")
    public BaseVO<SqlResultVO> showTimeseries(@PathVariable("serverId") Integer serverId,
                                              @PathVariable("groupName") String groupName,
                                              @PathVariable("deviceName") String deviceName,
                                              HttpServletRequest request) throws BaseException {
        if (deviceName == null || !deviceName.matches("^[^ ]+$")) {
            throw new BaseException(ErrorCode.WRONG_DB_PARAM, ErrorCode.WRONG_DB_PARAM_MSG);
        }
        check(request, serverId);
        Connection connection = connectionService.getById(serverId);
        groupName = "root." + groupName;
        deviceName = groupName + "." + deviceName;
        SqlResultVO resultVO = iotDBService.showTimeseries(connection, deviceName);
        return BaseVO.success("获取成功", resultVO);
    }

    @GetMapping("/storageGroups/{groupName}/devices/{deviceName}/timeseries")
    @ApiOperation("指定设备下的物理量列表")
    public BaseVO<List<String>> getTimeseries(@PathVariable("serverId") Integer serverId,
                                              @PathVariable("groupName") String groupName,
                                              @PathVariable("deviceName") String deviceName,
                                              HttpServletRequest request) throws BaseException {
        if (deviceName == null || !deviceName.matches("^[^ ]+$")) {
            throw new BaseException(ErrorCode.WRONG_DB_PARAM, ErrorCode.WRONG_DB_PARAM_MSG);
        }
        if (groupName == null || !deviceName.contains(groupName)) {
            throw new BaseException(ErrorCode.WRONG_DB_PARAM, ErrorCode.WRONG_DB_PARAM_MSG);
        }
        check(request, serverId);
        Connection connection = connectionService.getById(serverId);
//        groupName = "root." + groupName;
//        deviceName = groupName + "." + deviceName;
        List<String> timeseries = iotDBService.getTimeseries(connection, deviceName);
        return BaseVO.success("获取成功", timeseries);
    }

    @DeleteMapping("/storageGroups/{groupName}/devices/{deviceName}/timeseries/{timeseriesName}")
    @ApiOperation("删除物理量")
    public BaseVO deleteTimeseries(@PathVariable("serverId") Integer serverId,
                                   @PathVariable("groupName") String groupName,
                                   @PathVariable("deviceName") String deviceName,
                                   @PathVariable("timeseriesName") String timeseriesName,
                                   HttpServletRequest request) throws BaseException {
        if (timeseriesName == null || !timeseriesName.matches("^[^ ]+$")) {
            throw new BaseException(ErrorCode.WRONG_DB_PARAM, ErrorCode.WRONG_DB_PARAM_MSG);
        }
        check(request, serverId);
        Connection connection = connectionService.getById(serverId);
//        groupName = "root." + groupName;
//        deviceName = groupName + "." + deviceName;
//        timeseriesName = deviceName + "." + timeseriesName;
        iotDBService.deleteTimeseries(connection, timeseriesName);
        String host = connection.getHost();
        measurementService.deleteMeasurementInfo(host, timeseriesName);
        return BaseVO.success("删除成功", null);
    }

    @PostMapping("/storageGroups/{groupName}/devices/{deviceName}/data")
    @ApiOperation("获取指定设备下的物理量数据  (新增2.4)")
    public BaseVO<DataVO> getDataByDevice(@PathVariable("serverId") Integer serverId,
                                          @PathVariable("groupName") String groupName,
                                          @PathVariable("deviceName") String deviceName,
                                          @RequestParam("pageSize") Integer pageSize,
                                          @RequestParam("pageNum") Integer pageNum,
                                          @RequestBody DataQueryDTO dataQueryDTO,
                                          HttpServletRequest request) throws BaseException {
        if (deviceName == null || !deviceName.matches("^[^ ]+$")) {
            throw new BaseException(ErrorCode.WRONG_DB_PARAM, ErrorCode.WRONG_DB_PARAM_MSG);
        }
        if (groupName == null || !deviceName.contains(groupName)) {
            throw new BaseException(ErrorCode.WRONG_DB_PARAM, ErrorCode.WRONG_DB_PARAM_MSG);
        }
        check(request, serverId);
        Connection connection = connectionService.getById(serverId);
//        groupName = "root." + groupName;
//        deviceName = groupName + "." + deviceName;
        DataVO dataVO = iotDBService.getDataByDevice(connection, deviceName, pageSize, pageNum, dataQueryDTO);
        return BaseVO.success("获取物理量数据成功", dataVO);
    }

    @PutMapping("/storageGroups/{groupName}/devices/{deviceName}/data")
    @ApiOperation("编辑物理量数据  (新增2.5)")
    public BaseVO updateDataByDevice(@PathVariable("serverId") Integer serverId,
                                     @PathVariable("groupName") String groupName,
                                     @PathVariable("deviceName") String deviceName,
                                     @RequestBody DataUpdateDTO dataUpdateDTO,
                                     HttpServletRequest request) throws BaseException {
        if (deviceName == null || !deviceName.matches("^[^ ]+$")) {
            throw new BaseException(ErrorCode.WRONG_DB_PARAM, ErrorCode.WRONG_DB_PARAM_MSG);
        }
        check(request, serverId);
        Connection connection = connectionService.getById(serverId);
//        groupName = "root." + groupName;
//        deviceName = groupName + "." + deviceName;
        iotDBService.updateDataByDevice(connection, deviceName, dataUpdateDTO);
        return BaseVO.success("编辑物理量数据成功", null);
    }

    @DeleteMapping("/storageGroups/{groupName}/devices/{deviceName}/data")
    @ApiOperation("删除物理量数据  (新增2.6)")
    public BaseVO deleteDataByDevice(@PathVariable("serverId") Integer serverId,
                                     @PathVariable("groupName") String groupName,
                                     @PathVariable("deviceName") String deviceName,
                                     @RequestBody DataDeleteDTO dataDeleteDTO,
                                     HttpServletRequest request) throws BaseException {
        if (deviceName == null || !deviceName.matches("^[^ ]+$")) {
            throw new BaseException(ErrorCode.WRONG_DB_PARAM, ErrorCode.WRONG_DB_PARAM_MSG);
        }
        check(request, serverId);
        Connection connection = connectionService.getById(serverId);
//        groupName = "root." + groupName;
//        deviceName = groupName + "." + deviceName;
        iotDBService.deleteDataByDevice(connection, deviceName, dataDeleteDTO);
        return BaseVO.success("删除物理量数据成功", null);
    }

    @PostMapping("/storageGroups/{groupName}/devices/{deviceName}/randomImport")
    @ApiOperation("随机批量导入指定设备下的物理量数据 (新增2.7）")
    public BaseVO randomImport(@PathVariable("serverId") Integer serverId,
                               @PathVariable("groupName") String groupName,
                               @PathVariable("deviceName") String deviceName,
                               @RequestBody RandomImportDTO randomImportDTO,
                               HttpServletRequest request) throws BaseException {
        checkPathParameter(groupName, deviceName);
        check(request, serverId);
        Connection connection = connectionService.getById(serverId);
        iotDBService.randomImport(connection, deviceName, randomImportDTO);
        return BaseVO.success("随机导入物理量数据成功", null);
    }

    @GetMapping("/users")
    @ApiOperation("获取数据库用户列表")
    public BaseVO<List<String>> getIotDBUserList(@PathVariable("serverId") Integer serverId, HttpServletRequest request) throws BaseException {
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

    @GetMapping("/roles")
    @ApiOperation("获取数据库角色列表   (未使用)")
    public BaseVO<List<String>> getIotDBRoleList(@PathVariable("serverId") Integer serverId, HttpServletRequest request) throws BaseException {
        check(request, serverId);
        Connection connection = connectionService.getById(serverId);
        List<String> roles = iotDBService.getIotDBRoleList(connection);
        return BaseVO.success("获取成功", roles);
    }

    @GetMapping("/users/{userName}")
    @ApiOperation("获取数据源用户的具体信息或其他用户的权限信息")
    public BaseVO<IotDBUserVO> getIotDBUser(@PathVariable("serverId") Integer serverId,
                                            @PathVariable("userName") String userName,
                                            HttpServletRequest request) throws BaseException {
        if (userName == null || !userName.matches("^[^ ]+$")) {
            throw new BaseException(ErrorCode.WRONG_DB_PARAM, ErrorCode.WRONG_DB_PARAM_MSG);
        }
        check(request, serverId);
        Connection connection = connectionService.getById(serverId);
        IotDBUserVO iotDBUserVO = iotDBService.getIotDBUser(connection, userName);
        return BaseVO.success("获取成功", iotDBUserVO);
    }

    @PostMapping("/users/{userName}")
    @ApiOperation("数据库用户赋权")
    public BaseVO setUserPrivileges(@PathVariable("serverId") Integer serverId,
                                    @PathVariable("userName") String userName,
                                    @RequestBody PrivilegeInfoDTO privilegeInfoDTO,
                                    HttpServletRequest request) throws BaseException {
        if (userName == null || !userName.matches("^[^ ]+$")) {
            throw new BaseException(ErrorCode.WRONG_DB_PARAM, ErrorCode.WRONG_DB_PARAM_MSG);
        }
        if (privilegeInfoDTO == null) {
            throw new BaseException(ErrorCode.WRONG_DB_PARAM, ErrorCode.WRONG_DB_PARAM_MSG);
        }
        Integer type = privilegeInfoDTO.getType();
        if (type != null && (type > 3 || type < 0)) {
            throw new BaseException(ErrorCode.WRONG_DB_PARAM, ErrorCode.WRONG_DB_PARAM_MSG);
        }
//        Integer delType = pathCheckAndGetDelType(privilegeInfoDTO);
        pathCheck(privilegeInfoDTO);
        check(request, serverId);
        Connection connection = connectionService.getById(serverId);
        iotDBService.setUserPrivileges(connection, userName, privilegeInfoDTO);
        return BaseVO.success("操作成功", null);
    }

    private void pathCheck(PrivilegeInfoDTO privilegeInfoDTO) throws BaseException {
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
//        if (delTimeseriesPaths != null && delTimeseriesPaths.size() > 0) {
//            if (delGroupPaths == null || delGroupPaths.size() != 1) {
//                throw new BaseException(ErrorCode.WRONG_DB_PARAM, ErrorCode.WRONG_DB_PARAM_MSG);
//            }
//            if (delDevicePaths == null || delDevicePaths.size() != 1) {
//                throw new BaseException(ErrorCode.WRONG_DB_PARAM, ErrorCode.WRONG_DB_PARAM_MSG);
//            }
//            return 3;
//        }
//        if (delDevicePaths != null && delDevicePaths.size() > 0) {
//            if (delGroupPaths == null || delGroupPaths.size() != 1) {
//                throw new BaseException(ErrorCode.WRONG_DB_PARAM, ErrorCode.WRONG_DB_PARAM_MSG);
//            }
//            return 2;
//        }
//        if (delGroupPaths != null && delGroupPaths.size() > 0) {
//            return 1;
//        }
//        return 0;
    }

    @PostMapping("/users/pwd")
    @ApiOperation("改变用户密码")
    public BaseVO updatePassword(@PathVariable("serverId") Integer serverId,
                                 @RequestBody IotDBUser iotDBUser,
                                 HttpServletRequest request) throws BaseException {
        check(request, serverId);
        Connection connection = connectionService.getById(serverId);
        iotDBService.updatePwd(connection, iotDBUser);
        return BaseVO.success("修改成功", null);
    }

    @DeleteMapping("/users/{userName}")
    @ApiOperation("删除数据库用户")
    public BaseVO deleteIotDBUser(@PathVariable("serverId") Integer serverId,
                                  @PathVariable("userName") String userName,
                                  HttpServletRequest request) throws BaseException {
        if (userName == null || !userName.matches("^[^ ]+$")) {
            throw new BaseException(ErrorCode.WRONG_DB_PARAM, ErrorCode.WRONG_DB_PARAM_MSG);
        }
        check(request, serverId);
        Connection connection = connectionService.getById(serverId);
        iotDBService.deleteIotDBUser(connection, userName);
        return BaseVO.success("删除成功", null);
    }

    @DeleteMapping("/roles/{roleName}")
    @ApiOperation("删除数据库角色  (未使用)")
    public BaseVO deleteIotDBRole(@PathVariable("serverId") Integer serverId,
                                  @PathVariable("roleName") String roleName,
                                  HttpServletRequest request) throws BaseException {
        if (roleName == null || roleName.matches("^[^ ]+$")) {
            throw new BaseException(ErrorCode.WRONG_DB_PARAM, ErrorCode.WRONG_DB_PARAM_MSG);
        }
        check(request, serverId);
        Connection connection = connectionService.getById(serverId);
        iotDBService.deleteIotDBRole(connection, roleName);
        return BaseVO.success("删除成功", null);
    }

    @PostMapping("/users")
    @ApiOperation("创建数据库用户")
    public BaseVO setIotDBUser(@PathVariable("serverId") Integer serverId,
                               @RequestBody IotDBUser iotDBUser,
                               HttpServletRequest request) throws BaseException {
        check(request, serverId);
        Connection connection = connectionService.getById(serverId);
        iotDBService.setIotDBUser(connection, iotDBUser);
        return BaseVO.success("创建成功", null);
    }

    @PostMapping("/roles")
    @ApiOperation("创建数据角色   (未使用)")
    public BaseVO setIotDBRole(@PathVariable("serverId") Integer serverId,
                               @RequestBody IotDBRole iotDBRole,
                               HttpServletRequest request) throws BaseException {
        check(request, serverId);
        Connection connection = connectionService.getById(serverId);
        iotDBService.setIotDBRole(connection, iotDBRole);
        return BaseVO.success("创建成功", null);
    }

    private void check(HttpServletRequest request, Integer serverId) throws BaseException {
        Integer userId = AuthenticationUtils.getUserId(request);
        connectionService.check(serverId, userId);
    }

    private void checkPathParameter(String groupName) throws BaseException {
        if (groupName == null || !groupName.matches("^[^ ]+$")) {
            throw new BaseException(ErrorCode.WRONG_DB_PARAM, ErrorCode.WRONG_DB_PARAM_MSG);
        }
    }

    private void checkPathParameter(String groupName, String deviceName) throws BaseException {
        checkPathParameter(deviceName);
        if (groupName == null || !groupName.matches("^[^ ]+$") || !deviceName.contains(groupName)) {
            throw new BaseException(ErrorCode.WRONG_DB_PARAM, ErrorCode.WRONG_DB_PARAM_MSG);
        }
    }

    private void checkPathParameter(String groupName, String deviceName, String timeseriesName) throws BaseException {
        checkPathParameter(deviceName, timeseriesName);
        checkPathParameter(groupName, deviceName);
    }

    private Long switchTime(String ttlUnit) throws BaseException {
        Long time = 0L;
        switch (ttlUnit) {
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
            return "milliSecond";
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
        return null;
    }
}
