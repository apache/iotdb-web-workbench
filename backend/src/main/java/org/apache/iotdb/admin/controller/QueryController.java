package org.apache.iotdb.admin.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.iotdb.admin.common.exception.BaseException;
import org.apache.iotdb.admin.common.exception.ErrorCode;
import org.apache.iotdb.admin.common.utils.AuthenticationUtils;
import org.apache.iotdb.admin.model.dto.SearchDTO;
import org.apache.iotdb.admin.model.entity.Connection;
import org.apache.iotdb.admin.model.entity.Query;
import org.apache.iotdb.admin.model.vo.BaseVO;
import org.apache.iotdb.admin.model.vo.QueryVO;
import org.apache.iotdb.admin.model.vo.SqlResultVO;
import org.apache.iotdb.admin.service.ConnectionService;
import org.apache.iotdb.admin.service.IotDBService;
import org.apache.iotdb.admin.service.QueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @anthor fyx 2021/7/1
 */
@RestController
@Api(value = "查询相关接口")
@RequestMapping("/servers/{serverId}")
public class QueryController {

    @Autowired
    private ConnectionService connectionService;

    @Autowired
    private IotDBService iotDBService;

    @Autowired
    private QueryService queryService;

    @PostMapping("/querySql")
    @ApiOperation("用于查询器查询")
    public BaseVO<SqlResultVO> query(@PathVariable("serverId") Integer serverId,
                                     @RequestBody SearchDTO searchDTO,
                                     HttpServletRequest request) throws BaseException {
        List<String> sqls = searchDTO.getSqls();
        if (sqls == null || sqls.size() == 0) {
            throw new BaseException(ErrorCode.NO_SQL, ErrorCode.NO_SQL_MSG);
        }
        check(request, serverId);
        Connection connection = connectionService.getById(serverId);
        Long timestamp = searchDTO.getTimestamp();
        SqlResultVO sqlResultVO = iotDBService.queryAll(connection, sqls,timestamp);
        return BaseVO.success("查询成功", sqlResultVO);
    }

    @PostMapping("/query")
    @ApiOperation("用于查询脚本保存或编辑")
    public BaseVO saveQuery(@PathVariable("serverId") Integer serverId,
                            @RequestBody Query query,
                            HttpServletRequest request) throws BaseException {
        String sqls = query.getSqls();
        if (sqls == null || "".equals(sqls)) {
            throw new BaseException(ErrorCode.NO_SQL, ErrorCode.NO_SQL_MSG);
        }
        check(request, serverId);
        if (query.getId() != null) {
            queryService.update(serverId,query);
            return BaseVO.success("更新成功", null);
        }
        queryService.save(serverId,query);
        return BaseVO.success("保存成功", null);
    }

    @GetMapping("/query")
    @ApiOperation("获取脚本列表")
    public BaseVO<List<QueryVO>> getQueries(@PathVariable("serverId") Integer serverId,
                              HttpServletRequest request) throws BaseException {
        check(request, serverId);
        List<QueryVO> queryVOList = queryService.getQueryList(serverId);
        return BaseVO.success("获取成功", queryVOList);
    }

    @DeleteMapping("/query/{queryId}")
    @ApiOperation("删除脚本")
    public BaseVO deleteQuery(@PathVariable("serverId") Integer serverId,
                              @PathVariable("queryId") Integer queryId,
                                          HttpServletRequest request) throws BaseException {
        check(request, serverId);
        queryService.deleteQuery(queryId);
        return BaseVO.success("删除成功", null);
    }

    @GetMapping("/query/{queryId}")
    @ApiOperation("获取指定脚本")
    public BaseVO<Query> getQuery(@PathVariable("serverId") Integer serverId,
                              @PathVariable("queryId") Integer queryId,
                              HttpServletRequest request) throws BaseException {
        check(request, serverId);
        Query query = queryService.getQuery(queryId);
        return BaseVO.success("获取成功", query);
    }

    @GetMapping("/stop")
    @ApiOperation("用于查询终止")
    public BaseVO query(@PathVariable("serverId") Integer serverId,@RequestParam("timestamp") Long timestamp, HttpServletRequest request) throws BaseException {
        check(request, serverId);
        iotDBService.stopQuery(serverId,timestamp);
        return BaseVO.success("停止成功", null);
    }

    public void check(HttpServletRequest request, Integer serverId) throws BaseException {
        Integer userId = AuthenticationUtils.getUserId(request);
        connectionService.check(serverId, userId);
    }

}
