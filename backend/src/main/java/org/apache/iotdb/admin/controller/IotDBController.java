package org.apache.iotdb.admin.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.iotdb.admin.common.exception.BaseException;
import org.apache.iotdb.admin.model.dto.IotDBRole;
import org.apache.iotdb.admin.model.dto.IotDBUser;
import org.apache.iotdb.admin.model.dto.Timeseries;
import org.apache.iotdb.admin.model.entity.Connection;
import org.apache.iotdb.admin.model.vo.BaseVO;
import org.apache.iotdb.admin.model.vo.IotDBUserVO;
import org.apache.iotdb.admin.model.vo.SqlResultVO;
import org.apache.iotdb.admin.service.ConnectionService;
import org.apache.iotdb.admin.service.IotDBService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @anthor fyx 2021/5/27
 */

@RestController
@Api(value = "iotdb操作相关接口")
@RequestMapping("/servers/{serverId}")
public class IotDBController<T> {

    @Autowired
    private ConnectionService connectionService;

    @Autowired
    private IotDBService iotDBService;

    @GetMapping("/storageGroups")
    @ApiOperation("获得存储组列表")
    public BaseVO<List<String>> getAllStorageGroups(@PathVariable("serverId") Integer serverId) throws BaseException {
        Connection connection = connectionService.getById(serverId);
        List<String> groupList = iotDBService.getAllStorageGroups(connection);
        return new BaseVO(0, "获取成功", groupList);
    }

    @PostMapping("/storageGroups")
    @ApiOperation("新增存储组")
    public BaseVO saveStorageGroup(@PathVariable("serverId") Integer serverId, @RequestParam("groupName") String groupName) throws BaseException {
        Connection connection = connectionService.getById(serverId);
        iotDBService.saveStorageGroup(connection, groupName);
        return new BaseVO(0, "新增成功", null);
    }

    @DeleteMapping("/storageGroups")
    @ApiOperation("删除存储组")
    public BaseVO deleteStorageGroup(@PathVariable("serverId") Integer serverId, @RequestParam("groupName") String groupName) throws BaseException {
        Connection connection = connectionService.getById(serverId);
        iotDBService.deleteStorageGroup(connection, groupName);
        return new BaseVO(0, "删除成功", null);
    }

    @GetMapping("/storageGroups/{groupName}/devices")
    @ApiOperation("获取指定存储组下的设备列表")
    public BaseVO<List<String>> getDevicesByGroupName(@PathVariable("serverId") Integer serverId, @PathVariable("groupName") String groupName) throws BaseException {
        Connection connection = connectionService.getById(serverId);
        List<String> devices = iotDBService.getDevicesByGroup(connection, groupName);
        return new BaseVO<>(0, "获取成功", devices);
    }

    @GetMapping("/devices/{deviceName}")
    @ApiOperation("获取指定设备下的测点列表")
    public BaseVO<List<String>> getMeasurementsByDeviceName(@PathVariable("serverId") Integer serverId, @PathVariable("deviceName") String deviceName) throws BaseException {
        Connection connection = connectionService.getById(serverId);
        List<String> measurements = iotDBService.getMeasurementsByDevice(connection, deviceName);
        return new BaseVO<>(0, "获取成功", measurements);
    }

    @PostMapping("/devices/{deviceName}")
    @ApiOperation("创建时间序列")
    public BaseVO<List<String>> insertTimeseries(@PathVariable("serverId") Integer serverId,
                                                 @PathVariable("deviceName") String deviceName,
                                                 @RequestBody Timeseries timeseries) throws BaseException {
        Connection connection = connectionService.getById(serverId);
        iotDBService.insertTimeseries(connection, deviceName,timeseries);
        return new BaseVO<>(0, "创建成功", null);
    }

    @GetMapping("/devices/{deviceName}/timeseries")
    @ApiOperation("指定设备下的所有时间序列")
    public BaseVO<SqlResultVO> showTimeseries(@PathVariable("serverId") Integer serverId, @PathVariable("deviceName") String deviceName) throws BaseException {
        Connection connection = connectionService.getById(serverId);
        SqlResultVO resultVO = iotDBService.showTimeseries(connection, deviceName);
        return new BaseVO<>(0, "获取成功", resultVO);
    }

