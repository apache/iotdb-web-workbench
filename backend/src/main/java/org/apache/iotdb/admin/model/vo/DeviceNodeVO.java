package org.apache.iotdb.admin.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class DeviceNodeVO implements Serializable {
    private String deviceName;
    private List<DeviceNodeVO> deviceChildren;

    public DeviceNodeVO(String deviceName) {
        this.deviceName = deviceName;
    }

    public List<DeviceNodeVO> initDeviceChildren() {
        if (deviceChildren == null) {
            deviceChildren = new ArrayList<>();
        }
        return deviceChildren;
    }
}
