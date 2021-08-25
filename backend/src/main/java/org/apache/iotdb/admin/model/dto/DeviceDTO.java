package org.apache.iotdb.admin.model.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class DeviceDTO implements Serializable {

    private String timeseries;

    private String dataType;

    private String encoding;

    private String description;
}
