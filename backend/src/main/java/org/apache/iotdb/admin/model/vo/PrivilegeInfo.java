package org.apache.iotdb.admin.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @anthor fyx 2021/6/29
 */
@Data
public class PrivilegeInfo implements Serializable {
    // 0 1 2 3 对应 root、storageGroup、device、timeseries
    private Integer type;
    private List<String> groupPaths;
    private List<String> devicePaths;
    private List<String> timeseriesPaths;
    private List<String> privileges;
    private List<String> allTimeseriesPaths;
    private List<String> allDevicePaths;
    private List<String> allGroupPaths;
}
