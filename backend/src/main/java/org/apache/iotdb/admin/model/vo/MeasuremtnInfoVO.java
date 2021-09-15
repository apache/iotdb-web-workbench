package org.apache.iotdb.admin.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class MeasuremtnInfoVO implements Serializable {
  private List<MeasurementVO> measurementVOList;
  private Integer totalCount;
  private Integer totalPage;
}
