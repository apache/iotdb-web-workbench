package org.apache.iotdb.admin.model.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Erickin
 * @create 2022-04-26-下午 5:26
 */
@Data
public class JVMMetricsListDataVO extends MetricsListDataVO implements Serializable {
  private String metricType;
}
