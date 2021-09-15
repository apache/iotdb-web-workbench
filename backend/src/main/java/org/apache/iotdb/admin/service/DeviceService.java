package org.apache.iotdb.admin.service;

import org.apache.iotdb.admin.common.exception.BaseException;
import org.apache.iotdb.admin.model.dto.DeviceInfoDTO;
import org.apache.iotdb.admin.model.entity.Connection;
import org.apache.iotdb.admin.model.entity.Device;
import org.apache.iotdb.admin.model.vo.DeviceVO;

import java.util.List;

public interface DeviceService {
  List<Device> getDevices(String host, List<String> deviceNames);

  void deleteDeviceInfo(String host, String groupName) throws BaseException;

  void deleteDeviceInfoByDeviceName(String host, String deviceName) throws BaseException;

  void setDeviceInfo(Connection connection, DeviceInfoDTO deviceInfoDTO) throws BaseException;

  DeviceVO getDevice(String host, String deviceName);

  void updateDeviceInfo(DeviceInfoDTO deviceInfoDTO) throws BaseException;
}
