package org.apache.iotdb.admin.model.vo;

import lombok.Data;

import java.io.Serializable;

/**
 *  角色及其对应权限
 */
@Data
public class RoleWithPrivilegesVO implements Serializable {
    private String role;
    private String privilege;
}
