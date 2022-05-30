package org.apache.iotdb.admin.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupInfo implements Serializable {
  private String groupName;
  private Integer deviceCount;
  private String description;
}
