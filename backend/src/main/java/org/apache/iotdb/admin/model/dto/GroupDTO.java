package org.apache.iotdb.admin.model.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * @anthor fyx 2021/6/22
 */
@Data
public class GroupDTO implements Serializable {

    @NotNull(message = "存储组名不能为null")
    @Pattern(regexp = "^[^ ]+$",message = "不能包含空格")
    private String groupName;
    private String description;
    private Long ttl;
    private String ttlUnit;

}


