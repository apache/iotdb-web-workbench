package org.apache.iotdb.admin.model.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class MeasurementDTO implements Serializable {
    private String timeseries;
    private String alias;
    private String storagegroup;
    private String dataType;
    private String encoding;
    private String compression;
    private String tags;
    private String attributes;
}
