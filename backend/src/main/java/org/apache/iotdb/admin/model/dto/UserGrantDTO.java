package org.apache.iotdb.admin.model.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class UserGrantDTO implements Serializable {

  private List<String> roleList;

  private List<String> cancelRoleList;
}
