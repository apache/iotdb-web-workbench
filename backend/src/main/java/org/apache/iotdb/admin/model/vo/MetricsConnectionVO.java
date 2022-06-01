package org.apache.iotdb.admin.model.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Erickin
 * @create 2022-04-25-上午 9:12
 */
@Data
public class MetricsConnectionVO implements Serializable {
  Integer id;
  String name;
}
