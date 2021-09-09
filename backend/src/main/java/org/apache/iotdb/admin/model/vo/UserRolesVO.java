package org.apache.iotdb.admin.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class UserRolesVO implements Serializable {

    private String password;

    private List<String> roleList;

}
