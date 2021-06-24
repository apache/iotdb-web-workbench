package org.apache.iotdb.admin.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.iotdb.admin.common.exception.BaseException;
import org.apache.iotdb.admin.common.exception.ErrorCode;
import org.apache.iotdb.admin.common.utils.AuthenticationUtils;
import org.apache.iotdb.admin.model.dto.DeviceDTO;
import org.apache.iotdb.admin.model.dto.GroupDTO;
import org.apache.iotdb.admin.model.dto.IotDBUser;
import org.apache.iotdb.admin.model.dto.Timeseries;
import org.apache.iotdb.admin.model.entity.Connection;
import org.apache.iotdb.admin.model.entity.Device;
import org.apache.iotdb.admin.model.vo.*;
import org.apache.iotdb.admin.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;


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

    @GetMapping("/storageGroups")
    @ApiOperation("获得存储组列表")
    public BaseVO<GroupInfoVO> getAllStorageGroups(@PathVariable("serverId") Integer serverId,HttpServletRequest request) throws BaseException {
        check(request,serverId);
        Connection connection = connectionService.getById(serverId);
        GroupInfoVO groupInfoVO = new GroupInfoVO();
        List<String> groupNames = iotDBService.getAllStorageGroups(connection);
        // 3.存储组列表 增加描述、设备数量
        if (groupNames == null || groupNames.size() == 0) {
            return BaseVO.success("获取成功", groupInfoVO);
        }
        List<Integer> deviceCounts = iotDBService.getDevicesCount(connection,groupNames);
        List<String> descriptions = groupService.getGroupDescription(serverId,groupNames);
        groupInfoVO.setGroupNames(groupNames);
        groupInfoVO.setDeviceCounts(deviceCounts);
        groupInfoVO.setDescriptions(descriptions);
        return BaseVO.success("获取成功", groupInfoVO);
    }

    @PostMapping("/storageGroups")
    @ApiOperation("新增或修改存储组")
    public BaseVO saveStorageGroup(@PathVariable("serverId") Integer serverId,
                                   @RequestBody GroupDTO groupDTO,
                                   HttpServletRequest request) throws BaseException {
        check(request,serverId);
        Connection connection = connectionService.getById(serverId);
        boolean flag = groupService.isExist(serverId,groupDTO.getGroupName());
        if (!flag) {
            iotDBService.saveStorageGroup(connection, groupDTO.getGroupName());
        }
        // 5. 新增存储组时添加描述和ttl
        if (groupDTO.getTtl() != null  && groupDTO.getTtlUnit() != null) {
            if (groupDTO.getTtl() >= 0) {
                Long times = switchTime(groupDTO.getTtlUnit());
                iotDBService.saveGroupTtl(connection,groupDTO.getGroupName(),groupDTO.getTtl() * times);
            }else {
                throw new BaseException(ErrorCode.WRONG_DB_PARAM,ErrorCode.WRONG_DB_PARAM_MSG);
            }
        }else {
            if (flag) {
                iotDBService.cancelGroupTtl(connection,groupDTO.getGroupName());
            }
        }
        groupService.setStorageGroupInfo(connection,groupDTO);
        // 8. 存储组的编辑
        return BaseVO.success( "新增或更新成功", null);
    }

    @DeleteMapping("/storageGroups")
    @ApiOperation("删除存储组")
    public BaseVO deleteStorageGroup(@PathVariable("serverId") Integer serverId,
                                     @RequestParam("groupName") String groupName,
                                     HttpServletRequest request) throws BaseException {
        if (groupName == null || !groupName.matches("^[^ ]+$")) {
            throw new BaseException(ErrorCode.WRONG_DB_PARAM,ErrorCode.WRONG_DB_PARAM_MSG);
        }
        check(request,serverId);
        Connection connection = connectionService.getById(serverId);
        iotDBService.deleteStorageGroup(connection, groupName);
        groupService.deleteGroupInfo(serverId,groupName);
        deviceService.deleteDeviceInfo(serverId,groupName);
        measurementService.deleteMeasurementInfo(serverId,groupName);
        return BaseVO.success("删除成功", null);
    }

    // 6. 存储组详情获取 孟老师
    @GetMapping("/storageGroups/{groupName}")
    @ApiOperation("存储组详情获取")
    public BaseVO getStorageGroup(@PathVariable("serverId") Integer serverId,
                                   @PathVariable("groupName") String groupName,
                                   HttpServletRequest request) throws BaseException {
        if (groupName == null || !groupName.matches("^[^ ]+$")) {
            throw new BaseException(ErrorCode.WRONG_DB_PARAM,ErrorCode.WRONG_DB_PARAM_MSG);
        }
        check(request,serverId);
        // 描述 创建人 创建时间

        return BaseVO.success("获取成功", null);
    }


    @GetMapping("/storageGroups/{groupName}/devices")
    @ApiOperation("获取指定存储组下的设备列表")
    public BaseVO<DeviceInfoVO> getDevicesByGroupName(@PathVariable("serverId") Integer serverId,
                                                      @PathVariable("groupName") String groupName,
                                                      @RequestParam("pageSize") Integer pageSize,
                                                      @RequestParam("pageNum") Integer pageNum,
                                                      HttpServletRequest request) throws BaseException {
        if (groupName == null || !groupName.matches("^[^ ]+$")) {
            throw new BaseException(ErrorCode.WRONG_DB_PARAM,ErrorCode.WRONG_DB_PARAM_MSG);
        }
        if (pageSize == null || pageNum == null) {
            throw new BaseException(ErrorCode.WRONG_DB_PARAM,ErrorCode.WRONG_DB_PARAM_MSG);
        }
        check(request,serverId);
        Connection connection = connectionService.getById(serverId);
        List<String> deviceNames = iotDBService.getDevicesByGroup(connection, groupName,pageSize,pageNum);
        // 7.设备列表分页
        Integer count = iotDBService.getDeviceCount(connection,groupName);
        List<Integer> lines = iotDBService.getTimeseriesCount(connection,deviceNames);
        List<Device> devices = deviceService.getDevices(serverId,deviceNames);
        DeviceInfoVO deviceInfoVO = new DeviceInfoVO();
        deviceInfoVO.setTotalCount(count);
        Integer totalPage = count % pageSize == 0 ? count / pageSize : count / pageSize + 1;
        deviceInfoVO.setTotalPage(totalPage);
        deviceInfoVO.setDeviceNames(deviceNames);
        deviceInfoVO.setLines(lines);
        List<String> descriptions = new ArrayList<>();
        List<String> creators = new ArrayList<>();
        for (Device device : devices) {
            if (device != null) {
                descriptions.add(device.getDescription());
                creators.add(device.getCreator());
            }
            descriptions.add(null);
            creators.add(null);
        }
        return BaseVO.success("获取成功", deviceInfoVO);
    }

    // 9.新增设备  // 12. 编辑设备
    @PostMapping("/storageGroups/{groupName}/devices")
    @ApiOperation("新增或编辑设备")
    public BaseVO<List<String>> getDevicesByGroupName(@PathVariable("serverId") Integer serverId,
                                                      @PathVariable("groupName") String groupName,
                                                      @RequestBody DeviceDTO deviceDTO,
                                                      HttpServletRequest request) throws BaseException {
        if (groupName == null || !groupName.matches("^[^ ]+$")) {
            throw new BaseException(ErrorCode.WRONG_DB_PARAM,ErrorCode.WRONG_DB_PARAM_MSG);
        }
        if (deviceDTO.getEncoding() == null || deviceDTO.getMeasurements() == null || deviceDTO.getMeasurements() == null || deviceDTO.getDescriptions() == null) {
            throw new BaseException(ErrorCode.WRONG_DB_PARAM,ErrorCode.WRONG_DB_PARAM_MSG);
        }
        if (deviceDTO.getEncoding().size() == deviceDTO.getMeasurements().size()  && deviceDTO.getMeasurements().size() == deviceDTO.getTypes().size() && deviceDTO.getTypes().size() ==deviceDTO.getDescriptions().size()) {
            check(request,serverId);
            Connection connection = connectionService.getById(serverId);
            iotDBService.createDeviceWithMeasurements(connection,deviceDTO);
            deviceService.setDeviceInfo(connection,deviceDTO);
            measurementService.setMeasurementsInfo(serverId,deviceDTO);
            //新增设备时 添加描述
            return BaseVO.success("新增成功", null);
        }else {
            throw new BaseException(ErrorCode.WRONG_DB_PARAM,ErrorCode.WRONG_DB_PARAM_MSG);
        }
    }


    @DeleteMapping("/storageGroups/{groupName}/devices/{deviceName}")
    @ApiOperation("删除设备")
    public BaseVO deleteDevice(@PathVariable("serverId") Integer serverId,
                                                      @PathVariable("groupName") String groupName,
                                                      @PathVariable("deviceName") String deviceName,
                                                      HttpServletRequest request) throws BaseException {
//        if (groupName == null || !groupName.matches("^[^ ]+$")) {
//            throw new BaseException(ErrorCode.WRONG_DB_PARAM,ErrorCode.WRONG_DB_PARAM_MSG);
//        }
        if (deviceName == null || !deviceName.matches("^[^ ]+$")) {
            throw new BaseException(ErrorCode.WRONG_DB_PARAM,ErrorCode.WRONG_DB_PARAM_MSG);
        }
        check(request,serverId);
        Connection connection = connectionService.getById(serverId);
        iotDBService.deleteTimeseriesByDevice(connection,deviceName);
        deviceService.deleteDeviceInfoByDeviceName(serverId,deviceName);
        measurementService.deleteMeasurementInfoByDeviceName(serverId,deviceName);
        return BaseVO.success("删除成功", null);
    }

    // 10.获取设备详情
    @GetMapping("/storageGroups/{groupName}/devices/{deviceName}")
    @ApiOperation("获取设备")
    public BaseVO<DeviceVO> getDeviceInfo(@PathVariable("serverId") Integer serverId,
                                                      @PathVariable("groupName") String groupName,
                                                      @PathVariable("deviceName") String deviceName,
                                                      HttpServletRequest request) throws BaseException {
        if (deviceName == null || !deviceName.matches("^[^ ]+$")) {
            throw new BaseException(ErrorCode.WRONG_DB_PARAM,ErrorCode.WRONG_DB_PARAM_MSG);
        }
        check(request,serverId);
        DeviceVO deviceVO = deviceService.getDevice(serverId,deviceName);
        return BaseVO.success("获取成功", deviceVO);
    }

    @GetMapping("/devices/{deviceName}")
    @ApiOperation("获取指定设备下的测点列表")
    public BaseVO<List<String>> getMeasurementsByDeviceName(@PathVariable("serverId") Integer serverId,
                                                            @PathVariable("deviceName") String deviceName,
                                                            HttpServletRequest request) throws BaseException {
        if (deviceName == null || !deviceName.matches("^[^ ]+$")) {
            throw new BaseException(ErrorCode.WRONG_DB_PARAM,ErrorCode.WRONG_DB_PARAM_MSG);
        }
        check(request,serverId);
        Connection connection = connectionService.getById(serverId);
        List<String> measurements = iotDBService.getMeasurementsByDevice(connection, deviceName);
        // 11.测点列表加分页
        return BaseVO.success( "获取成功", measurements);
    }

    @PostMapping("/devices/{deviceName}/timeseries")
    @ApiOperation("创建时间序列")
    public BaseVO<List<String>> insertTimeseries(@PathVariable("serverId") Integer serverId,
                                                 @PathVariable("deviceName") String deviceName,
                                                 @RequestBody Timeseries timeseries,
                                                 HttpServletRequest request) throws BaseException {
        if (deviceName == null || !deviceName.matches("^[^ ]+$")) {
            throw new BaseException(ErrorCode.WRONG_DB_PARAM,ErrorCode.WRONG_DB_PARAM_MSG);
        }
        check(request,serverId);
        Connection connection = connectionService.getById(serverId);
        iotDBService.insertTimeseries(connection, deviceName,timeseries);
        return BaseVO.success( "创建成功", null);
    }

    @GetMapping("/devices/{deviceName}/timeseries")
    @ApiOperation("指定设备下的所有时间序列")
    public BaseVO<SqlResultVO> showTimeseries(@PathVariable("serverId") Integer serverId,
                                              @PathVariable("deviceName") String deviceName,
                                              HttpServletRequest request) throws BaseException {
        if (deviceName == null || !deviceName.matches("^[^ ]+$")) {
            throw new BaseException(ErrorCode.WRONG_DB_PARAM,ErrorCode.WRONG_DB_PARAM_MSG);
        }
        check(request,serverId);
        Connection connection = connectionService.getById(serverId);
        SqlResultVO resultVO = iotDBService.showTimeseries(connection, deviceName);
        return BaseVO.success("获取成功", resultVO);
    }

    @DeleteMapping("/timeseries/{timeseriesName}")
    @ApiOperation("删除时间序列")
    public BaseVO<List<String>> deleteTimeseries(@PathVariable("serverId") Integer serverId,
                                                 @PathVariable("timeseriesName") String timeseriesName,
                                                 HttpServletRequest request) throws BaseException {
        if (timeseriesName == null || !timeseriesName.matches("^[^ ]+$")) {
            throw new BaseException(ErrorCode.WRONG_DB_PARAM,ErrorCode.WRONG_DB_PARAM_MSG);
        }
        check(request,serverId);
        Connection connection = connectionService.getById(serverId);
        iotDBService.deleteTimeseries(connection, timeseriesName);
        return BaseVO.success("删除成功", null);
    }

    @GetMapping("/users")
    @ApiOperation("获取数据库用户列表")
    public BaseVO<List<String>> getIotDBUserList(@PathVariable("serverId") Integer serverId, HttpServletRequest request) throws BaseException {
        check(request,serverId);
        Connection connection = connectionService.getById(serverId);
        List<String> users = iotDBService.getIotDBUserList(connection);
        return BaseVO.success("获取成功", users);
    }

