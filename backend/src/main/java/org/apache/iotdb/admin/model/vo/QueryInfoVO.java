package org.apache.iotdb.admin.model.vo;

import lombok.Data;

import java.util.List;

/**
 * @author Erickin
 * @create 2022-04-25-下午 5:13
 */
@Data
public class QueryInfoVO {
  private Integer queryClassificationId;
  private String latestRunningTime;
  private Integer totalCount;
  private Integer totalPage;
  private Integer serverId;
  private List<QueryDataStrVO> filteredQueryDataStrVOSList;
}
