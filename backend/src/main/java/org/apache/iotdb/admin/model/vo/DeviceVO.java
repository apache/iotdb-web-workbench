package org.apache.iotdb.admin.model.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class DeviceVO implements Serializable {
  private String description;
  private String creator;
  private String time;
  private Integer deviceId;
}
