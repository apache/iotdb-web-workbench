package org.apache.iotdb.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.iotdb.admin.common.exception.BaseException;
import org.apache.iotdb.admin.mapper.ConnectionMapper;
import org.apache.iotdb.admin.model.entity.Connection;
import org.apache.iotdb.admin.model.vo.ConnVO;
import org.apache.iotdb.admin.service.ConnectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @anthor fyx 2021/5/25
 */

@Service
public class ConnectionServiceImpl extends ServiceImpl<ConnectionMapper, Connection> implements ConnectionService {

    @Autowired
    private ConnectionMapper connectionMapper;

    @Override
    public List<ConnVO> getAllConnections(Integer userId) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("user_id",userId);
        List<Connection> connections = connectionMapper.selectList(queryWrapper);
        List<ConnVO> ConnVOs = new ArrayList();
        for (Connection connection : connections) {
            ConnVOs.add(new ConnVO(connection.getId(),connection.getAlias()));
        }
        return ConnVOs;
    }

    @Override
    public void insert(Connection connection) throws BaseException {
        String alias = connection.getAlias();
        Integer userId = connection.getUserId();
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("user_id",userId);
        queryWrapper.eq("alias",alias);
        List list = connectionMapper.selectList(queryWrapper);
        //别名唯一
        if (list != null && list.size() > 0){
            throw new BaseException(1001,"别名重复");
        }
        int flag = connectionMapper.insert(connection);
        if(flag <= 0){
            throw new BaseException(1002,"添加连接失败");
        }
    }

    @Override
    public void deleteById(Integer serverId) throws BaseException {
        int flag = connectionMapper.deleteById(serverId);
        if(flag <= 0){
            throw new BaseException(1003,"删除连接失败");
        }
    }

    @Override
    public Connection getById(Integer serverId) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("id",serverId);
        return connectionMapper.selectOne(queryWrapper);
    }


}
