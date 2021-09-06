package org.apache.iotdb.admin.model.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class DataCountVO implements Serializable {
    private Integer groupCount;
    private Integer deviceCount;
    private Integer measurementCount;
    private Integer dataCount;
}
