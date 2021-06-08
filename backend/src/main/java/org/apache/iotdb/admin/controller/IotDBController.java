package org.apache.iotdb.admin.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.iotdb.admin.common.exception.BaseException;
import org.apache.iotdb.admin.common.exception.ErrorCode;
import org.apache.iotdb.admin.common.utils.AuthenticationUtils;
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

import javax.servlet.http.HttpServletRequest;
import java.util.List;


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
    public BaseVO<List<String>> getAllStorageGroups(@PathVariable("serverId") Integer serverId,HttpServletRequest request) throws BaseException {
        check(request,serverId);
        Connection connection = connectionService.getById(serverId);
        List<String> groupList = iotDBService.getAllStorageGroups(connection);
        return BaseVO.success("获取成功", groupList);
    }

    @PostMapping("/storageGroups")
    @ApiOperation("新增存储组")
    public BaseVO saveStorageGroup(@PathVariable("serverId") Integer serverId, 
                                   @RequestParam("groupName") String groupName,
                                   HttpServletRequest request) throws BaseException {
        if(groupName == null || groupName.matches("^[^ ]+$")){
            throw new BaseException(ErrorCode.WRONG_DB_PARAM,ErrorCode.WRONG_DB_PARAM_MSG);
        }
        check(request,serverId);
        Connection connection = connectionService.getById(serverId);
        iotDBService.saveStorageGroup(connection, groupName);
        return BaseVO.success( "新增成功", null);
    }

    @DeleteMapping("/storageGroups")
    @ApiOperation("删除存储组")
    public BaseVO deleteStorageGroup(@PathVariable("serverId") Integer serverId,
                                     @RequestParam("groupName") String groupName,
                                     HttpServletRequest request) throws BaseException {
        if(groupName == null || groupName.matches("^[^ ]+$")){
            throw new BaseException(ErrorCode.WRONG_DB_PARAM,ErrorCode.WRONG_DB_PARAM_MSG);
        }
        check(request,serverId);
        Connection connection = connectionService.getById(serverId);
        iotDBService.deleteStorageGroup(connection, groupName);
        return BaseVO.success("删除成功", null);
    }

    @GetMapping("/storageGroups/{groupName}/devices")
    @ApiOperation("获取指定存储组下的设备列表")
    public BaseVO<List<String>> getDevicesByGroupName(@PathVariable("serverId") Integer serverId,
                                                      @PathVariable("groupName") String groupName,
                                                      HttpServletRequest request) throws BaseException {
        if(groupName == null || groupName.matches("^[^ ]+$")){
            throw new BaseException(ErrorCode.WRONG_DB_PARAM,ErrorCode.WRONG_DB_PARAM_MSG);
        }
        check(request,serverId);
        Connection connection = connectionService.getById(serverId);
        List<String> devices = iotDBService.getDevicesByGroup(connection, groupName);
        return BaseVO.success("获取成功", devices);
    }

    @GetMapping("/devices/{deviceName}")
    @ApiOperation("获取指定设备下的测点列表")
    public BaseVO<List<String>> getMeasurementsByDeviceName(@PathVariable("serverId") Integer serverId, 
                                                            @PathVariable("deviceName") String deviceName,
                                                            HttpServletRequest request) throws BaseException {
        if(deviceName == null || deviceName.matches("^[^ ]+$")){
            throw new BaseException(ErrorCode.WRONG_DB_PARAM,ErrorCode.WRONG_DB_PARAM_MSG);
        }
        check(request,serverId);
        Connection connection = connectionService.getById(serverId);
        List<String> measurements = iotDBService.getMeasurementsByDevice(connection, deviceName);
        return BaseVO.success( "获取成功", measurements);
    }

    @PostMapping("/devices/{deviceName}")
    @ApiOperation("创建时间序列")
    public BaseVO<List<String>> insertTimeseries(@PathVariable("serverId") Integer serverId,
                                                 @PathVariable("deviceName") String deviceName,
                                                 @RequestBody Timeseries timeseries,
                                                 HttpServletRequest request) throws BaseException {
        if(deviceName == null || deviceName.matches("^[^ ]+$")){
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
        if(deviceName == null || deviceName.matches("^[^ ]+$")){
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
        if(timeseriesName == null || timeseriesName.matches("^[^ ]+$")){
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

    @GetMapping("/roles")
    @ApiOperation("获取数据库角色列表")
    public BaseVO<List<String>> getIotDBRoleList(@PathVariable("serverId") Integer serverId,HttpServletRequest request) throws BaseException {
        check(request,serverId);
        Connection connection = connectionService.getById(serverId);
        List<String> roles = iotDBService.getIotDBRoleList(connection);
        return BaseVO.success("获取成功", roles);
    }

    @GetMapping("/users/{userName}")
    @ApiOperation("获取数据库用户的具体信息")
    public BaseVO<IotDBUserVO> getIotDBUser(@PathVariable("serverId") Integer serverId,
                                            @PathVariable("userName") String userName,
                                            HttpServletRequest request) throws BaseException {
        if(userName == null || userName.matches("^[^ ]+$")){
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
        if(userName == null || userName.matches("^[^ ]+$")){
            throw new BaseException(ErrorCode.WRONG_DB_PARAM,ErrorCode.WRONG_DB_PARAM_MSG);
        }
        check(request,serverId);
        Connection connection = connectionService.getById(serverId);
        iotDBService.deleteIotDBUser(connection, userName);
        return BaseVO.success( "删除成功", null);
    }

    @DeleteMapping("/roles/{roleName}")
    @ApiOperation("删除数据库角色")
    public BaseVO deleteIotDBRole(@PathVariable("serverId") Integer serverId,
                                  @PathVariable("roleName") String roleName,
                                  HttpServletRequest request) throws BaseException {
        if(roleName == null || roleName.matches("^[^ ]+$")){
            throw new BaseException(ErrorCode.WRONG_DB_PARAM,ErrorCode.WRONG_DB_PARAM_MSG);
        }
        check(request,serverId);
        Connection connection = connectionService.getById(serverId);
        iotDBService.deleteIotDBRole(connection, roleName);
        return BaseVO.success( "删除成功", null);
    }

    @PostMapping("/users")
    @ApiOperation("创建数据库用户")
    public BaseVO setIotDBUser(@PathVariable("serverId") Integer serverId,
                               @RequestBody IotDBUser iotDBUser,
                               HttpServletRequest request) throws BaseException {
        check(request,serverId);
        Connection connection = connectionService.getById(serverId);
        iotDBService.setIotDBUser(connection, iotDBUser);
        return BaseVO.success("创建成功", null);
    }

    @PostMapping("/roles")
    @ApiOperation("创建数据角色")
    public BaseVO setIotDBUser(@PathVariable("serverId") Integer serverId,
                               @RequestBody IotDBRole iotDBRole,
                               HttpServletRequest request) throws BaseException {
        check(request,serverId);
        Connection connection = connectionService.getById(serverId);
        iotDBService.setIotDBRole(connection, iotDBRole);
        return BaseVO.success( "创建成功", null);
    }


    @PostMapping("/query")
    @ApiOperation("用于查询器查询")
    public BaseVO<SqlResultVO> query(@PathVariable("serverId") Integer serverId,
                                     @RequestParam("sql") String sql,
                                     HttpServletRequest request) throws BaseException {
        if(sql == null){
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
}
