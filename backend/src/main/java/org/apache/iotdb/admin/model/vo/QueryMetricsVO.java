package org.apache.iotdb.admin.model.vo;

import lombok.Data;

/**
 * @author Erickin
 * @create 2022-04-26-下午 9:20
 */
@Data
public class QueryMetricsVO {
  private String SQLStatement;
  private String runningTime;
  private Integer executionTime;
}
