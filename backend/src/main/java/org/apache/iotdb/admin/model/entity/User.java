package org.apache.iotdb.admin.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;


@Data
public class User implements Serializable {

    @TableId(type = IdType.AUTO)
    private Integer id;

    @NotBlank
    @Length(min = 4,message = "长度必须大于等于4")
    @Pattern(regexp = "^[^ ]+$",message = "不能包含空格")
    private String name;

    @NotBlank
    @Length(min = 4,message = "长度必须大于等于4")
    @Pattern(regexp = "^[^ ]+$",message = "不能包含空格")
    private String password;

}
