package org.apache.iotdb.admin.service;

import org.apache.iotdb.admin.common.exception.BaseException;
import org.apache.iotdb.admin.model.dto.DeviceInfoDTO;

/**
 * @anthor fyx 2021/6/16
 */
public interface MeasurementService {
    void deleteMeasurementInfo(String host, String groupName) throws BaseException;

    void deleteMeasurementInfoByDeviceName(String host, String deviceName) throws BaseException;

    void setMeasurementsInfo(String host, DeviceInfoDTO deviceInfoDTO) throws BaseException;

    String getDescription(String host, String timeseries) throws BaseException;

    void updateMeasurementsInfo(String host, DeviceInfoDTO deviceInfoDTO) throws BaseException;
}
