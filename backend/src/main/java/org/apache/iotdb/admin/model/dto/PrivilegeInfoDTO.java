package org.apache.iotdb.admin.model.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class PrivilegeInfoDTO implements Serializable {
    // 0 1 2 3 对应 root、storageGroup、device、timeseries
    private Integer type;
    private List<String> groupPaths;
    private List<String> devicePaths;
    private List<String> timeseriesPaths;
    private List<String> privileges;
    private List<String> cancelPrivileges;
    private List<String> delGroupPaths;
    private List<String> delDevicePaths;
    private List<String> delTimeseriesPaths;
}