//    @GetMapping("/roles")
//    @ApiOperation("获取数据库角色列表")
//    public BaseVO<List<String>> getIotDBRoleList(@PathVariable("serverId") Integer serverId,HttpServletRequest request) throws BaseException {
//        check(request,serverId);
//        Connection connection = connectionService.getById(serverId);
//        List<String> roles = iotDBService.getIotDBRoleList(connection);
//        return BaseVO.success("获取成功", roles);
//    }

    @GetMapping("/users/{userName}")
    @ApiOperation("获取数据库用户的具体信息")
    public BaseVO<IotDBUserVO> getIotDBUser(@PathVariable("serverId") Integer serverId,
                                            @PathVariable("userName") String userName,
                                            HttpServletRequest request) throws BaseException {
        if (userName == null || !userName.matches("^[^ ]+$")) {
            throw new BaseException(ErrorCode.WRONG_DB_PARAM,ErrorCode.WRONG_DB_PARAM_MSG);
        }
        check(request,serverId);
        Connection connection = connectionService.getById(serverId);
        IotDBUserVO iotDBUserVO = iotDBService.getIotDBUser(connection, userName);
        return BaseVO.success("获取成功", iotDBUserVO);
    }


    @DeleteMapping("/users/{userName}")
    @ApiOperation("删除数据库用户")
    public BaseVO deleteIotDBUser(@PathVariable("serverId") Integer serverId,
                                  @PathVariable("userName") String userName,
                                  HttpServletRequest request) throws BaseException {
        if (userName == null || !userName.matches("^[^ ]+$")) {
            throw new BaseException(ErrorCode.WRONG_DB_PARAM,ErrorCode.WRONG_DB_PARAM_MSG);
        }
        check(request,serverId);
        Connection connection = connectionService.getById(serverId);
        iotDBService.deleteIotDBUser(connection, userName);
        return BaseVO.success( "删除成功", null);
    }

