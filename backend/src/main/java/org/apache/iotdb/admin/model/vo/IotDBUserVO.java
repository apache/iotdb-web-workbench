package org.apache.iotdb.admin.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class IotDBUserVO implements Serializable {
  private String userName;
  private String password;
  private List<PrivilegeInfo> privilegesInfo;
  //    private List<RoleWithPrivilegesVO> roleWithPrivileges;
}
