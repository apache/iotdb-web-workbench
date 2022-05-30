package org.apache.iotdb.admin.model.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Erickin
 * @create 2022-04-28-下午 10:02
 */
@Data
public class QueryDataStrVO implements Serializable {
  private Integer id;
  private String statement;
  private String runningTime;
  private Boolean isSlowQuery;
  private Integer totalTime;
  private Integer analysisTime;
  private Integer executionTime;
  private Integer executionResult;
}
