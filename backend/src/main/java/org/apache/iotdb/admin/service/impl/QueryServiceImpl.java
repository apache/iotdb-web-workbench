package org.apache.iotdb.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.iotdb.admin.common.exception.BaseException;
import org.apache.iotdb.admin.common.exception.ErrorCode;
import org.apache.iotdb.admin.mapper.QueryMapper;
import org.apache.iotdb.admin.model.entity.Query;
import org.apache.iotdb.admin.model.vo.QueryVO;
import org.apache.iotdb.admin.service.QueryService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class QueryServiceImpl extends ServiceImpl<QueryMapper, Query> implements QueryService {

    @Autowired
    private QueryMapper queryMapper;

    @Override
    public void save(Integer serverId, Query inputQuery) throws BaseException {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("connection_id", serverId);
        queryWrapper.eq("query_name", inputQuery.getQueryName());
        Query query = queryMapper.selectOne(queryWrapper);
        if (query == null) {
            Query newQuery = new Query();
            newQuery.setConnectionId(serverId);
            newQuery.setQueryName(inputQuery.getQueryName());
            newQuery.setSqls(inputQuery.getSqls());
            queryMapper.insert(newQuery);
            return;
        }
        throw new BaseException(ErrorCode.QUERY_EXIST, ErrorCode.QUERY_EXIST_MSG);
    }

    @Override
    public void update(Integer serverId, Query inputQuery) throws BaseException {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("id", inputQuery.getId());
        Query query = queryMapper.selectOne(queryWrapper);
        if (query != null) {
            query.setQueryName(inputQuery.getQueryName());
            query.setSqls(inputQuery.getSqls());
            queryMapper.updateById(query);
            return;
        }
        throw new BaseException(ErrorCode.QUERY_EXIST, ErrorCode.QUERY_EXIST_MSG);
    }

    @Override
    public List<QueryVO> getQueryList(Integer serverId) {
        QueryWrapper<Query> queryWrapper = new QueryWrapper();
        queryWrapper.eq("connection_id", serverId);
        List<Query> queries = queryMapper.selectList(queryWrapper);
        List<QueryVO> queryVOList = new ArrayList<>();
        for (Query query : queries) {
            QueryVO queryVO = new QueryVO();
            BeanUtils.copyProperties(query, queryVO);
            queryVOList.add(queryVO);
        }
        return queryVOList;
    }

    @Override
    public void deleteQuery(Integer queryId) throws BaseException {
        QueryWrapper<Query> queryWrapper = new QueryWrapper();
        queryWrapper.eq("id", queryId);
        int flag = queryMapper.delete(queryWrapper);
        if (flag <= 0) {
            throw new BaseException(ErrorCode.QUERY_NOT_EXIST, ErrorCode.QUERY_NOT_EXIST_MSG);
        }
    }

    @Override
    public Query getQuery(Integer queryId) throws BaseException {
        QueryWrapper<Query> queryWrapper = new QueryWrapper();
        queryWrapper.eq("id", queryId);
        Query query = queryMapper.selectOne(queryWrapper);
        if (query == null) {
            throw new BaseException(ErrorCode.QUERY_NOT_EXIST, ErrorCode.QUERY_NOT_EXIST_MSG);
        }
        return query;
    }

}
