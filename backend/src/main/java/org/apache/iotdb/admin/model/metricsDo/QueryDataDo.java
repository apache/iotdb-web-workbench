package org.apache.iotdb.admin.model.metricsDo;

import org.apache.iotdb.admin.model.vo.QueryDataVO;

import lombok.Data;

import java.util.List;

/**
 * @author Erickin
 * @create 2022-04-26-上午 9:28
 */
@Data
public class QueryDataDo {
  private List<QueryDataVO> QueryDataVOs;
  private Long latestTimeStamp;
  private Integer count;
}
