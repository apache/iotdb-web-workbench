package org.apache.iotdb.admin.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class DeviceInfoVO implements Serializable {
  private List<DeviceInfo> deviceInfos;
  private Integer totalCount;
  private Integer totalPage;
}
