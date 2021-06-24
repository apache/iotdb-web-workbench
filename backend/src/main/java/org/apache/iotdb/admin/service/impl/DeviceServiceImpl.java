package org.apache.iotdb.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.iotdb.admin.common.exception.BaseException;
import org.apache.iotdb.admin.common.exception.ErrorCode;
import org.apache.iotdb.admin.mapper.DeviceMapper;
import org.apache.iotdb.admin.model.dto.DeviceDTO;
import org.apache.iotdb.admin.model.entity.Connection;
import org.apache.iotdb.admin.model.entity.Device;
import org.apache.iotdb.admin.service.DeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @anthor fyx 2021/6/16
 */
@Service
public class DeviceServiceImpl extends ServiceImpl<DeviceMapper, Device> implements DeviceService {


    @Autowired
    private DeviceMapper deviceMapper;

    @Override
    public List<Device> getDevices(Integer serverId, List<String> deviceNames) {
        List<Device> devices = new ArrayList<>();
        for (String deviceName : deviceNames) {
            QueryWrapper queryWrapper = new QueryWrapper();
            queryWrapper.eq("connection_id",serverId);
            queryWrapper.eq("device_name",deviceName);
            Device device = deviceMapper.selectOne(queryWrapper);
            devices.add(device);
        }
        return devices;
    }

    @Override
    public void deleteDeviceInfo(Integer serverId, String groupName) throws BaseException {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("connection_id",serverId);
        queryWrapper.like("device_name",groupName);
        int flag = deviceMapper.delete(queryWrapper);
        if (flag <= 0) {
            throw new BaseException(ErrorCode.DELETE_DEVICE_INFO_FAIL,ErrorCode.DELETE_DEVICE_INFO_FAIL_MSG);
        }
    }

    @Override
    public void deleteDeviceInfoByDeviceName(Integer serverId, String deviceName) throws BaseException {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("connection_id",serverId);
        queryWrapper.eq("device_name",deviceName);
        int flag = deviceMapper.delete(queryWrapper);
        if (flag <= 0) {
            throw new BaseException(ErrorCode.DELETE_DEVICE_INFO_FAIL,ErrorCode.DELETE_DEVICE_INFO_FAIL_MSG);
        }
    }

    @Override
    public void setDeviceInfo(Connection connection, DeviceDTO deviceDTO) throws BaseException {
        Device device = new Device();
        device.setCreator(connection.getUsername());
        device.setDeviceName(deviceDTO.getDeviceName());
        device.setCreateTime(System.currentTimeMillis());
        device.setConnectionId(connection.getId());
        device.setDescription(deviceDTO.getDescription());
        int flag = deviceMapper.insert(device);
        if (flag <= 0) {
            throw new BaseException(ErrorCode.SET_DEVICE_INFO_FAIL,ErrorCode.SET_DEVICE_INFO_FAIL_MSG);
        }
    }
}
