/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.iotdb.admin.service.impl;

import org.apache.iotdb.admin.common.exception.BaseException;
import org.apache.iotdb.admin.common.exception.ErrorCode;
import org.apache.iotdb.admin.mapper.DeviceMapper;
import org.apache.iotdb.admin.model.dto.DeviceInfoDTO;
import org.apache.iotdb.admin.model.entity.Connection;
import org.apache.iotdb.admin.model.entity.Device;
import org.apache.iotdb.admin.model.vo.DeviceVO;
import org.apache.iotdb.admin.service.DeviceService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class DeviceServiceImpl extends ServiceImpl<DeviceMapper, Device> implements DeviceService {

  @Autowired private DeviceMapper deviceMapper;

  @Override
  public List<Device> getDevices(String host, List<String> deviceNames) {
    List<Device> devices = new ArrayList<>();
    for (String deviceName : deviceNames) {
      QueryWrapper queryWrapper = new QueryWrapper();
      queryWrapper.eq("host", host);
      queryWrapper.eq("device_name", deviceName);
      Device device = deviceMapper.selectOne(queryWrapper);
      devices.add(device);
    }
    return devices;
  }

  @Override
  public void deleteDeviceInfo(String host, String groupName) throws BaseException {
    QueryWrapper queryWrapper = new QueryWrapper();
    queryWrapper.eq("host", host);
    queryWrapper.like("device_name", groupName);
    try {
      deviceMapper.delete(queryWrapper);
    } catch (Exception e) {
      throw new BaseException(
          ErrorCode.DELETE_DEVICE_INFO_FAIL, ErrorCode.DELETE_DEVICE_INFO_FAIL_MSG);
    }
  }

  @Override
  public void deleteDeviceInfoByDeviceName(String host, String deviceName) throws BaseException {
    QueryWrapper queryWrapper = new QueryWrapper();
    queryWrapper.eq("host", host);
    queryWrapper.eq("device_name", deviceName);
    try {
      deviceMapper.delete(queryWrapper);
    } catch (Exception e) {
      throw new BaseException(
          ErrorCode.DELETE_DEVICE_INFO_FAIL, ErrorCode.DELETE_DEVICE_INFO_FAIL_MSG);
    }
  }

  @Override
  public void setDeviceInfo(Connection connection, DeviceInfoDTO deviceInfoDTO)
      throws BaseException {
    QueryWrapper queryWrapper = new QueryWrapper();
    queryWrapper.eq("host", connection.getHost());
    queryWrapper.eq("device_name", deviceInfoDTO.getDeviceName());
    Device existDevice = deviceMapper.selectOne(queryWrapper);
    if (existDevice == null) {
      Device device = new Device();
      device.setCreator(connection.getUsername());
      device.setDeviceName(deviceInfoDTO.getDeviceName());
      device.setCreateTime(System.currentTimeMillis());
      device.setHost(connection.getHost());
      device.setDescription(deviceInfoDTO.getDescription());
      int flag = deviceMapper.insert(device);
      if (flag <= 0) {
        throw new BaseException(ErrorCode.SET_DEVICE_INFO_FAIL, ErrorCode.SET_DEVICE_INFO_FAIL_MSG);
      }
      return;
    }
    existDevice.setDescription(deviceInfoDTO.getDescription());
    int flag = deviceMapper.updateById(existDevice);
    if (flag <= 0) {
      throw new BaseException(ErrorCode.SET_DEVICE_INFO_FAIL, ErrorCode.SET_DEVICE_INFO_FAIL_MSG);
    }
  }

  @Override
  public DeviceVO getDevice(String host, String deviceName) {
    QueryWrapper queryWrapper = new QueryWrapper();
    queryWrapper.eq("host", host);
    queryWrapper.eq("device_name", deviceName);
    Device device = deviceMapper.selectOne(queryWrapper);
    // 非系统创建的设备没有设备信息
    DeviceVO deviceVO = new DeviceVO();
    if (device != null) {
      deviceVO.setCreator(device.getCreator());
      deviceVO.setDescription(device.getDescription());
      deviceVO.setDeviceId(device.getId());
      Long createTime = device.getCreateTime();
      Date date = new Date(createTime);
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
      String time = sdf.format(date);
      deviceVO.setTime(time);
      return deviceVO;
    }
    return deviceVO;
  }

  @Override
  public void updateDeviceInfo(DeviceInfoDTO deviceInfoDTO) throws BaseException {
    QueryWrapper queryWrapper = new QueryWrapper();
    queryWrapper.eq("id", deviceInfoDTO.getDeviceId());
    Device existDevice = deviceMapper.selectOne(queryWrapper);
    if (existDevice != null) {
      existDevice.setDescription(deviceInfoDTO.getDescription());
      deviceMapper.updateById(existDevice);
      return;
    }
    throw new BaseException(ErrorCode.NO_DEVICE_INFO, ErrorCode.NO_DEVICE_INFO_MSG);
  }
}
