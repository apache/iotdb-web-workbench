package org.apache.iotdb.admin.model.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Erickin
 * @create 2022-04-26-上午 10:12
 */
@Data
public class MetricsDataForDiagramVO implements Serializable {
  private Integer serverId;
  private Integer metricId;
  private MetricsChartDataVO chartData;
}
