package org.apache.iotdb.admin.model.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @anthor fyx 2021/5/28
 */
@Data
public class RoleWithPrivilegesVO implements Serializable {
    private String role;
    private String privilege;
}
