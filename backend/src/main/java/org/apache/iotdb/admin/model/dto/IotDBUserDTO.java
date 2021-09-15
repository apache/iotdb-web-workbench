package org.apache.iotdb.admin.model.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class IotDBUserDTO implements Serializable {
  private List<PrivilegeInfoDTO> privilegesInfos;
}
