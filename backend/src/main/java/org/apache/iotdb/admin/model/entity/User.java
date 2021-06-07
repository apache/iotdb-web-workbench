package org.apache.iotdb.admin.model.entity;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * @anthor fyx 2021/5/24
 */

@Data
public class User implements Serializable {

    private Integer id;

    @Length(min = 4,message = "长度必须大于等于4")
    @Pattern(regexp = "^[^ ]+$",message = "不能包含空格")
    private String name;

    @Length(min = 4,message = "长度必须大于等于4")
    @Pattern(regexp = "^[^ ]+$",message = "不能包含空格")
    private String password;

}
