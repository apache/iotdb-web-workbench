package org.apache.iotdb.admin.model.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

import java.io.Serializable;

@Data
public class QueryDTO implements Serializable {

  @NotNull(message = "未指定脚本名")
  private String queryName;

  private String sqls;
}
