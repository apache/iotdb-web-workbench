package org.apache.iotdb.admin.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/** 查询设备下的物理量数据时返回的数据 */
@Data
public class DataVO implements Serializable {
  private List<String> metaDataList;
  private List<List<String>> valueList;
  private List<String> typeList;
  private Integer totalCount;
  private Integer totalPage;
}
