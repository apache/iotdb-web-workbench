package org.apache.iotdb.admin.service;

import org.apache.iotdb.admin.common.exception.BaseException;
import org.apache.iotdb.admin.model.dto.IotDBRole;
import org.apache.iotdb.admin.model.dto.IotDBUser;
import org.apache.iotdb.admin.model.dto.Timeseries;
import org.apache.iotdb.admin.model.entity.Connection;
import org.apache.iotdb.admin.model.vo.IotDBUserVO;
import org.apache.iotdb.admin.model.vo.SqlResultVO;

import java.util.List;

/**
 * @anthor fyx 2021/5/27
 */
public interface IotDBService {
    List<String> getAllStorageGroups(Connection connection) throws BaseException;

    void saveStorageGroup(Connection connection,String groupName) throws BaseException;

    void deleteStorageGroup(Connection connection, String groupName) throws BaseException;

    List<String> getDevicesByGroup(Connection connection, String groupName) throws BaseException;

    List<String> getMeasurementsByDevice(Connection connection, String deviceName) throws BaseException;

    List<String> getIotDBUserList(Connection connection) throws BaseException;

    List<String> getIotDBRoleList(Connection connection) throws BaseException;

    IotDBUserVO getIotDBUser(Connection connection, String userName) throws BaseException;

    void deleteIotDBUser(Connection connection, String userName) throws BaseException;

    void deleteIotDBRole(Connection connection, String roleName) throws BaseException;

    void setIotDBUser(Connection connection, IotDBUser iotDBUserVO) throws BaseException;

    void setIotDBRole(Connection connection, IotDBRole iotDBRole) throws BaseException;

    SqlResultVO query(Connection connection, String sql) throws BaseException;

    void insertTimeseries(Connection connection, String deviceName, Timeseries timeseries) throws BaseException;
}
