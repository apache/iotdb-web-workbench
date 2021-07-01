package org.apache.iotdb.admin.service;

import org.apache.iotdb.admin.common.exception.BaseException;
import org.apache.iotdb.admin.model.entity.Query;
import org.apache.iotdb.admin.model.vo.QueryVO;

import java.util.List;

/**
 * @anthor fyx 2021/7/1
 */
public interface QueryService {
    void save(Integer serverId, Query query) throws BaseException;

    void update(Integer serverId, Query query) throws BaseException;

    List<QueryVO> getQueryList(Integer serverId);

    void deleteQuery(Integer queryId) throws BaseException;

    Query getQuery(Integer queryId) throws BaseException;
}
