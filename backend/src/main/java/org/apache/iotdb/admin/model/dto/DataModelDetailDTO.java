package org.apache.iotdb.admin.model.dto;

import org.apache.iotdb.admin.model.vo.DataModelVO;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class DataModelDetailDTO implements Serializable {
  private List<DataModelVO> dataModelVOList;
  private Integer pageNum;
  private Integer pageSize;
  private Integer total;
}