//    @DeleteMapping("/roles/{roleName}")
//    @ApiOperation("删除数据库角色")
//    public BaseVO deleteIotDBRole(@PathVariable("serverId") Integer serverId,
//                                  @PathVariable("roleName") String roleName,
//                                  HttpServletRequest request) throws BaseException {
//        if(roleName == null || roleName.matches("^[^ ]+$")){
//            throw new BaseException(ErrorCode.WRONG_DB_PARAM,ErrorCode.WRONG_DB_PARAM_MSG);
//        }
//        check(request,serverId);
//        Connection connection = connectionService.getById(serverId);
//        iotDBService.deleteIotDBRole(connection, roleName);
//        return BaseVO.success( "删除成功", null);
//    }

    @PostMapping("/users")
    @ApiOperation("创建数据库用户")
    public BaseVO setIotDBUser(@PathVariable("serverId") Integer serverId,
                               @RequestBody IotDBUser iotDBUser,
                               HttpServletRequest request) throws BaseException {
        check(request,serverId);
        Connection connection = connectionService.getById(serverId);
        iotDBService.setIotDBUser(connection, iotDBUser);
        // 2.用户增加修改去掉角色 勾
        return BaseVO.success("创建成功", null);
    }

//    @PostMapping("/roles")
//    @ApiOperation("创建数据角色")
//    public BaseVO setIotDBUser(@PathVariable("serverId") Integer serverId,
//                               @RequestBody IotDBRole iotDBRole,
//                               HttpServletRequest request) throws BaseException {
//        check(request,serverId);
//        Connection connection = connectionService.getById(serverId);
//        iotDBService.setIotDBRole(connection, iotDBRole);
//        return BaseVO.success( "创建成功", null);
//    }


    @PostMapping("/query")
    @ApiOperation("用于查询器查询")
    public BaseVO<SqlResultVO> query(@PathVariable("serverId") Integer serverId,
                                     @RequestParam("sql") String sql,
                                     HttpServletRequest request) throws BaseException {
        if (sql == null) {
            throw new BaseException(ErrorCode.WRONG_DB_PARAM,ErrorCode.WRONG_DB_PARAM_MSG);
        }
        check(request,serverId);
        Connection connection = connectionService.getById(serverId);
        SqlResultVO sqlResultVO = iotDBService.query(connection, sql);
        return BaseVO.success("查询成功",sqlResultVO);
    }

    private void check(HttpServletRequest request, Integer serverId) throws BaseException {
        Integer userId = AuthenticationUtils.getUserId(request);
        connectionService.check(serverId,userId);
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
                throw new BaseException(ErrorCode.WRONG_DB_PARAM,ErrorCode.WRONG_DB_PARAM_MSG);
        }
        return time;
    }
}
