package org.apache.iotdb.admin.model.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @anthor fyx 2021/6/28
 */
@Data
public class DeviceInfo implements Serializable {
    private String deviceName;
    private String description;
    private String creator;
    private Integer line;
    private Integer deviceId;
}
