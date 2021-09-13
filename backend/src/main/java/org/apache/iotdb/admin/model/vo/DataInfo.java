package org.apache.iotdb.admin.model.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class DataInfo implements Serializable {
    private String dataType;

    private String newValue;

    private Integer dataCount;
}
