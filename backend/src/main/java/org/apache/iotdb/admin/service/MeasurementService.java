package org.apache.iotdb.admin.service;

import org.apache.iotdb.admin.common.exception.BaseException;
import org.apache.iotdb.admin.model.dto.DeviceDTO;

/**
 * @anthor fyx 2021/6/16
 */
public interface MeasurementService {
    void deleteMeasurementInfo(Integer serverId, String groupName) throws BaseException;

    void deleteMeasurementInfoByDeviceName(Integer serverId, String deviceName) throws BaseException;

    void setMeasurementsInfo(Integer serverId, DeviceDTO deviceDTO) throws BaseException;
}
