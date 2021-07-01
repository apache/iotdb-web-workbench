package org.apache.iotdb.admin.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 返回iotdb用户的信息及其角色权限
 */
@Data
public class IotDBUserVO implements Serializable {
    private String userName;
    private String password;
    private List<PrivilegeInfo> privilegesInfo;
//    private List<RoleWithPrivilegesVO> roleWithPrivileges;
}
