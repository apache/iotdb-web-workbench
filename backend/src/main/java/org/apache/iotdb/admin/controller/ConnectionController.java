package org.apache.iotdb.admin.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.iotdb.admin.common.exception.BaseException;
import org.apache.iotdb.admin.common.exception.ErrorCode;
import org.apache.iotdb.admin.common.utils.AuthenticationUtils;
import org.apache.iotdb.admin.model.entity.Connection;
import org.apache.iotdb.admin.model.vo.BaseVO;
import org.apache.iotdb.admin.model.vo.ConnVO;
import org.apache.iotdb.admin.model.vo.ConnectionVO;
import org.apache.iotdb.admin.service.ConnectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.util.List;


@RestController
@Api(value = "连接相关接口")
public class ConnectionController {

    @Autowired
    private ConnectionService connectionService;

    @PostMapping("/servers")
    @ApiOperation("保存或更新连接")
    public BaseVO saveOrUpdateConnection(@RequestBody Connection connection, HttpServletRequest request) throws BaseException {
        AuthenticationUtils.userAuthentication(connection.getUserId(),request);
        if (connection.getId() != null){
            connectionService.update(connection);
        }
        connectionService.insert(connection);
        return BaseVO.success("更新成功",null);
    }

    @DeleteMapping("/servers/{serverId}")
    @ApiOperation("删除连接")
    public BaseVO deleteConnection(@PathVariable("serverId") Integer serverId, HttpServletRequest request) throws BaseException {
        Integer userId = AuthenticationUtils.getUserId(request);
        connectionService.check(serverId,userId);
        connectionService.deleteById(serverId,userId);
        return BaseVO.success("删除成功",null);
    }

    @GetMapping("/servers/{serverId}")
    @ApiOperation("获取连接具体配置")
    public BaseVO<Connection> getConnection(@PathVariable("serverId") Integer serverId, HttpServletRequest request) throws BaseException {
        Integer userId = AuthenticationUtils.getUserId(request);
        connectionService.check(serverId,userId);
        // 1. 增加数据源详情内容
        return BaseVO.success("获取成功",connectionService.getById(serverId));
    }

    @GetMapping("/servers")
    @ApiOperation("获取所有连接")
    public BaseVO<ConnectionVO> getAllConnections(@RequestParam("userId") Integer userId, HttpServletRequest request) throws BaseException {
        AuthenticationUtils.userAuthentication(userId,request);
        List<ConnVO> connVOs = connectionService.getAllConnections(userId);
        ConnectionVO connectionVO = new ConnectionVO(connVOs,userId,null);
        return BaseVO.success("获取成功",connectionVO);
    }

    // 4. 连通性测试
    @GetMapping("/test")
    @ApiOperation("连通性测试")
    public BaseVO<ConnectionVO> testConnection(String host) throws BaseException {
        if (host == null || !host.matches("^(2(5[0-5]{1}|[0-4]\\d{1})|[0-1]?\\d{1,2})(\\.(2(5[0-5]{1}|[0-4]\\d{1})|[0-1]?\\d{1,2})){3}$")) {
            throw new BaseException(ErrorCode.TEST_CONN_WRONG,ErrorCode.TEST_CONN_WRONG_MSG);
        }
        try {
            InetAddress address  = InetAddress.getByName(host);
            if (address.isReachable(5000)) {
                return BaseVO.success("连通成功",null);
            }
        } catch (Exception e) {
            throw new BaseException(ErrorCode.TEST_CONN_FAIL,ErrorCode.TEST_CONN_FAIL_MSG);
        }
        throw new BaseException(ErrorCode.TEST_CONN_FAIL,ErrorCode.TEST_CONN_FAIL_MSG);
    }
}
