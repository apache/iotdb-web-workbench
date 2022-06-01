package org.apache.iotdb.admin.model.dto;

import org.apache.iotdb.admin.model.vo.QueryDataStrVO;

import lombok.Data;

import java.util.List;

/**
 * @author Erickin
 * @create 2022-04-25-下午 5:12
 */
@Data
public class QueryInfoDTO {
  private Long latestRunningTime;
  private Integer totalCount;
  private Integer totalPage;
  List<QueryDataStrVO> filteredQueryDataStrVOSList;
}
