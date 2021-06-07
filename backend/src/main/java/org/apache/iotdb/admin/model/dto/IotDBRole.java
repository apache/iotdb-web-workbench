package org.apache.iotdb.admin.model.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;
import java.util.List;

/**
 * @anthor fyx 2021/5/31
 */
@Data
public class IotDBRole implements Serializable {

    @Length(min = 4,message = "长度必须大于等于4")
    private String roleName;

    private List<String> privileges;

}
