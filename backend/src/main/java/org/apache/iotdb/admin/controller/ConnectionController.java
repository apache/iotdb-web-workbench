package org.apache.iotdb.admin.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.iotdb.admin.common.exception.BaseException;
import org.apache.iotdb.admin.model.entity.Connection;
import org.apache.iotdb.admin.model.vo.BaseVO;
import org.apache.iotdb.admin.service.ConnectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @anthor fyx 2021/5/25
 */

@RestController
@Api(value = "连接相关接口")
public class ConnectionController {

    @Autowired
    private ConnectionService connectionService;

    @PostMapping("/servers")
    @ApiOperation("保存或更新连接")
    public BaseVO saveOrUpdateConnection(@RequestBody Connection connection) throws BaseException {
        connectionService.insert(connection);
        return new BaseVO(200,"成功",null);
    }

    @DeleteMapping("/servers/{serverId}")
    @ApiOperation("删除连接")
    public BaseVO deleteConnection(@PathVariable("serverId") String serverId) throws BaseException {
        connectionService.deleteById(serverId);
        return new BaseVO(200,"成功",null);
    }

    @GetMapping("/servers/{serverId}")
    @ApiOperation("获取连接具体配置")
    public BaseVO<Connection> getConnection(@PathVariable("serverId") String serverId){
        return new BaseVO(200,"成功",connectionService.getById(serverId));
    }
}
