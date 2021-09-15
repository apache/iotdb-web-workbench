package org.apache.iotdb.admin.model.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class PathVO implements Serializable {
  private String groupName;
  private String deviceName;
  private String timeseriesName;
}
