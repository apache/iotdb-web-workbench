package org.apache.iotdb.admin.model.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class QueryVO implements Serializable {
    private Integer id;
    private String queryName;
}
