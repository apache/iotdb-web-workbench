package org.apache.iotdb.admin.service;

import org.apache.iotdb.admin.model.entity.Connection;

import java.util.List;

/**
 * @anthor fyx 2021/5/27
 */
public interface IotDBService {
    List<String> getAllStorageGroups(Connection connection);

    void saveStorageGroup(Connection connection,String groupName);

    void deleteStorageGroup(Connection connection, String groupName);
}
