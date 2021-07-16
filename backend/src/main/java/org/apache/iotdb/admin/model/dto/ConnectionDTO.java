package org.apache.iotdb.admin.model.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;


@Data
public class ConnectionDTO implements Serializable {

    @NotBlank(message = "主机地址不能为空或为null")
    @Pattern(regexp = "^((2(5[0-5]{1}|[0-4]\\d{1})|[0-1]?\\d{1,2})(\\.(2(5[0-5]{1}|[0-4]\\d{1})|[0-1]?\\d{1,2})){3})|(localhost)$", message = "主机号输入不合法")
    private String host;

    @NotNull(message = "端口号不能为null")
    @Range(min = 0, max = 65535, message = "端口号输入不合法")
    private Integer port;

    @NotBlank(message = "用户名不能为空或为null")
    @Length(min = 4, message = "长度必须大于等于4")
    @Pattern(regexp = "^[^ ]+$", message = "用户名不能包含空格")
    private String username;

    @NotBlank(message = "密码不能为空或为null")
    @Length(min = 4, message = "长度必须大于等于4")
    @Pattern(regexp = "^[^ ]+$", message = "密码不能包含空格")
    private String password;
}
