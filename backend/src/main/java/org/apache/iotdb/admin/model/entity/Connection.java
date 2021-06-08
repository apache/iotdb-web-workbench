package org.apache.iotdb.admin.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;


@Data
public class Connection implements Serializable {

    @TableId(type = IdType.AUTO)
    private Integer id;

    @NotBlank(message = "不能为空或为null")
    @Pattern(regexp = "^(2(5[0-5]{1}|[0-4]\\d{1})|[0-1]?\\d{1,2})(\\.(2(5[0-5]{1}|[0-4]\\d{1})|[0-1]?\\d{1,2})){3}$",message = "主机号输入不合法")
    private String host;

    @NotNull(message = "不能为null")
    @Range(min = 0,max = 65535,message = "端口号输入不合法")
    private Integer port;

    @NotBlank(message = "不能为空或为null")
    @Length(min = 4,message = "长度必须大于等于4")
    @Pattern(regexp = "^[^ ]+$",message = "不能包含空格")
    private String username;

    @NotBlank(message = "不能为空或为null")
    @Length(min = 4,message = "长度必须大于等于4")
    @Pattern(regexp = "^[^ ]+$",message = "不能包含空格")
    private String password;

    @NotBlank(message = "不能为空或为null")
    @Pattern(regexp = "^[^ ]+$",message = "不能包含空格")
    private String alias;

    @NotNull(message = "不能为null")
    private Integer userId;

}
