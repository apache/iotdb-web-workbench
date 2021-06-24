package org.apache.iotdb.admin.model.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @anthor fyx 2021/6/24
 */
@Data
public class MeasurementVO implements Serializable {
    private String timeseries;
    private String dataType;
    private String encoding;
    private String description;
    private String newValue;
}
