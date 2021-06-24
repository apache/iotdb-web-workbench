package org.apache.iotdb.admin.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @anthor fyx 2021/6/23
 */
@Data
public class DeviceInfoVO implements Serializable {
    private List<String> deviceNames;
    private List<String> descriptions;
    private List<String> creators;
    private Integer totalCount;
    private Integer totalPage;
    private List<Integer> lines;
}
