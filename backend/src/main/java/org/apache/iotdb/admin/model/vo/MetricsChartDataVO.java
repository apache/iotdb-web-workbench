package org.apache.iotdb.admin.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

/**
 * @author Erickin
 * @create 2022-04-26-上午 10:15
 */
@Data
public class MetricsChartDataVO implements Serializable {
  private List<String> timeList;
  private List<String> metricnameList;
  private List<String> unitList;
  private HashMap<String, List<String>> dataList;
}

// List<String> timeList;
// List<String> metricnameList;
// HashMap<String, List<Integer> dataList;
