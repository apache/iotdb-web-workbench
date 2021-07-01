package org.apache.iotdb.admin.model.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @anthor fyx 2021/7/1
 */
@Data
public class QueryVO implements Serializable {
    private Integer id;
    private String queryName;
}
