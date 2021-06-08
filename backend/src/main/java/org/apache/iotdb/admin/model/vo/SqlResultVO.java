package org.apache.iotdb.admin.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * sql查询的元数据列表和数据列表
 */
@Data
public class SqlResultVO implements Serializable {
    private List<String> metaDataList;
    private List<List<String>> valueList;
}
