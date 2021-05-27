package org.apache.iotdb.admin.service;


import org.apache.iotdb.admin.common.exception.BaseException;
import org.apache.iotdb.admin.model.entity.Connection;
import org.apache.iotdb.admin.model.vo.ConnVO;

import java.util.List;

/**
 * @anthor fyx 2021/5/25
 */
public interface ConnectionService {
    List<ConnVO> getAllConnections(Integer id);


    void insert(Connection connection) throws BaseException;

    void deleteById(Integer serverId) throws BaseException;

    Connection getById(Integer serverId);
}
