package org.apache.iotdb.admin.service;

import org.apache.iotdb.admin.common.exception.BaseException;
import org.apache.iotdb.admin.model.dto.GroupDTO;
import org.apache.iotdb.admin.model.entity.Connection;
import org.apache.iotdb.admin.model.entity.StorageGroup;

import java.util.List;

/**
 * @anthor fyx 2021/6/16
 */
public interface GroupService {
    List<String> getGroupDescription(Integer serverId, List<String> groupNames) throws BaseException;

    void setStorageGroupInfo(Connection connection, GroupDTO groupDTO) throws BaseException;

    boolean isExist(Integer serverId, String groupName);

    void deleteGroupInfo(Integer serverId, String groupName) throws BaseException;

    StorageGroup getGroupInfo(Integer serverId, String groupName);
}
