package org.apache.iotdb.admin.model.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Erickin
 * @create 2022-04-25-下午 3:56
 */
@Data
public class QueryData1VO extends QueryDataVO implements Serializable {
  private Integer precompiledTime;
  private Integer optimizedTime;
}
