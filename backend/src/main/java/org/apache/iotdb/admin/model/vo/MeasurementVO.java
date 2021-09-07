package org.apache.iotdb.admin.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

@Data
public class MeasurementVO implements Serializable {
    private String timeseries;
    private String dataType;
    private String encoding;
    private String description;
    private String newValue;
    private Integer dataCount;
    private String alias;
    private String compression;
    private Map<String,String> tags;
    private Map<String,String> attributes;
}
