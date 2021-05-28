package org.apache.iotdb.admin.service;

import org.apache.iotdb.admin.model.entity.Connection;
import org.apache.iotdb.admin.model.vo.IotDBUserVO;

import java.util.List;

/**
 * @anthor fyx 2021/5/27
 */
public interface IotDBService {
    List<String> getAllStorageGroups(Connection connection);

    void saveStorageGroup(Connection connection,String groupName);

    void deleteStorageGroup(Connection connection, String groupName);

    List<String> getDevicesByGroup(Connection connection, String groupName);

    List<String> getMeasurementsByDevice(Connection connection, String deviceName);

    List<String> getIotDBUserList(Connection connection);

    List<String> getIotDBRoleList(Connection connection);

    IotDBUserVO getIotDBUser(Connection connection, String userName);

    void deleteIotDBUser(Connection connection, String userName);

    void deleteIotDBRole(Connection connection, String roleName);
}
