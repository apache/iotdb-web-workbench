package org.apache.iotdb.admin.model.vo;

import lombok.Data;

/**
 * @author Erickin
 * @create 2022-04-25-上午 9:38
 */
@Data
public class MetricsDataCountVO {
  private Integer serverId;
  private Boolean status;
  private String url;
  private Integer port;
  private Integer storageGroupCount;
  private Integer deviceCount;
  private Integer monitorCount;
  private Integer dataCount;
  private String version;
}
