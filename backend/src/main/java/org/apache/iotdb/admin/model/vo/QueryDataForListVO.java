package org.apache.iotdb.admin.model.vo;

import lombok.Data;

import java.util.List;

/**
 * @author Erickin
 * @create 2022-04-26-下午 9:22
 */
@Data
public class QueryDataForListVO {
  private Integer serverId;
  private Integer mode;
  private List<QueryMetricsVO> queryMetricsVOs;
}
