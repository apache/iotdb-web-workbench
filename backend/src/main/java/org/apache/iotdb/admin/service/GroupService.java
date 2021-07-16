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
    List<String> getGroupDescription(String host, List<String> groupNames) throws BaseException;

    void setStorageGroupInfo(Connection connection, GroupDTO groupDTO) throws BaseException;

    boolean isExist(String host, String groupName);

    void deleteGroupInfo(String host, String groupName) throws BaseException;

    StorageGroup getGroupInfo(String host, String groupName);

    void updateStorageGroupInfo(Connection connection, GroupDTO groupDTO) throws BaseException;

    Integer getGroupId(String host, String groupName);
}
