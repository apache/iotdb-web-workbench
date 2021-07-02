package org.apache.iotdb.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.iotdb.admin.common.exception.BaseException;
import org.apache.iotdb.admin.common.exception.ErrorCode;
import org.apache.iotdb.admin.mapper.MeasurementMapper;
import org.apache.iotdb.admin.model.dto.DeviceDTO;
import org.apache.iotdb.admin.model.dto.DeviceInfoDTO;
import org.apache.iotdb.admin.model.entity.Measurement;
import org.apache.iotdb.admin.service.MeasurementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @anthor fyx 2021/6/16
 */
@Service
public class MeasurementServiceImpl extends ServiceImpl<MeasurementMapper, Measurement> implements MeasurementService {

    @Autowired
    private MeasurementMapper measurementMapper;

    private static final Logger logger = LoggerFactory.getLogger(IotDBServiceImpl.class);

    @Override
    public void deleteMeasurementInfo(Integer serverId, String groupName) throws BaseException {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("connection_id",serverId);
        queryWrapper.like("measurement_name",groupName);
        try {
            measurementMapper.delete(queryWrapper);
        } catch (Exception e) {
            throw new BaseException(ErrorCode.DELETE_MEASUREMENT_INFO_FAIL,ErrorCode.DELETE_MEASUREMENT_INFO_FAIL_MSG);
        }
    }

    @Override
    public void deleteMeasurementInfoByDeviceName(Integer serverId, String deviceName) throws BaseException {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("connection_id",serverId);
        queryWrapper.like("measurement_name",deviceName);
        try {
            measurementMapper.delete(queryWrapper);
        } catch (Exception e) {
            throw new BaseException(ErrorCode.DELETE_MEASUREMENT_INFO_FAIL,ErrorCode.DELETE_MEASUREMENT_INFO_FAIL_MSG);
        }
    }

    @Override
    public void setMeasurementsInfo(Integer serverId, DeviceInfoDTO deviceInfoDTO) throws BaseException {
        List<String> descriptions = new ArrayList<>();
        List<String> measurements = new ArrayList<>();
        for (DeviceDTO deviceDTO : deviceInfoDTO.getDeviceDTOList()) {
            descriptions.add(deviceDTO.getDescription());
            measurements.add(deviceDTO.getMeasurement());
        }
        for (int i = 0; i < measurements.size(); i++) {
            Measurement mea = new Measurement();
            mea.setDescription(descriptions.get(i));
            mea.setMeasurementName(measurements.get(i));
            mea.setConnectionId(serverId);
            int flag = measurementMapper.insert(mea);
            if (flag <= 0) {
                throw new BaseException(ErrorCode.SET_MEASUREMENT_INFO_FAIL,ErrorCode.SET_MEASUREMENT_INFO_FAIL_MSG);
            }
        }
    }

    @Override
    public String getDescription(Integer serverId, String timeseries) throws BaseException {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("connection_id",serverId);
        queryWrapper.eq("measurement_name",timeseries);
        Measurement measurement = null;
        try {
            measurement = measurementMapper.selectOne(queryWrapper);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new BaseException(ErrorCode.GET_MSM_DES_FAIL,ErrorCode.GET_MSM_DES_FAIL_MSG);
        }
        if (measurement != null) {
            return measurement.getDescription();
        }
        return null;
    }

    @Override
    public void updateMeasurementsInfo(Integer serverId, DeviceInfoDTO deviceInfoDTO) throws BaseException {
        List<String> descriptions = new ArrayList<>();
        List<String> measurements = new ArrayList<>();
        for (DeviceDTO deviceDTO : deviceInfoDTO.getDeviceDTOList()) {
            descriptions.add(deviceDTO.getDescription());
            measurements.add(deviceDTO.getMeasurement());
        }
        for (int i = 0; i < measurements.size(); i++) {
            QueryWrapper queryWrapper = new QueryWrapper();
            queryWrapper.eq("connection_id",serverId);
            queryWrapper.eq("measurement_name",measurements.get(i));
            Measurement existMeasurement = measurementMapper.selectOne(queryWrapper);
            // 未创建的测点
            if (existMeasurement == null) {
                Measurement mea = new Measurement();
                mea.setDescription(descriptions.get(i));
                mea.setMeasurementName(measurements.get(i));
                mea.setConnectionId(serverId);
                int flag = measurementMapper.insert(mea);
                if (flag <= 0) {
                    throw new BaseException(ErrorCode.SET_MEASUREMENT_INFO_FAIL,ErrorCode.SET_MEASUREMENT_INFO_FAIL_MSG);
                }
                continue;
            }
            // 已创建的测点更新描述
            existMeasurement.setDescription(descriptions.get(i));
            int flag = measurementMapper.updateById(existMeasurement);
            if (flag <= 0) {
                throw new BaseException(ErrorCode.SET_MEASUREMENT_INFO_FAIL,ErrorCode.SET_MEASUREMENT_INFO_FAIL_MSG);
            }
        }
    }
}
