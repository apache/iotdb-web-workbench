package org.apache.iotdb.admin.model.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class GroupVO implements Serializable {
  private String groupName;
  private String alias;
  private String description;
  private String creator;
  private String createTime;
  private String ttl;
  private String ttiUnit;
}
