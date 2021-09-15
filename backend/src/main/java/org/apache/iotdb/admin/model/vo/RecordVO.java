package org.apache.iotdb.admin.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
public class RecordVO implements Serializable {
  private List<Date> timeList;

  private List<String> valueList;

  private Map<String, Integer> textCount;
}
