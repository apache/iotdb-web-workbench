package org.apache.iotdb.admin.model.dto;

import lombok.Data;
import org.apache.iotdb.admin.model.vo.RoleWithPrivileges;

import java.io.Serializable;
import java.util.List;

/**
 * @anthor fyx 2021/5/31
 */
@Data
public class IotDBUser implements Serializable {
    private String userName;
    private String password;
    private List<String> privileges;
    private List<String> roles;
}
