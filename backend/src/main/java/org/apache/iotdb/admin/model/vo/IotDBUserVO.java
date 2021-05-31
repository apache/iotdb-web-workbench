package org.apache.iotdb.admin.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @anthor fyx 2021/5/28
 */
@Data
public class IotDBUserVO implements Serializable {
    private String userName;
    private String password;
    private List<RoleWithPrivileges> roleWithPrivileges;
}
