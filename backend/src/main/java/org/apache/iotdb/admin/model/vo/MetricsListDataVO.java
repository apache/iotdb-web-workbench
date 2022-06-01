package org.apache.iotdb.admin.model.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Erickin
 * @create 2022-04-26-下午 5:24
 */
@Data
public class MetricsListDataVO implements Serializable {
  private String name;
  private String latestScratchTime;
  private String latestResult;
  private Integer detailAvailable;
}
