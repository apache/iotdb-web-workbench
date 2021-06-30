package org.apache.iotdb.admin.model.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @anthor fyx 2021/6/30
 */
@Data
public class IotDBUserDTO implements Serializable {
    private List<PrivilegeInfoDTO> privilegesInfos;
}
