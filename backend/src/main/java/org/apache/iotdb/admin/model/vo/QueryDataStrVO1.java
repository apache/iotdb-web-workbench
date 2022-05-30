package org.apache.iotdb.admin.model.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Erickin
 * @create 2022-04-29-下午 2:10
 */
@Data
public class QueryDataStrVO1 extends QueryDataStrVO implements Serializable {
  private Integer precompiledTime;
  private Integer optimizedTime;
}
