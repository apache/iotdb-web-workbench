package org.apache.iotdb.admin.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.iotdb.admin.common.exception.BaseException;
import org.apache.iotdb.admin.common.exception.ErrorCode;
import org.apache.iotdb.admin.common.utils.AuthenticationUtils;
import org.apache.iotdb.admin.model.dto.*;
import org.apache.iotdb.admin.model.entity.Connection;
import org.apache.iotdb.admin.model.entity.Device;
import org.apache.iotdb.admin.model.entity.StorageGroup;
import org.apache.iotdb.admin.model.vo.*;
import org.apache.iotdb.admin.service.*;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.text.SimpleDateFormat;
import java.util.*;


@RestController
@Api(value = "iotdb操作相关接口")
@RequestMapping("/servers/{serverId}")
public class IotDBController<T> {

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

    static Set<String> searchGroupSet;

    static Set<String> searchDeviceSet;

    @GetMapping("/storageGroups/info")
    @ApiOperation("获得存储组信息列表")
    public BaseVO<List<GroupInfoVO>> getAllStorageGroupsInfo(@PathVariable("serverId") Integer serverId, HttpServletRequest request) throws BaseException {
        check(request, serverId);
        Connection connection = connectionService.getById(serverId);
        List<String> groupNames = iotDBService.getAllStorageGroups(connection);
        List<GroupInfoVO> groupInfoList = new ArrayList<>();
        // 3.存储组列表 增加描述、设备数量
        if (groupNames == null || groupNames.size() == 0) {
            return BaseVO.success("获取成功", groupInfoList);
        }
        List<Integer> deviceCounts = iotDBService.getDevicesCount(connection, groupNames);
        List<String> descriptions = groupService.getGroupDescription(serverId, groupNames);
        for (int i = 0; i < groupNames.size(); i++) {
            GroupInfoVO groupInfoVO = new GroupInfoVO();
            groupInfoVO.setGroupName(groupNames.get(i).replaceFirst("root.", ""));
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
        for (String groupName : groupNames) {
            StorageGroupVO storageGroupVO = new StorageGroupVO();
            Integer id = groupService.getGroupId(serverId,groupName);
            storageGroupVO.setGroupId(id);
            groupName = groupName.replaceFirst("root.","");
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
        groupDTO.setGroupName("root." + groupDTO.getGroupName());
        iotDBService.saveStorageGroup(connection, groupDTO.getGroupName());
        if (groupDTO.getGroupId() == null) {
            // 新增
            groupService.setStorageGroupInfo(connection, groupDTO);
        } else {
            // 更新
            groupService.updateStorageGroupInfo(connection,groupDTO);
        }
        if (groupDTO.getTtl() != null && groupDTO.getTtlUnit() != null) {
            if (groupDTO.getTtl() >= 0) {
                Long times = switchTime(groupDTO.getTtlUnit());
                iotDBService.saveGroupTtl(connection, groupDTO.getGroupName(), groupDTO.getTtl() * times);
            }
        }else {
            if (groupDTO.getTtl() == null && groupDTO.getTtlUnit() == null) {
                iotDBService.cancelGroupTtl(connection, groupDTO.getGroupName());
            } else {
                throw new BaseException(ErrorCode.WRONG_DB_PARAM, ErrorCode.WRONG_DB_PARAM_MSG);
            }
        }
        // 8. 存储组的编辑
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
        groupName = "root." + groupName;
        iotDBService.deleteStorageGroup(connection, groupName);
        groupService.deleteGroupInfo(serverId, groupName);
        deviceService.deleteDeviceInfo(serverId, groupName);
        measurementService.deleteMeasurementInfo(serverId, groupName);
        return BaseVO.success("删除成功", null);
    }

    // 6. 存储组详情获取 孟老师
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
        groupName = "root." + groupName;
        StorageGroup group = groupService.getGroupInfo(serverId, groupName);
        String ttl = iotDBService.getGroupTTL(connection, groupName);
        String ttlUnit;
        GroupVO groupVO = new GroupVO();
        if (ttl != null && !"null".equalsIgnoreCase(ttl)) {
            Long totalTime = Long.valueOf(ttl);
            ttlUnit = getTTL(totalTime);
            Long times = switchTime(ttlUnit);
            ttl = String.valueOf(totalTime / times);
            groupVO.setTtl(ttl);
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
        groupVO.setGroupName(groupName.replaceFirst("root.",""));
        groupVO.setAlias(connection.getAlias());
        // 描述 创建人 创建时间
        return BaseVO.success("获取成功", groupVO);
    }

    @GetMapping("/storageGroups/{groupName}/devices/info")
    @ApiOperation("获取指定存储组下的实体(设备)信息列表")
    public BaseVO<DeviceInfoVO> getDevicesInfoByGroupName(@PathVariable("serverId") Integer serverId,
                                                      @PathVariable("groupName") String groupName,
                                                      @RequestParam("pageSize") Integer pageSize,
                                                      @RequestParam("pageNum") Integer pageNum,
                                                      @RequestParam(value = "keyword",required = false) String keyword,
                                                      HttpServletRequest request) throws BaseException {
        if (groupName == null || !groupName.matches("^[^ ]+$")) {
            throw new BaseException(ErrorCode.WRONG_DB_PARAM, ErrorCode.WRONG_DB_PARAM_MSG);
        }
        if (pageSize == null || pageNum == null) {
            throw new BaseException(ErrorCode.WRONG_DB_PARAM, ErrorCode.WRONG_DB_PARAM_MSG);
        }
        check(request, serverId);
        Connection connection = connectionService.getById(serverId);
        groupName = "root." + groupName;
        CountDTO countDTO =  iotDBService.getDevicesByGroup(connection, groupName, pageSize, pageNum,keyword);
        // 7.设备列表分页
        List<String> deviceNames = countDTO.getObjects();
        DeviceInfoVO deviceInfoVO = new DeviceInfoVO();
        Integer totalPage = countDTO.getTotalPage();
        Integer totalCount = countDTO.getTotalCount();
        deviceInfoVO.setTotalCount(totalCount);
        deviceInfoVO.setTotalPage(totalPage);
        List<Integer> lines = iotDBService.getTimeseriesCount(connection, deviceNames);
        List<Device> devices = deviceService.getDevices(serverId, deviceNames);
        List<DeviceInfo> deviceInfos = new ArrayList<>();
        if (deviceNames != null) {
            for (int i = 0; i < deviceNames.size(); i++) {
                DeviceInfo deviceInfo = new DeviceInfo();
                deviceInfo.setDeviceName(deviceNames.get(i).replaceFirst(groupName+".",""));
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
        return BaseVO.success("获取成功", deviceInfoVO);
    }

    @GetMapping("/storageGroups/{groupName}/devices")
    @ApiOperation("获取指定存储组下的实体(设备)列表")
    public BaseVO<List<String>> getDevicesByGroupName(@PathVariable("serverId") Integer serverId,
                                                      @PathVariable("groupName") String groupName,
                                                      HttpServletRequest request) throws BaseException {
        if (groupName == null || !groupName.matches("^[^ ]+$")) {
            throw new BaseException(ErrorCode.WRONG_DB_PARAM, ErrorCode.WRONG_DB_PARAM_MSG);
        }
        check(request, serverId);
        Connection connection = connectionService.getById(serverId);
        groupName = "root." + groupName;
        List<String> deviceNamesStr = iotDBService.getDevices(connection, groupName);
        List<String> deviceNames = new ArrayList<>();
        for (String s : deviceNamesStr) {
            String deviceName = s.replaceFirst(groupName + ".", "");
            deviceNames.add(deviceName);
        }
        return BaseVO.success("获取成功", deviceNames);
    }

    // 9.新增设备  // 12. 编辑设备
    @PostMapping("/storageGroups/{groupName}/devices")
    @ApiOperation("新增或编辑实体(设备)")
    public BaseVO<List<String>> saveOrUpdateDevice(@PathVariable("serverId") Integer serverId,
                                                   @PathVariable("groupName") String groupName,
                                                   @RequestBody DeviceInfoDTO deviceInfoDTO,
                                                   HttpServletRequest request) throws BaseException {
        // 修改DeviceDTO传参内容 修改逻辑
        if (groupName == null || !groupName.matches("^[^ ]+$")) {
            throw new BaseException(ErrorCode.WRONG_DB_PARAM, ErrorCode.WRONG_DB_PARAM_MSG);
        }
        if (deviceInfoDTO.getDeviceDTOList() == null|| deviceInfoDTO.getDeviceDTOList().size() == 0){
            throw new BaseException(ErrorCode.WRONG_DB_PARAM, ErrorCode.WRONG_DB_PARAM_MSG);
        }
        check(request, serverId);
        Connection connection = connectionService.getById(serverId);
        groupName = "root." + groupName;
        deviceInfoDTO.setDeviceName(groupName + "." + deviceInfoDTO.getDeviceName());
        for (DeviceDTO deviceDTO : deviceInfoDTO.getDeviceDTOList()) {
            deviceDTO.setTimeseries(deviceInfoDTO.getDeviceName() + "." + deviceDTO.getTimeseries());
        }
        iotDBService.createDeviceWithMeasurements(connection, deviceInfoDTO);
        // 新增
        if (deviceInfoDTO.getDeviceId() == null) {
            deviceService.setDeviceInfo(connection, deviceInfoDTO);
            measurementService.setMeasurementsInfo(serverId, deviceInfoDTO);
        } else {
            deviceService.updateDeviceInfo(deviceInfoDTO);
            measurementService.updateMeasurementsInfo(serverId,deviceInfoDTO);
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
            throw new BaseException(ErrorCode.WRONG_DB_PARAM,ErrorCode.WRONG_DB_PARAM_MSG);
        }
        if (deviceName == null || !deviceName.matches("^[^ ]+$")) {
            throw new BaseException(ErrorCode.WRONG_DB_PARAM, ErrorCode.WRONG_DB_PARAM_MSG);
        }
        check(request, serverId);
        Connection connection = connectionService.getById(serverId);
        groupName = "root." + groupName;
        deviceName = groupName + "." + deviceName;
        iotDBService.deleteTimeseriesByDevice(connection, deviceName);
        deviceService.deleteDeviceInfoByDeviceName(serverId, deviceName);
        measurementService.deleteMeasurementInfoByDeviceName(serverId, deviceName);
        return BaseVO.success("删除成功", null);
    }

    // 10.获取设备详情
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
        groupName = "root." + groupName;
        deviceName = groupName + "." + deviceName;
        DeviceVO deviceVO = deviceService.getDevice(serverId, deviceName);
        return BaseVO.success("获取成功", deviceVO);
    }

    @GetMapping("/storageGroups/{groupName}/devices/{deviceName}/info")
    @ApiOperation("获取指定实体(设备)下的测点列表详情")
    public BaseVO<MeasuremtnInfoVO> getMeasurementsByDeviceName(@PathVariable("serverId") Integer serverId,
                                                                @PathVariable("groupName") String groupName,
                                                                @PathVariable("deviceName") String deviceName,
                                                                @RequestParam("pageSize") Integer pageSize,
                                                                @RequestParam("pageNum") Integer pageNum,
                                                                @RequestParam(value = "keyword",required = false) String keyword,
                                                                HttpServletRequest request) throws BaseException {
        if (groupName == null || !groupName.matches("^[^ ]+$")) {
            throw new BaseException(ErrorCode.WRONG_DB_PARAM, ErrorCode.WRONG_DB_PARAM_MSG);
        }
        if (deviceName == null || !deviceName.matches("^[^ ]+$")) {
            throw new BaseException(ErrorCode.WRONG_DB_PARAM, ErrorCode.WRONG_DB_PARAM_MSG);
        }
        check(request, serverId);
        Connection connection = connectionService.getById(serverId);
        groupName = "root." + groupName;
        deviceName = groupName + "." + deviceName;
        CountDTO countDTO = iotDBService.getMeasurementsByDevice(connection, deviceName, pageSize, pageNum,keyword);
        List<MeasurementDTO> measurementDTOList = countDTO.getObjects();
        List<MeasurementVO> measurementVOList = new ArrayList<>();
        if (measurementDTOList != null) {
            for (MeasurementDTO measurementDTO : measurementDTOList) {
                MeasurementVO measurementVO = new MeasurementVO();
                BeanUtils.copyProperties(measurementDTO, measurementVO);
                if (measurementVO.getTimeseries() != null) {
                    measurementVO.setTimeseries(measurementVO.getTimeseries().replaceFirst(deviceName + ".",""));
                }
                String description = measurementService.getDescription(serverId, measurementDTO.getTimeseries());
                String newValue = iotDBService.getLastMeasurementValue(connection, measurementDTO.getTimeseries());
                measurementVO.setNewValue(newValue);
                measurementVO.setDescription(description);
                measurementVOList.add(measurementVO);
            }
        }
        MeasuremtnInfoVO measuremtnInfoVO = new MeasuremtnInfoVO();
        measuremtnInfoVO.setTotalCount(countDTO.getTotalCount());
        measuremtnInfoVO.setTotalPage(countDTO.getTotalPage());
        measuremtnInfoVO.setMeasurementVOList(measurementVOList);
        // 11.测点列表加分页
        return BaseVO.success("获取成功", measuremtnInfoVO);
    }

    @GetMapping("/storageGroups/{groupName}/devices/{deviceName}/timeseries/{timeseriesName}")
    @ApiOperation("获取指定测点的最新两百条数据记录")
    public BaseVO<RecordVO> getMeasurementInfo(@PathVariable("serverId") Integer serverId,
                                                                @PathVariable("groupName") String groupName,
                                                                @PathVariable("deviceName") String deviceName,
                                                                @PathVariable("timeseriesName") String timeseriesName,
                                                                HttpServletRequest request) throws BaseException {
        if (groupName == null || !groupName.matches("^[^ ]+$")) {
            throw new BaseException(ErrorCode.WRONG_DB_PARAM, ErrorCode.WRONG_DB_PARAM_MSG);
        }
        if (deviceName == null || !deviceName.matches("^[^ ]+$")) {
            throw new BaseException(ErrorCode.WRONG_DB_PARAM, ErrorCode.WRONG_DB_PARAM_MSG);
        }
        check(request, serverId);
        Connection connection = connectionService.getById(serverId);
        groupName = "root." + groupName;
        deviceName = groupName + "." + deviceName;
        RecordVO recordVO = iotDBService.getRecords(connection,deviceName,timeseriesName);
        return BaseVO.success("获取成功", recordVO);
    }

    @PostMapping("/storageGroups/{groupName}/devices/{deviceName}/timeseries")
    @ApiOperation("创建时间序列  (未使用)")
    public BaseVO<List<String>> insertTimeseries(@PathVariable("serverId") Integer serverId,
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
    @ApiOperation("指定设备下的所有测点  (未使用)")
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
    @ApiOperation("指定设备下的测点列表")
    public BaseVO<List<String>> getTimeseries(@PathVariable("serverId") Integer serverId,
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
        List<String> timeseriesStr = iotDBService.getTimeseries(connection, deviceName);
        List<String> timeseries = new ArrayList<>();
        for (String s : timeseriesStr) {
            timeseries.add(s.replaceFirst(deviceName+".",""));
        }
        return BaseVO.success("获取成功", timeseries);
    }

    @DeleteMapping("/storageGroups/{groupName}/devices/{deviceName}/timeseries/{timeseriesName}")
    @ApiOperation("删除测点")
    public BaseVO<List<String>> deleteTimeseries(@PathVariable("serverId") Integer serverId,
                                                 @PathVariable("groupName") String groupName,
                                                 @PathVariable("deviceName") String deviceName,
                                                 @PathVariable("timeseriesName") String timeseriesName,
                                                 HttpServletRequest request) throws BaseException {
        if (timeseriesName == null || !timeseriesName.matches("^[^ ]+$")) {
            throw new BaseException(ErrorCode.WRONG_DB_PARAM, ErrorCode.WRONG_DB_PARAM_MSG);
        }
        check(request, serverId);
        Connection connection = connectionService.getById(serverId);
        groupName = "root." + groupName;
        deviceName = groupName + "." + deviceName;
        timeseriesName = deviceName + "." + timeseriesName;
        iotDBService.deleteTimeseries(connection, timeseriesName);
        measurementService.deleteMeasurementInfo(serverId, timeseriesName);
        return BaseVO.success("删除成功", null);
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
        check(request, serverId);
        Connection connection = connectionService.getById(serverId);
        iotDBService.setUserPrivileges(connection,userName,privilegeInfoDTO);
        return BaseVO.success("操作成功", null);
    }

//    @DeleteMapping("/users/{userName}")
//    @ApiOperation("数据库用户删除对应路径全部权限")
//    public BaseVO deleteUserPrivileges(@PathVariable("serverId") Integer serverId,
//                                    @PathVariable("userName") String userName,
//                                    @RequestBody PrivilegeInfoDTO privilegeInfoDTO,
//                                    HttpServletRequest request) throws BaseException {
//
//    }

    @PostMapping("/users/pwd")
    @ApiOperation("改变用户密码")
    public BaseVO updatePassword(@PathVariable("serverId") Integer serverId,
                                  @RequestBody IotDBUser iotDBUser,
                                  HttpServletRequest request) throws BaseException {
        check(request, serverId);
        Connection connection = connectionService.getById(serverId);
        iotDBService.updatePwd(connection, iotDBUser);
        return BaseVO.success("删除成功", null);
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
        // 2.用户增加修改去掉角色 勾
        return BaseVO.success("创建成功", null);
    }

    @PostMapping("/roles")
    @ApiOperation("创建数据角色   (未使用)")
    public BaseVO setIotDBUser(@PathVariable("serverId") Integer serverId,
                               @RequestBody IotDBRole iotDBRole,
                               HttpServletRequest request) throws BaseException {
        check(request, serverId);
        Connection connection = connectionService.getById(serverId);
        iotDBService.setIotDBRole(connection, iotDBRole);
        return BaseVO.success("创建成功", null);
    }


//    @Scheduled(fixedRate = 1000 * 60 * 10)
//    public void update(){
//        for (String s : searchGroupSet) {
//            String[] ip_groupPath = s.split(":");
//            String ip = ip_groupPath[0];
//            String groupPath = ip_groupPath[1];
//        }
//    }

    private void check(HttpServletRequest request, Integer serverId) throws BaseException {
        Integer userId = AuthenticationUtils.getUserId(request);
        connectionService.check(serverId, userId);
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
        if (time == 0) {
            return "milliSecond";
        }
        if (time / 12 * 30 * 24 * 60 * 60 * 1000 != 0) {
            return "year";
        }
        if (time / 30 * 24 * 60 * 60 * 1000 != 0) {
            return "month";
        }
        if (time / 7 * 24 * 60 * 60 * 1000 != 0) {
            return "week";
        }
        if (time / 24 * 60 * 60 * 1000 != 0) {
            return "day";
        }
        if (time / 60 * 60 * 1000 != 0) {
            return "hour";
        }
        if (time / 60 * 1000 != 0) {
            return "minute";
        }
        if (time / 1000 != 0) {
            return "second";
        }
        return null;
    }
}
