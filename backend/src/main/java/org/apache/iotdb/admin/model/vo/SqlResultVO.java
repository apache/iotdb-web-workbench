package org.apache.iotdb.admin.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @anthor fyx 2021/6/2
 */
@Data
public class SqlResultVO implements Serializable {
    private List<String> metaDataList;
    private List<List<String>> valueList;
}
