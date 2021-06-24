package org.apache.iotdb.admin.service;

import org.apache.iotdb.admin.common.exception.BaseException;
import org.apache.iotdb.admin.model.dto.DeviceDTO;
import org.apache.iotdb.admin.model.entity.Connection;
import org.apache.iotdb.admin.model.entity.Device;

import java.util.List;

/**
 * @anthor fyx 2021/6/16
 */
public interface DeviceService  {
    List<Device> getDevices(Integer serverId, List<String> deviceNames);

    void deleteDeviceInfo(Integer serverId, String groupName) throws BaseException;

    void deleteDeviceInfoByDeviceName(Integer serverId, String deviceName) throws BaseException;

    void setDeviceInfo(Connection connection, DeviceDTO deviceDTO) throws BaseException;
}
