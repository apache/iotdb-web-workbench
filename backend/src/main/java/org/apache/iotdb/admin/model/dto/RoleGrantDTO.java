package org.apache.iotdb.admin.model.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class RoleGrantDTO implements Serializable {

    private List<String> UserList;

    private List<String> cancelUserList;

}
