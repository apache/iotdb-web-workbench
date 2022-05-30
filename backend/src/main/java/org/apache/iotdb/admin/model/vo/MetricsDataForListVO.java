package org.apache.iotdb.admin.model.vo;

import lombok.Data;

import java.util.List;

/**
 * @author Erickin
 * @create 2022-04-26-下午 8:12
 */
@Data
public class MetricsDataForListVO {
  private Integer serverId;
  private Integer metricsType;
  private List<MetricsListDataVO> listInfo;
}
