package org.apache.iotdb.admin.model.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Erickin
 * @create 2022-04-25-下午 8:36
 */
@Data
public class QueryDataVO implements Serializable {
  private Integer id;
  private String statement;
  private Long runningTime;
  private Boolean isSlowQuery;
  private Integer totalTime;
  private Integer analysisTime;
  private Integer executionTime;
  private Integer executionResult;
}