    @DeleteMapping("/timeseries/{timeseriesName}")
    @ApiOperation("删除时间序列")
    public BaseVO<List<String>> deleteTimeseries(@PathVariable("serverId") Integer serverId, @PathVariable("timeseriesName") String timeseriesName) throws BaseException {
        Connection connection = connectionService.getById(serverId);
        iotDBService.deleteTimeseries(connection, timeseriesName);
        return new BaseVO<>(0, "删除成功", null);
    }


    @GetMapping("/users")
    @ApiOperation("获取数据库用户列表")
    public BaseVO<List<String>> getIotDBUserList(@PathVariable("serverId") Integer serverId) throws BaseException {
        Connection connection = connectionService.getById(serverId);
        List<String> users = iotDBService.getIotDBUserList(connection);
        return new BaseVO<>(0, "获取成功", users);
    }

    @GetMapping("/roles")
    @ApiOperation("获取数据库角色列表")
    public BaseVO<List<String>> getIotDBRoleList(@PathVariable("serverId") Integer serverId) throws BaseException {
        Connection connection = connectionService.getById(serverId);
        List<String> roles = iotDBService.getIotDBRoleList(connection);
        return new BaseVO<>(0, "获取成功", roles);
    }

    @GetMapping("/users/{userName}")
    @ApiOperation("获取数据库用户的具体信息")
    public BaseVO<IotDBUserVO> getIotDBUser(@PathVariable("serverId") Integer serverId, @PathVariable("userName") String userName) throws BaseException {
        Connection connection = connectionService.getById(serverId);
        IotDBUserVO iotDBUserVO = iotDBService.getIotDBUser(connection, userName);
        return new BaseVO<>(0, "获取成功", iotDBUserVO);
    }


    @DeleteMapping("/users/{userName}")
    @ApiOperation("删除数据库用户")
    public BaseVO deleteIotDBUser(@PathVariable("serverId") Integer serverId, @PathVariable("userName") String userName) throws BaseException {
        Connection connection = connectionService.getById(serverId);
        iotDBService.deleteIotDBUser(connection, userName);
        return new BaseVO<>(0, "删除成功", null);
    }

    @DeleteMapping("/roles/{roleName}")
    @ApiOperation("删除数据库角色")
    public BaseVO deleteIotDBRole(@PathVariable("serverId") Integer serverId, @PathVariable("roleName") String roleName) throws BaseException {
        Connection connection = connectionService.getById(serverId);
        iotDBService.deleteIotDBRole(connection, roleName);
        return new BaseVO<>(0, "删除成功", null);
    }

    @PostMapping("/users")
    @ApiOperation("创建数据库用户")
    public BaseVO setIotDBUser(@PathVariable("serverId") Integer serverId, @RequestBody IotDBUser iotDBUser) throws BaseException {
        Connection connection = connectionService.getById(serverId);
        iotDBService.setIotDBUser(connection, iotDBUser);
        return new BaseVO<>(0, "创建成功", null);
    }

    @PostMapping("/roles")
    @ApiOperation("创建数据角色")
    public BaseVO setIotDBUser(@PathVariable("serverId") Integer serverId, @RequestBody IotDBRole iotDBRole) throws BaseException {
        Connection connection = connectionService.getById(serverId);
        iotDBService.setIotDBRole(connection, iotDBRole);
        return new BaseVO<>(0, "创建成功", null);
    }


    @PostMapping("/query")
    public BaseVO<SqlResultVO> query(@PathVariable("serverId") Integer serverId,@RequestParam("sql") String sql) throws BaseException {
        Connection connection = connectionService.getById(serverId);
        SqlResultVO sqlResultVO = iotDBService.query(connection, sql);
        return new BaseVO<>(0,"查询成功",sqlResultVO);
    }

}
