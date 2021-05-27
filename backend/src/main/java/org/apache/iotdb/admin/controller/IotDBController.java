package org.apache.iotdb.admin.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.ibatis.annotations.Delete;
import org.apache.iotdb.admin.model.entity.Connection;
import org.apache.iotdb.admin.model.vo.BaseVO;
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
public class IotDBController {

    @Autowired
    private ConnectionService connectionService;

    @Autowired
    private IotDBService iotDBService;

    @GetMapping("/storageGroups")
    @ApiOperation("获得存储组列表")
    public BaseVO<List<String>> getAllStorageGroups(@PathVariable("serverId") Integer serverId) {
        Connection connection = connectionService.getById(serverId);
        List<String> groupList = iotDBService.getAllStorageGroups(connection);
        return new BaseVO(200, "成功", groupList);
    }

    @PostMapping("/storageGroups")
    @ApiOperation("新增存储组")
    public BaseVO saveStorageGroup(@PathVariable("serverId") Integer serverId, @RequestParam("groupName") String groupName) {
        Connection connection = connectionService.getById(serverId);
        iotDBService.saveStorageGroup(connection, groupName);
        return new BaseVO(200, "成功", null);
    }

    @DeleteMapping("/storageGroups")
    @ApiOperation("删除存储组")
    public BaseVO deleteStorageGroup(@PathVariable("serverId") Integer serverId, @RequestParam("groupName") String groupName) {
        Connection connection = connectionService.getById(serverId);
        iotDBService.deleteStorageGroup(connection, groupName);
        return new BaseVO(200, "成功", null);
    }
}
