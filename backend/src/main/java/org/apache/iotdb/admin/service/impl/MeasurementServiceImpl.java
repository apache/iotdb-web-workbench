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
import org.apache.iotdb.admin.mapper.MeasurementMapper;
import org.apache.iotdb.admin.model.dto.DeviceDTO;
import org.apache.iotdb.admin.model.dto.DeviceInfoDTO;
import org.apache.iotdb.admin.model.entity.Measurement;
import org.apache.iotdb.admin.service.MeasurementService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MeasurementServiceImpl extends ServiceImpl<MeasurementMapper, Measurement>
    implements MeasurementService {

  @Autowired private MeasurementMapper measurementMapper;

  private static final Logger logger = LoggerFactory.getLogger(IotDBServiceImpl.class);

  @Override
  public void deleteMeasurementInfo(String host, String groupName) throws BaseException {
    QueryWrapper queryWrapper = new QueryWrapper();
    queryWrapper.eq("host", host);
    queryWrapper.like("measurement_name", groupName);
    try {
      measurementMapper.delete(queryWrapper);
    } catch (Exception e) {
      throw new BaseException(
          ErrorCode.DELETE_MEASUREMENT_INFO_FAIL, ErrorCode.DELETE_MEASUREMENT_INFO_FAIL_MSG);
    }
  }

  @Override
  public void deleteMeasurementInfoByDeviceName(String host, String deviceName)
      throws BaseException {
    QueryWrapper queryWrapper = new QueryWrapper();
    queryWrapper.eq("host", host);
    queryWrapper.like("measurement_name", deviceName);
    try {
      measurementMapper.delete(queryWrapper);
    } catch (Exception e) {
      throw new BaseException(
          ErrorCode.DELETE_MEASUREMENT_INFO_FAIL, ErrorCode.DELETE_MEASUREMENT_INFO_FAIL_MSG);
    }
  }

  @Override
  public void setMeasurementsInfo(String host, DeviceInfoDTO deviceInfoDTO) throws BaseException {
    List<String> descriptions = new ArrayList<>();
    List<String> measurements = new ArrayList<>();
    for (DeviceDTO deviceDTO : deviceInfoDTO.getDeviceDTOList()) {
      descriptions.add(deviceDTO.getDescription());
      measurements.add(deviceDTO.getTimeseries());
    }
    for (int i = 0; i < measurements.size(); i++) {
      Measurement mea = new Measurement();
      mea.setDescription(descriptions.get(i));
      mea.setMeasurementName(measurements.get(i));
      mea.setHost(host);
      int flag = measurementMapper.insert(mea);
      if (flag <= 0) {
        throw new BaseException(
            ErrorCode.SET_MEASUREMENT_INFO_FAIL, ErrorCode.SET_MEASUREMENT_INFO_FAIL_MSG);
      }
    }
  }

  @Override
  public String getDescription(String host, String timeseries) throws BaseException {
    QueryWrapper queryWrapper = new QueryWrapper();
    queryWrapper.eq("host", host);
    queryWrapper.eq("measurement_name", timeseries);
    Measurement measurement = null;
    try {
      measurement = measurementMapper.selectOne(queryWrapper);
    } catch (Exception e) {
      logger.error(e.getMessage());
      throw new BaseException(ErrorCode.GET_MSM_DES_FAIL, ErrorCode.GET_MSM_DES_FAIL_MSG);
    }
    if (measurement != null) {
      return measurement.getDescription();
    }
    return null;
  }

  @Override
  public void updateMeasurementsInfo(String host, DeviceInfoDTO deviceInfoDTO)
      throws BaseException {
    List<String> descriptions = new ArrayList<>();
    List<String> measurements = new ArrayList<>();
    for (DeviceDTO deviceDTO : deviceInfoDTO.getDeviceDTOList()) {
      descriptions.add(deviceDTO.getDescription());
      measurements.add(deviceDTO.getTimeseries());
    }
    for (int i = 0; i < measurements.size(); i++) {
      QueryWrapper queryWrapper = new QueryWrapper();
      queryWrapper.eq("host", host);
      queryWrapper.eq("measurement_name", measurements.get(i));
      Measurement existMeasurement = measurementMapper.selectOne(queryWrapper);
      // Uncreated measurement
      if (existMeasurement == null) {
        Measurement mea = new Measurement();
        mea.setDescription(descriptions.get(i));
        mea.setMeasurementName(measurements.get(i));
        mea.setHost(host);
        int flag = measurementMapper.insert(mea);
        if (flag <= 0) {
          throw new BaseException(
              ErrorCode.SET_MEASUREMENT_INFO_FAIL, ErrorCode.SET_MEASUREMENT_INFO_FAIL_MSG);
        }
        continue;
      }
      // Updated description of created measurement
      existMeasurement.setDescription(descriptions.get(i));
      int flag = measurementMapper.updateById(existMeasurement);
      if (flag <= 0) {
        throw new BaseException(
            ErrorCode.SET_MEASUREMENT_INFO_FAIL, ErrorCode.SET_MEASUREMENT_INFO_FAIL_MSG);
      }
    }
  }
}
