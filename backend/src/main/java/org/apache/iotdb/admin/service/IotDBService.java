package org.apache.iotdb.admin.service;

import org.apache.iotdb.admin.common.exception.BaseException;
import org.apache.iotdb.admin.model.dto.*;
import org.apache.iotdb.admin.model.entity.Connection;
import org.apache.iotdb.admin.model.vo.*;

import java.util.List;

public interface IotDBService {
    DataCountVO getDataCount(Connection connection) throws BaseException;

    List<String> getAllStorageGroups(Connection connection) throws BaseException;

    void saveStorageGroup(Connection connection, String groupName) throws BaseException;

    void deleteStorageGroup(Connection connection, String groupName) throws BaseException;

    CountDTO getDevicesByGroup(Connection connection, String groupName, Integer pageSize, Integer pageNum, String keyword) throws BaseException;

    CountDTO getMeasurementsByDevice(Connection connection, String deviceName, Integer pageSize, Integer pageNum, String keyword) throws BaseException;

    List<String> getIotDBUserList(Connection connection) throws BaseException;

    List<String> getIotDBRoleList(Connection connection) throws BaseException;

    RoleVO getIotDBRoleInfo(Connection connection, String roleName) throws BaseException;

    IotDBUserVO getIotDBUser(Connection connection, String userName) throws BaseException;

    void deleteIotDBUser(Connection connection, String userName) throws BaseException;

    void deleteIotDBRole(Connection connection, String roleName) throws BaseException;

    void setIotDBUser(Connection connection, IotDBUser iotDBUserVO) throws BaseException;

    void setIotDBRole(Connection connection, IotDBRole iotDBRole) throws BaseException;

    UserRolesVO getRolesOfUser(Connection connection, String userName) throws BaseException;

    void userGrant(Connection connection, String userName, UserGrantDTO userGrantDTO) throws BaseException;

    void roleGrant(Connection connection, String roleName, RoleGrantDTO roleGrantDTO) throws BaseException;

    void insertTimeseries(Connection connection, String deviceName, Timeseries timeseries) throws BaseException;

    void deleteTimeseries(Connection connection, String timeseriesName) throws BaseException;

    SqlResultVO showTimeseries(Connection connection, String deviceName) throws BaseException;

    List<Integer> getDevicesCount(Connection connection, List<String> groupNames) throws BaseException;

    void saveGroupTtl(Connection connection, String groupName, long l) throws BaseException;

    void cancelGroupTtl(Connection connection, String groupName) throws BaseException;

    Integer getDeviceCount(Connection connection, String groupName) throws BaseException;

    List<Integer> getTimeseriesCount(Connection connection, List<String> deviceNames) throws BaseException;

    List<String> deleteTimeseriesByDevice(Connection connection, String deviceName) throws BaseException;

    void createDeviceWithMeasurements(Connection connection, DeviceInfoDTO deviceInfoDTO) throws BaseException;

    void upsertMeasurementAlias(Connection connection, List<DeviceDTO> deviceDTOList) throws BaseException;

    void upsertMeasurementTags(Connection connection, List<DeviceDTO> deviceDTOList) throws BaseException;

    void upsertMeasurementAttributes(Connection connection, List<DeviceDTO> deviceDTOList) throws BaseException;

    Integer getMeasurementsCount(Connection connection, String deviceName) throws BaseException;

    Integer getOneDataCount(Connection connection, String deviceName, String measurementName) throws BaseException;

    String getLastMeasurementValue(Connection connection, String timeseries) throws BaseException;

    String getGroupTTL(Connection connection, String groupName) throws BaseException;

    List<String> getDevices(Connection connection, String groupName) throws BaseException;

    DeviceNodeVO getDeviceList(Connection connection, String groupName) throws BaseException;

    Boolean deviceExist(Connection connection, String groupName, String deviceName) throws BaseException;

    List<String> getTimeseries(Connection connection, String deviceName) throws BaseException;

    DataVO getDataByDevice(Connection connection, String deviceName, Integer pageSize, Integer pageNum, DataQueryDTO dataQueryDTO) throws BaseException;

    void updateDataByDevice(Connection connection, String deviceName, DataUpdateDTO dataUpdateDTO) throws BaseException;

    void deleteDataByDevice(Connection connection, String deviceName, DataDeleteDTO dataDeleteDTO) throws BaseException;

    void randomImport(Connection connection, String deviceName, RandomImportDTO randomImportDTO) throws BaseException;

    void setUserPrivileges(Connection connection, String userName, PrivilegeInfoDTO privilegeInfoDTO) throws BaseException;

    RecordVO getRecords(Connection connection, String deviceName, String timeseriesName, String dataType) throws BaseException;

    List<SqlResultVO> queryAll(Connection connection, List<String> sqls, Long timestamp) throws BaseException;

    void updatePwd(Connection connection, IotDBUser iotDBUser) throws BaseException;

    void stopQuery(Integer serverId, Long timestamp) throws BaseException;
}
